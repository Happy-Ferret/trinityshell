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

// TODO documentation
/**
 * A <code>MouseEnterLeaveNotifyEvent</code> notifies that a mouse cursor has
 * entered or left an on-screen display resource, like a window.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public abstract class PointerVisitationNotifyEvent extends DisplayEvent {

	/*****************************************
	 * @param displayEventSource
	 ****************************************/
	public PointerVisitationNotifyEvent(final DisplayEventSource displayEventSource) {
		super(displayEventSource);
	}
}