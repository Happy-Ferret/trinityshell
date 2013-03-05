/*
 * Trinity Window Manager and Desktop Shell Copyright (C) 2012 Erik De Rijcke
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.foundation.display.x11.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.concurrent.NotThreadSafe;

import org.freedesktop.xcb.LibXcb;
import org.freedesktop.xcb.xcb_generic_event_t;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.To;
import de.devsurf.injection.guice.annotations.To.Type;

@Bind(to = @To(value = Type.IMPLEMENTATION))
@NotThreadSafe
public class XEventPump implements Runnable {

	private final XConnection connection;
	private final EventBus xEventBus;
	private final ExecutorService xEventPumpExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(final Runnable r) {
			return new Thread(	r,
								"x-event-pump");
		}
	});
	private final ListeningExecutorService xExecutor;

	@Inject
	XEventPump(	final XConnection connection,
				@Named("XEventBus") final EventBus xEventBus,
				@Named("XExecutor") final ListeningExecutorService xExecutor) {
		this.connection = connection;
		this.xEventBus = xEventBus;
		this.xExecutor = xExecutor;
	}

	@Override
	public void run() {
		final xcb_generic_event_t event_t = LibXcb.xcb_wait_for_event(this.connection.getConnectionReference());

		if (LibXcb.xcb_connection_has_error(this.connection.getConnectionReference()) != 0) {
			throw new Error("X11 connection was closed unexpectedly - maybe your X server terminated / crashed?");
		}

		this.xExecutor.submit(new Runnable() {
			@Override
			public void run() {
				XEventPump.this.xEventBus.post(event_t);
			}
		});

		// schedule next event retrieval
		this.xEventPumpExecutor.submit(this);
	}

	public void start() {
		this.xEventPumpExecutor.submit(this);
	}

	public void stop() {
		this.xEventPumpExecutor.shutdown();
		try {
			if (this.xEventPumpExecutor.awaitTermination(	10,
															TimeUnit.SECONDS)) {
				return;
			}
			// TODO log error
			System.err.println("X event pump could not terminate gracefully!");
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}