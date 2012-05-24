/*
 * This file is part of Fusion-X11.
 * 
 * Fusion-X11 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Fusion-X11 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Fusion-X11. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fusion.x11.core.xcb;

import org.fusion.x11.core.XCoreInterfaceProvider;
import org.fusion.x11.core.XDisplayPlatform;

/**
 * An <code>XcbCoreInterfaceProvider</code> creates a new
 * <code>XcbCoreInterfaceImpl</code> for use by the
 * <code>XDisplayPlatform</code>.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class XcbCoreInterfaceProvider implements XCoreInterfaceProvider {
	@Override
	public XcbCoreInterfaceImpl getNewXCoreInterface(
			final XDisplayPlatform xDisplayPlatform) {
		final XcbCoreInterfaceImpl returnXInterface = new XcbCoreInterfaceImpl(
				xDisplayPlatform);
		return returnXInterface;
	}
}