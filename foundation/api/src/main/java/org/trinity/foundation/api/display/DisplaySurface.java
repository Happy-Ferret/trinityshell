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

import javax.annotation.concurrent.ThreadSafe;

import org.trinity.foundation.api.display.bindkey.DisplayExecutor;
import org.trinity.foundation.api.shared.AsyncListenable;
import org.trinity.foundation.api.shared.ExecutionContext;
import org.trinity.foundation.api.shared.Rectangle;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Represents a native isolated graphical area. A <code>DisplaySurface</code>
 * maps to a native window from the native display system.
 *
 */
@ExecutionContext(DisplayExecutor.class)
@ThreadSafe
public interface DisplaySurface extends AsyncListenable {

	/***************************************
	 * Destroy the bound {@link DisplayArea}. A destroyed {@link DisplayArea}
	 * should be disposed and should not accept any calls made by it's
	 * {@link DisplayAreaManipulator}.
	 *
	 * @return A {@link ListenableFuture} that indicates when the operation is
	 *         done.
	 ***************************************
	 */
	ListenableFuture<Void> destroy();

	/***************************************
	 * Set the input focus to the bound {@link DisplayArea}. Generated input
	 * evens will have their source set as coming from the manipulated
	 * {@link DisplayArea}.
	 * <p>
	 * The effects of giving focus to a hidden or destroyed {@link DisplayArea}
	 * is implementation dependent.
	 * </p>
	 *
	 * @return A {@link ListenableFuture} that indicates when the operation is
	 *         done.
	 ***************************************
	 */
	ListenableFuture<Void> setInputFocus();

	/***************************************
	 * Move the bound {@link DisplayArea} to the given coordinates, relative to
	 * the parent. The bound {@link DisplayArea}'s top left corner will be
	 * positioned at the provided coordinates.
	 *
	 * @param x
	 *            The X coordinate. Usually in pixels but can be implementation
	 *            dependent.
	 * @param y
	 *            The Y coordinate. Usually in pixels but can be implementation
	 *            dependent. </p>
	 * @return A {@link ListenableFuture} that indicates when the operation is
	 *         done.
	 ***************************************
	 */
	ListenableFuture<Void> move(int x,
	                            int y);

	/***************************************
	 * Perform a move and resize operation on the bound {@link DisplayArea}.
	 *
	 *
	 * @param x
	 *            The X coordinate. Usually in pixels but can be implementation
	 *            dependent.
	 * @param y
	 *            The Y coordinate. Usually in pixels but can be implementation
	 *            dependent.
	 * @param width
	 *            The width. Usually in pixels but can be implementation
	 *            dependent.
	 * @param height
	 *            The height. Usually in pixels but can be implementation
	 *            dependent. </p>
	 * @return A {@link ListenableFuture} that indicates when the operation is
	 *         done.
	 * @see #move(int, int)
	 * @see #resize(int, int)
	 *
	 ***************************************
	 */
	ListenableFuture<Void> moveResize(	int x,
	                                      int y,
	                                      int width,
	                                      int height);

	/***************************************
	 * Set the size of the bound {@link DisplayArea}.
	 *
	 * @param width
	 *            The width. Usually in pixels but can be implementation
	 *            dependent.
	 * @param height
	 *            The width. Usually in pixels but can be implementation
	 *            dependent. </p>
	 * @return A {@link ListenableFuture} that indicates when the operation is
	 *         done.
	 ***************************************
	 */
	ListenableFuture<Void> resize(	int width,
	                                  int height);

	/***************************************
	 * Query geometry information. The values of the returned {@link Rectangle}
	 * are display server dependent but are usually in pixels.
	 *
	 * @return a future {@link Rectangle} corresponding to this
	 *         {@link DisplaySurface} position, width and a height.
	 ***************************************
	 */
	ListenableFuture<Rectangle> getGeometry();

	/***************************************
	 * Return the handle of the underlying native resource.
	 *
	 * @return a {@link DisplaySurfaceHandle}
	 ***************************************
	 */
	DisplaySurfaceHandle getDisplaySurfaceHandle();

}
