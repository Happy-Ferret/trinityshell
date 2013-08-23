/*******************************************************************************
 * Trinity Shell Copyright (C) 2011 Erik De Rijcke
 *
 * This file is part of Trinity Shell.
 *
 * Trinity Shell is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * Trinity Shell is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 ******************************************************************************/
package org.trinity.foundation.api.display;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public interface DisplaySurfacePool {

	DisplaySurface getDisplaySurface(Object nativeHandle);

	boolean isPresent(Object nativeHandle);

	/**
	 * Blocks the display execution context from processing any events. This is
	 * to avoid that a newly created server side DisplaySurface is seen as a
	 * client. This method should be called before any server side display
	 * surface is created.
	 *
	 * @return A {@link DisplaySurfacePreparation}
	 */
	DisplaySurfacePreparation prepareDisplaySurface();
}
