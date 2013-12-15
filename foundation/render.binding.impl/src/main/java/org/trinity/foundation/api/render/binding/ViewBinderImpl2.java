package org.trinity.foundation.api.render.binding;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.render.binding.view.EventSignal;
import org.trinity.foundation.api.render.binding.view.PropertySlot;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.Callable;

public class ViewBinderImpl2 implements ViewBinder {

    private static final Logger LOG = LoggerFactory.getLogger(ViewBinderImpl2.class);

    private static final Table<Class<?>, String, Optional<Field>> FIELD_CACHE = HashBasedTable.create();

    private final ViewBindingsTraverser viewBindingsTraverser;
    private final CollectionBindingFactory collectionBindingFactory;
    private final EventBindingFactory eventBindingFactory;
    private final PropertyBindingFactory propertyBindingFactory;

    private final Multimap<DataModelProperty, ViewBinding> dataModelPopertyToViewBindings = HashMultimap.create();
    private final Multimap<Object, ViewBindingMeta> viewModelToViewBindingMetas = HashMultimap.create();
    private final Multimap<ViewBindingMeta, ViewBinding> viewBindingMetaToViewBindings = HashMultimap.create();

    @Inject
    ViewBinderImpl2(final ViewBindingsTraverser viewBindingsTraverser,
                    final CollectionBindingFactory collectionBindingFactory,
                    final EventBindingFactory eventBindingFactory,
                    final PropertyBindingFactory propertyBindingFactory) {
        this.viewBindingsTraverser = viewBindingsTraverser;
        this.collectionBindingFactory = collectionBindingFactory;
        this.eventBindingFactory = eventBindingFactory;
        this.propertyBindingFactory = propertyBindingFactory;
    }

    @Override
    public ListenableFuture<Void> updateDataModelBinding(@Nonnull final ListeningExecutorService dataModelExecutor,
                                                         @Nonnull final Object dataModel,
                                                         @Nonnull final String propertyName,
                                                         @Nonnull final Optional<Object> oldPropertyValue,
                                                         @Nonnull final Optional<Object> newPropertyValue) {
        return dataModelExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() {
                updateDataModelBinding( dataModel,
                                        propertyName,
                                        oldPropertyValue,
                                        newPropertyValue);
                return null;
            }
        });
    }

    private void updateDataModelBinding(@Nonnull final Object dataModel,
                                        @Nonnull final String propertyName,
                                        @Nonnull final Optional<Object> oldPropertyValue,
                                        @Nonnull final Optional<Object> newPropertyValue) {
        if (oldPropertyValue.equals(newPropertyValue)) {
            return;
        }

        final DataModelProperty dataModelProperty = new RelativeDataModelProperty(dataModel,
                propertyName);

        //find all impacted view bindings
        final Collection<ViewBinding> viewBindings = this.dataModelPopertyToViewBindings.removeAll(dataModelProperty);

        //find all data model properties that point to the impacted view binding
        final Multimap<ViewBinding, DataModelProperty> viewBindingToDataModelProperties = HashMultimap.create();
        Multimaps.invertFrom(   this.dataModelPopertyToViewBindings,
                                viewBindingToDataModelProperties);

        //for each view binding that was impacted by the changed data model property.
        for (final ViewBinding viewBinding : viewBindings) {

            //unbind the view binding
            viewBinding.unbind();

            //remove all remaining data model properties that were pointing to the view binding
            final Collection<DataModelProperty> dataModelProperties = viewBindingToDataModelProperties.get(viewBinding);
            for (final DataModelProperty oldModelProperty : dataModelProperties) {
                this.dataModelPopertyToViewBindings.remove( oldModelProperty,
                                                            viewBinding);
            }

            //rebind the view & update the data model properties that point to this view model
            bindViewBinding(viewBinding);
        }
    }

    private void bindViewBinding(final ViewBinding viewBinding) {

        //bind the view binding
        final Collection<DataModelProperty> newDataModelProperties = viewBinding.bind();

        //link these new data model properties to this view binding
        for (final DataModelProperty newDataModelProperty : newDataModelProperties) {
            this.dataModelPopertyToViewBindings.put(    newDataModelProperty,
                                                        viewBinding);
        }

        //link the binding description to the created binding
        this.viewBindingMetaToViewBindings.put( viewBinding.getViewBindingMeta(),
                                                viewBinding);
    }

    @Override
    public ListenableFuture<Void> bind(@Nonnull final ListeningExecutorService dataModelExecutor,
                                       @Nonnull final Object dataModel,
                                       @Nonnull final Object viewModel) {
        return dataModelExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                bindViewModelToDataModel(   dataModelExecutor,
                                            dataModel,
                                            viewModel);
                return null;
            }
        });
    }

    @Override
    public ListenableFuture<Void> unbind(@Nonnull final ListeningExecutorService dataModelExecutor,
                                         @Nonnull final Object dataModel,
                                         @Nonnull final Object viewModel) {
        return dataModelExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                unbind(dataModel,
                        viewModel);
                return null;
            }
        });
    }

    private void unbind(@Nonnull final Object dataModel,
                        @Nonnull final Object viewModel) {
        final ViewBindingMeta viewBindingMeta = ViewBindingMeta.create(dataModel,
                viewModel);
        removeAllBindings(viewBindingMeta);
    }

    private void removeAllBindings(final ViewBindingMeta rootBindingMeta) {
        final FluentIterable<ViewBindingMeta> viewBindingMetas = this.viewBindingsTraverser.postOrderTraversal(rootBindingMeta);

        for (final ViewBindingMeta viewBindingMeta : viewBindingMetas) {
            removeBindings(viewBindingMeta);
        }
    }

    private void removeBindings(final ViewBindingMeta viewBindingMeta) {

        final Collection<ViewBinding> viewBindings = this.viewBindingMetaToViewBindings.removeAll(viewBindingMeta);
        for (final ViewBinding viewBinding : viewBindings) {
            viewBinding.unbind();
        }

        this.viewModelToViewBindingMetas.remove(    viewBindingMeta.getViewModel(),
                                                    viewBindingMeta);
    }


    private void bindViewModelToDataModel(  @Nonnull final ListeningExecutorService dataModelExecutor,
                                            @Nonnull final Object dataModel,
                                            @Nonnull final Object viewModel) {
        final ViewBindingMeta viewBindingMeta = ViewBindingMeta.create( dataModel,
                                                                        viewModel);
        createAllBindings(  dataModelExecutor,
                            viewBindingMeta);
    }

    private void createAllBindings(final ListeningExecutorService dataModelExecutor,
                                   final ViewBindingMeta rootViewBindingMeta) {
        final FluentIterable<ViewBindingMeta> viewBindingMetas = this.viewBindingsTraverser.preOrderTraversal(rootViewBindingMeta);

        for (final ViewBindingMeta viewBindingMeta : viewBindingMetas) {
            createBindings( dataModelExecutor,
                            viewBindingMeta);
        }
    }

    private void createBindings(final ListeningExecutorService dataModelExecutor,
                                final ViewBindingMeta bindingMeta) {

        //if there are property slots
        if (bindingMeta.getPropertySlots().isPresent()) {
            for (final PropertySlot propertySlot : bindingMeta.getPropertySlots().get().value()) {
                final PropertyBinding propertyBinding = this.propertyBindingFactory.createPropertyBinding(  bindingMeta,
                                                                                                            propertySlot);
                //bind property binding
                bindViewBinding(propertyBinding);
            }
        }

        //if there is an observable collection
        if (bindingMeta.getObservableCollection().isPresent()) {
            final CollectionBinding collectionBinding = this.collectionBindingFactory.createCollectionBinding(  bindingMeta,
                                                                                                                dataModelExecutor,
                                                                                                                bindingMeta.getObservableCollection().get());
            //bind collection binding
            bindViewBinding(collectionBinding);
        }

        //if this view emits events
        if (bindingMeta.getEventSignals().isPresent()) {
            for (final EventSignal eventSignal: bindingMeta.getEventSignals().get().value()) {
                final EventBinding eventBinding = this.eventBindingFactory.createEventBinding(  dataModelExecutor,
                                                                                                bindingMeta,
                                                                                                eventSignal);
                bindViewBinding(eventBinding);
            }
        }
    }

    @Override
    public ListenableFuture<Void> updateViewModelBinding(@Nonnull final ListeningExecutorService dataModelExecutor,
                                                         @Nonnull final Object parentViewModel,
                                                         @Nonnull final String fieldName,
                                                         @Nonnull final Optional<?> oldSubView,
                                                         @Nonnull final Optional<?> newSubView) {
        return dataModelExecutor.submit(new Callable<Void>() {
            @Override
            public Void call() {
                updateViewModelBindingImpl( dataModelExecutor,
                                            parentViewModel,
                                            fieldName,
                                            oldSubView,
                                            newSubView);
                return null;
            }
        });
    }

    private void updateViewModelBindingImpl(@Nonnull final ListeningExecutorService dataModelExecutor,
                                            @Nonnull final Object parentViewModel,
                                            @Nonnull final String subViewFieldName,
                                            @Nonnull final Optional<?> oldSubView,
                                            @Nonnull final Optional<?> newSubView) {
        if (oldSubView.equals(newSubView)) {
            return;
        }

        final Class<?> parentViewModelClass = parentViewModel.getClass();
        final Optional<Field> field = getField( parentViewModelClass,
                                                subViewFieldName);
        if (!field.isPresent()) {
            LOG.warn(   "Subview field name {} not found on object {}. Not updating subview.",
                        subViewFieldName,
                        parentViewModel);
            return;
        }

        //find all binding descriptions from the parent view
        final Collection<ViewBindingMeta> parentViewBindingMetas = this.viewModelToViewBindingMetas.get(parentViewModel);

        //if an old subview was present
        if (oldSubView.isPresent()) {

            //then for each of the parent binding descriptions
            for (final ViewBindingMeta parentViewBindingMeta : parentViewBindingMetas) {

                //recreate the old subview binding description
                final ViewBindingMeta oldBindingMeta = ViewBindingMeta.create(  parentViewBindingMeta,
                                                                                field.get(),
                                                                                oldSubView.get());

                //use it to find and remove all old view bindings
                removeAllBindings(oldBindingMeta);
            }
        }

        //if the new subview has a value
        if (newSubView.isPresent()) {

            //then for each of the parent binding descriptions
            for (final ViewBindingMeta parentViewBindingMeta : parentViewBindingMetas) {

                //create a new subview binding description
                final ViewBindingMeta newBindingMeta = ViewBindingMeta.create(  parentViewBindingMeta,
                                                                                field.get(),
                                                                                newSubView.get());

                //and create all bindings from this description.
                createAllBindings(  dataModelExecutor,
                                    newBindingMeta);
            }
        }
    }

    private Optional<Field> getField(final Class<?> viewModelClass,
                                     final String subViewFieldName) {
        Optional<Field> fieldOptional = FIELD_CACHE.get(    viewModelClass,
                                                            subViewFieldName);
        if (fieldOptional == null) {

            Field foundField = null;
            try {
                foundField = viewModelClass.getDeclaredField(subViewFieldName);
                foundField.setAccessible(true);
            } catch (final NoSuchFieldException e) {
                // TODO explanation
                LOG.error(  "",
                            e);
            }
            fieldOptional = Optional.fromNullable(foundField);
            FIELD_CACHE.put(    viewModelClass,
                                subViewFieldName,
                                fieldOptional);
        }

        return fieldOptional;
    }
}