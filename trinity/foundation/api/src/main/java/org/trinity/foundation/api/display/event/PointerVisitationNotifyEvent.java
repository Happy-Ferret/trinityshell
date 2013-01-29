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
package org.trinity.foundation.api.display.event;

/****************************************
 * Indicates when a pointer device has crossed the boundary of a a display
 * resource.
 * 
 *************************************** 
 */
public abstract class PointerVisitationNotifyEvent extends DisplayEvent {

	/***************************************
	 * Construct a new <code>PointerVisitationNotifyEvent</code> that targets
	 * the given display resource.
	 * 
	 * @param displayEventTarget
	 *            The receiver of this event. eg the display resource that was
	 *            left by the pointer.
	 *************************************** 
	 */
	public PointerVisitationNotifyEvent(final Object displayEventTarget) {
		super(displayEventTarget);
	}
}
