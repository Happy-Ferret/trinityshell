package org.trinity.foundation.api.render.binding;

import com.google.common.base.Optional;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.render.binding.view.PropertyAdapter;
import org.trinity.foundation.api.render.binding.view.PropertySlot;
import org.trinity.foundation.api.render.binding.view.delegate.PropertySlotInvocationDelegate;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

public class PropertyBinding implements ViewBinding {

	private static final Logger LOG = LoggerFactory.getLogger(PropertyBinding.class);

	private final Injector                       injector;
	private final PropertySlotInvocationDelegate propertySlotInvocationDelegate;
	private final ViewBindingMeta                viewBindingMeta;
	private final PropertySlot                   propertySlot;

	@Inject
	PropertyBinding(final Injector injector,
					final PropertySlotInvocationDelegate propertySlotInvocationDelegate,
					@Assisted final ViewBindingMeta viewBindingMeta,
					@Assisted final PropertySlot propertySlot) {
		this.injector = injector;
		this.propertySlotInvocationDelegate = propertySlotInvocationDelegate;
		this.viewBindingMeta = viewBindingMeta;
		this.propertySlot = propertySlot;
	}

	@Override
	public ViewBindingMeta getViewBindingMeta() {
		return this.viewBindingMeta;
	}

	@Override
	public Collection<DataModelProperty> bind() {

		final String propertySlotDataModelContext = this.propertySlot.dataModelContext();
		final LinkedList<DataModelProperty> dataModelProperties = new LinkedList<>();

		//resolve the data model context of this view binding meta
		boolean resolvable = this.viewBindingMeta.resolveDataModelChain(dataModelProperties);
		if(resolvable) {
			//resolve the data model context of our specific property slot
			resolvable = this.viewBindingMeta.appendDataModelPropertyChain(dataModelProperties,
																		   propertySlotDataModelContext);
		}

		if(resolvable) {

			//construct the property that we will use to update the view

			final DataModelProperty parentDataModelProperty = dataModelProperties.getLast();
			final Optional<Object> parentDataModelPropertyValue = parentDataModelProperty.getPropertyValue();

			if(parentDataModelPropertyValue.isPresent()) {
				final String propertyName = this.propertySlot.propertyName();
				final DataModelProperty dataModelProperty = new RelativeDataModelProperty(parentDataModelPropertyValue.get(),
																						  propertyName);
				dataModelProperties.add(dataModelProperty);
				final Optional<Object> propertyValue = dataModelProperty.getPropertyValue();

				//if the actual property has a value (ie not null), then invoke the view method.
				if(propertyValue.isPresent()) {
					invokeViewPropertySlot(propertyValue.get());
				}
			}
		}

		return dataModelProperties;
	}

	private void invokeViewPropertySlot(final Object propertyValue) {
		try {
			final Object viewModel = this.viewBindingMeta.getViewModel();
			final String viewModelMethodName = this.propertySlot.methodName();
			final Class<?>[] viewMethodArgumentTypes = this.propertySlot.argumentTypes();
			final Method targetViewMethod = viewModel.getClass().getMethod(viewModelMethodName,
																		   viewMethodArgumentTypes);
			final Class<? extends PropertyAdapter<?>> propertyAdapterType = this.propertySlot.adapter();
			final PropertyAdapter propertyAdapter = this.injector.getInstance(propertyAdapterType);

			final Object argument = propertyAdapter.adapt(propertyValue);

			this.propertySlotInvocationDelegate.invoke(viewModel,
													   targetViewMethod,
													   argument);
		}
		catch(final NoSuchMethodException | SecurityException e) {
			// TODO explanation
			LOG.error("",
					  e);
		}
	}

	@Override
	public void unbind() {
		//property binding is stateless, nothing to do here.
	}
}