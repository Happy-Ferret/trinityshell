/*
 * This file is part of Hydrogen. Hydrogen is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Hydrogen is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with Hydrogen. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.trinity.foundation.display.api;

import org.trinity.foundation.display.api.event.DisplayEvent;

/**
 * A <code>Display</code> provides a basic contract to talk to the underlying
 * native display. One of the most import aspects of the <code>Display</code>
 * interface is the queuing of {@link DisplayEvent}s.
 * <p>
 * <code>DisplayEvent</code>s generated by the underlying native display are
 * fetched and placed on the master queue. The fetching of
 * <code>DisplayEvent</code>s happens when an {@link DisplayEventProducer} is
 * added to the <code>Display</code>. This implies that any source can provide
 * events to a <code>Display</code>, including the <code>Display</code> itself.
 * Fetched events can then later be taken from the <code>Display</code>'s master
 * queue for further processing.
 * <p>
 * Other functionality provided by the <code>Display</code> is access to the
 * {@link Mouse} and {@link Keyboard}. {@link Atom}s and
 * {@link PlatformRenderAreaBling}s are also provided by the
 * <code>Display</code>.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 * @see BaseDisplay
 */
public interface DisplayServer {

	/**
	 * Indicates if there are any pending <code>DisplayEvent</code>s on the
	 * master queue.
	 * 
	 * @return
	 */
	boolean hasNextDisplayEvent();

	/**
	 * Retrieves ands removes the head <code>DisplayEvent</code> from this
	 * <code>Display</code> master queue.
	 * 
	 * @return The next {@link DisplayEvent}.
	 */
	DisplayEvent getNextDisplayEvent();

	/**
	 * Orderly shut down this <code>Display</code>. All resources living on this
	 * <code>Display</code> will be shut down as well.
	 * <p>
	 * This method does not shut down the underlying native display, it merely
	 * closes the connection to the underlying native display.
	 */
	void shutDown();

	/**
	 * The <code>PlatformRenderArea</code> that has the input focus on this
	 * <code>Display</code>
	 * 
	 * @return The focussed {@link PlatformRenderArea}.
	 */
	PlatformRenderArea getInputFocus();
}