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
package org.trinity.shell.api.surface;

import com.google.common.eventbus.EventBus;

public interface ShellDisplayEventDispatcher {
	void dispatchDisplayEvent(final boolean block);

	void registerDisplayEventSourceListener(final EventBus nodeEventBus,
											final Object displayEventTarget);

	void unregisterDisplayEventSourceListener(	EventBus nodeEventBus,
												Object displayEventTarget);

	void unregisterAllDisplayEventSourceListeners(Object displayEventTarget);
}