/*
 * This file is part of HyperDrive.
 * 
 * HyperDrive is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * HyperDrive is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * HyperDrive. If not, see <http://www.gnu.org/licenses/>.
 */
package org.hyperdrive.foundation.impl;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.hydrogen.display.api.Display;
import org.hydrogen.display.api.PlatformRenderArea;
import org.hydrogen.display.api.event.ConfigureRequestEvent;
import org.hydrogen.display.api.event.DisplayEvent;
import org.hydrogen.display.api.event.DisplayEventSource;
import org.hydrogen.display.api.event.DisplayEventType;
import org.hydrogen.display.api.event.MapRequestEvent;
import org.hydrogen.event.api.EventHandler;
import org.hydrogen.event.api.EventManager;
import org.hydrogen.event.api.base.EventBus;
import org.hyperdrive.foundation.api.ManagedDisplay;
import org.hyperdrive.foundation.api.event.ClientCreatedHandler;
import org.hyperdrive.foundation.api.event.DisplayEventHandler;

/**
 * An <code>EventDispatcher</code> fetches and dispatches
 * <code>DisplayEvent</code>s for a {@link ManagedDisplay}. An
 * <code>EventDispatcher</code> fetches <code>DisplayEvent</code>s from the
 * <code>Display</code> that is wrapped by a <code>ManagedDisplay</code>. After
 * an event is fetched, it is delivered at corresponding the
 * <code>EventBus</code> objects that were registered with the
 * <code>ManagedDisplay</code>. (see
 * {@link ManagedDisplay#registerEventBus(DisplayEventSource, EventBus)})
 * <p>
 * The <code>EventDispatcher</code> implements the main hyperdrive even loop in
 * it's run method. This method is activated by the <code>ManagedDisplay</code>
 * that created the <code>EventDispatcher</code>. See
 * {@link ManagedDisplay#start()}.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
final class DisplayEventDispatcher implements Runnable {

	private final EventBus displayEventEmitter = new EventBus();

	// TODO this is basically the same mechanism as used in EventBus. Find a way
	// to seperate and uniform this mechanism.
	private final Map<DisplayEventSource, List<WeakReference<EventManager>>> eventManagers;
	private final List<ClientCreatedHandler> clientCreatedHandlers;

	private final class ConfigureRequestHandler implements
			DisplayEventHandler<ConfigureRequestEvent> {
		@Override
		public void handleEvent(final ConfigureRequestEvent event) {
			handleConfigureRequestEvent(event);
		}

		@Override
		public DisplayEventType getType() {
			return DisplayEventType.CONFIGURE_REQUEST;
		}
	}

	private final class MapRequestEventHandler implements
			DisplayEventHandler<MapRequestEvent> {
		@Override
		public void handleEvent(final MapRequestEvent event) {
			handleMapRequestEvent(event);
		}

		@Override
		public DisplayEventType getType() {
			return DisplayEventType.MAP_REQUEST;
		}
	}

	private final class DefaultDisplayEventHandler implements
			EventHandler<DisplayEvent> {
		@Override
		public void handleEvent(final DisplayEvent event) {
			doDefaultEventHandlingForDisplayEvent(event);
		}
	}

	private static final Logger LOGGER = Logger
			.getLogger(DisplayEventDispatcher.class);
	private static final String DISPLAYEVENT_LOGMESSAGE = "Firing display event: %s";
	private static final String THREADSTART_LOGMESSAGE = "Starting display event dispatching loop.";
	private static final String THREADSTOP_LOGMESSAGE = "Display event dispatching loop terminated.";

	private final Display display;
	private final ManagedDisplay managedDisplay;
	private volatile boolean running;

	DisplayEventDispatcher(final ManagedDisplay managedDisplay) {
		this.clientCreatedHandlers = new CopyOnWriteArrayList<ClientCreatedHandler>();
		// use weakhashmap?
		this.eventManagers = new WeakHashMap<DisplayEventSource, List<WeakReference<EventManager>>>();
		this.managedDisplay = managedDisplay;
		this.display = managedDisplay.getDisplay();
		initEventManagers();
	}

	private void initEventManagers() {
		this.displayEventEmitter
				.addTypedEventHandler(new ConfigureRequestHandler());
		this.displayEventEmitter
				.addTypedEventHandler(new MapRequestEventHandler());
		final DefaultDisplayEventHandler defaultDisplayEventHandler = new DefaultDisplayEventHandler();
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.BUTTON_PRESSED);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.BUTTON_RELEASED);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.KEY_PRESSED);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.KEY_RELEASED);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.DESTROY_NOTIFY);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.UNMAP_NOTIFY);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.MOUSE_ENTER);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.MOUSE_LEAVE);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.PROPERTY_CHANGED);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.FOCUS_GAIN_NOTIFY);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.FOCUS_LOST_NOTIFY);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.MAP_NOTIFY);
		this.displayEventEmitter.addEventHandler(defaultDisplayEventHandler,
				DisplayEventType.CONFIGURE_NOTIFY);
	}

	private void handleMapRequestEvent(final MapRequestEvent event) {
		createNewClientWhenNoEventManagers(event);
	}

	private void handleConfigureRequestEvent(final ConfigureRequestEvent event) {
		createNewClientWhenNoEventManagers(event);
	}

	private void createNewClientWhenNoEventManagers(final DisplayEvent event) {
		final List<WeakReference<EventManager>> eventManagersByEventSource = this.eventManagers
				.get(event.getEventSource());

		final DisplayEventSource eventSource = event.getEventSource();

		if (((eventManagersByEventSource == null) || eventManagersByEventSource
				.isEmpty()) && (eventSource instanceof PlatformRenderArea)) {
			final ClientWindow client = new ClientWindow(this.managedDisplay,
					(PlatformRenderArea) eventSource);
			notifyClientCreatedHandlers(client);
			client.fireEvent(event);
		} else {
			doDefaultEventHandlingForDisplayEvent(event);
		}
	}

	private void notifyClientCreatedHandlers(final ClientWindow clientWindow) {
		for (final ClientCreatedHandler clientCreatedHandler : this.clientCreatedHandlers) {
			clientCreatedHandler.handleCreatedClient(clientWindow);
		}
	}

	@Override
	public void run() {
		DisplayEventDispatcher.LOGGER
				.info(DisplayEventDispatcher.THREADSTART_LOGMESSAGE);

		if (this.running) {
			return;
		}
		this.running = true;
		Thread.currentThread().setName(
				String.format("Hyperdrive %s thread",
						DisplayEventDispatcher.class.getSimpleName()));

		while (this.running) {
			try {
				dispatchNextEvent(true);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		DisplayEventDispatcher.LOGGER
				.info(DisplayEventDispatcher.THREADSTOP_LOGMESSAGE);
	}

	/**
	 * Fetch and dispatch the next <code>DisplayEvent</code>.
	 * 
	 * @param block
	 *            True to wait for a new event to become available. False to
	 *            continue without dispatching an event.
	 */
	void dispatchNextEvent(final boolean block) {
		if (!block && this.display.isMasterQueueEmpty()) {
			return;
		}

		final DisplayEvent displayEvent = this.display
				.getEventFromMasterQueue();

		if (displayEvent != null) {

			DisplayEventDispatcher.LOGGER.debug(String.format(
					DisplayEventDispatcher.DISPLAYEVENT_LOGMESSAGE,
					displayEvent, this.managedDisplay));

			this.displayEventEmitter.fireEvent(displayEvent);
		}
	}

	void shutDown() {
		this.running = false;
	}

	private void doDefaultEventHandlingForDisplayEvent(final DisplayEvent event) {
		final List<WeakReference<EventManager>> list = this.eventManagers
				.get(event.getEventSource());

		for (final WeakReference<EventManager> eventManagerRef : list) {
			final EventManager eventManager = eventManagerRef.get();
			if (eventManager == null) {
				list.remove(eventManagerRef);
			} else {
				eventManager.fireEvent(event);
			}
		}
	}

	void addEventManagerForDisplayEventSource(final EventManager manager,
			final DisplayEventSource forSource) {
		final WeakReference<EventManager> eventManagerReference = new WeakReference<EventManager>(
				manager);
		if (this.eventManagers.containsKey(forSource)) {
			this.eventManagers.get(forSource).add(eventManagerReference);
		} else {
			final CopyOnWriteArrayList<WeakReference<EventManager>> list = new CopyOnWriteArrayList<WeakReference<EventManager>>();
			list.add(eventManagerReference);
			this.eventManagers.put(forSource, list);
		}
	}

	void addDisplayEventHandler(
			final DisplayEventHandler<? extends DisplayEvent> displayEventHandler) {
		this.displayEventEmitter.addTypedEventHandler(displayEventHandler);
	}

	void removeDisplayEventHandler(
			final DisplayEventHandler<? extends DisplayEvent> displayEventHandler) {
		this.displayEventEmitter.removeTypedEventHandler(displayEventHandler);
	}

	void addClientCreatedHandler(final ClientCreatedHandler clientCreatedHandler) {
		this.clientCreatedHandlers.add(clientCreatedHandler);
	}

	void removeClientCreatedHandler(
			final ClientCreatedHandler clientCreatedHandler) {
		this.clientCreatedHandlers.remove(clientCreatedHandler);
	}
}