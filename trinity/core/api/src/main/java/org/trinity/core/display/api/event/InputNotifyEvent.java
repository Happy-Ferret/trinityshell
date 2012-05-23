/*
 * This file is part of Hydrogen.
 * 
 * Hydrogen is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Hydrogen is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Hydrogen. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.core.display.api.event;

import org.trinity.core.input.api.Input;

// TODO documentation
/**
 * An <code>InputNotifyEvent</code> represents general user input notification.
 * This can be, for example, a keyboard key that was pressed or a mouse button
 * that was released.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public interface InputNotifyEvent<I extends Input> extends DisplayEvent {

	/**
	 * Describes the input that has changed.
	 * 
	 * @return
	 */
	I getInput();
}