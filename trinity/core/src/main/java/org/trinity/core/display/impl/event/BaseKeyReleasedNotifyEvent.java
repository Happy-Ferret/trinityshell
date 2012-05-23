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
package org.trinity.core.display.impl.event;

import org.trinity.core.display.api.event.DisplayEventSource;
import org.trinity.core.display.api.event.DisplayEventType;
import org.trinity.core.input.api.KeyboardInput;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/*****************************************
 * @author Erik De Rijcke
 ****************************************/
public class BaseKeyReleasedNotifyEvent extends BaseKeyNotifyEvent {

	/*****************************************
	 * @param eventType
	 * @param eventSource
	 * @param input
	 ****************************************/
	@Inject
	protected BaseKeyReleasedNotifyEvent(	@Named("KeyReleased") final DisplayEventType eventType,
											@Assisted final DisplayEventSource eventSource,
											@Assisted final KeyboardInput input) {
		super(eventType, eventSource, input);
	}
}