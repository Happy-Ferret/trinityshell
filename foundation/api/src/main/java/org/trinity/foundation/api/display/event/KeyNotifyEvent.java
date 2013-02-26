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
package org.trinity.foundation.api.display.event;

import org.trinity.foundation.api.display.input.KeyboardInput;

/**
 * Notifies that a keyboard key's state (pressed, released) has changed.
 */
public class KeyNotifyEvent extends InputNotifyEvent<KeyboardInput> {

	/***************************************
	 * Create a new <code>KeyNotifyEvent</code> with the given display resource
	 * as the target object.
	 * 
	 * @param displayEventTarget
	 *            The receiver of this event. eg the display resource that has
	 *            the focus at the time of keyboard input.
	 * @param input
	 *            {@link KeyboardInput}
	 *************************************** 
	 */
	public KeyNotifyEvent(	final Object displayEventTarget,
							final KeyboardInput input) {
		super(	displayEventTarget,
				input);
	}
}