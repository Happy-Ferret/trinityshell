package org.trinity.foundation.api.render.binding;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import org.trinity.foundation.api.render.binding.view.EventSignal;
import org.trinity.foundation.api.render.binding.view.EventSignalFilter;
import org.trinity.foundation.api.render.binding.view.delegate.Signal;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;

public class EventBinding implements ViewBinding {
	private final Injector                 injector;
	private final SignalFactory            signalFactory;
	private final ListeningExecutorService dataModelExecutor;
	private final ViewBindingMeta          viewBindingMeta;
	private final EventSignal              eventSignal;

	private Optional<Signal> optionalSignal = Optional.absent();

	@Inject
	EventBinding(final Injector injector,
				 final SignalFactory signalFactory,
				 @Assisted final ListeningExecutorService dataModelExecutor,
				 @Assisted final ViewBindingMeta viewBindingMeta,
				 @Assisted final EventSignal eventSignal) {
		this.injector = injector;
		this.signalFactory = signalFactory;
		this.dataModelExecutor = dataModelExecutor;
		this.viewBindingMeta = viewBindingMeta;
		this.eventSignal = eventSignal;
	}

	@Override
	public ViewBindingMeta getViewBindingMeta() {
		return this.viewBindingMeta;
	}

    @Override
    public Collection<DataModelProperty> bind() {

        final LinkedList<DataModelProperty> properties = new LinkedList<>();
        final boolean commonDataModelContextResolved = this.viewBindingMeta.resolveDataModelChain(properties);

        if (!commonDataModelContextResolved) {
            return properties;
        }

        final String collectionDataModelContext = this.eventSignal.dataModelContext();
        final boolean dataModelContextResolved = this.viewBindingMeta.appendDataModelPropertyChain(properties,
                collectionDataModelContext);

        if (!dataModelContextResolved) {
            return properties;
        }

        final Optional<Object> lastPropertyValue = properties.getLast().getPropertyValue();

        if (lastPropertyValue.isPresent()) {
            bindEventSignal(lastPropertyValue.get());
        }

        return properties;
    }

    private void bindEventSignal(final Object eventReceiver) {
        final EventSignalFilter eventSignalFilter = this.injector.getInstance(this.eventSignal.filter());
		final Signal signal = this.signalFactory.createSignal(this.dataModelExecutor,
															  eventReceiver,
															  this.eventSignal.name());
		this.optionalSignal = Optional.of(signal);
		eventSignalFilter.installFilter(this.viewBindingMeta.getViewModel(),
										signal);
	}

	@Override
	public void unbind() {
        if(this.optionalSignal.isPresent()){
			final EventSignalFilter eventSignalFilter = this.injector.getInstance(this.eventSignal.filter());
			eventSignalFilter.uninstallFilter(this.viewBindingMeta.getViewModel(),
											  this.optionalSignal.get());
		}
	}
}
