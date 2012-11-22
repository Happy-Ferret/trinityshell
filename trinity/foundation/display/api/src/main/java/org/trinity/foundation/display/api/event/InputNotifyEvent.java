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
package org.trinity.foundation.display.api.event;

import org.trinity.foundation.input.api.Input;

// TODO documentation
/**
 * Represents general user input notification. This can be, for example, a
 * keyboard key that was pressed or a mouse button that was released.
 */
public class InputNotifyEvent<I extends Input> extends DisplayEvent {

	private final I input;

	/***************************************
	 * Create a new <code>InputNotifyEvent</code> that originated from the given
	 * {@link DisplayEventSource}. The input detail is described by the given
	 * {@link Input} argument.
	 * 
	 * @param displayEventSource
	 *            A {@link DisplayEventSource}
	 * @param input
	 *            an {@link Input}
	 *************************************** 
	 */
	public InputNotifyEvent(final DisplayEventSource displayEventSource, final I input) {
		super(displayEventSource);
		this.input = input;
	}

	public I getInput() {
		return this.input;
	}
}
