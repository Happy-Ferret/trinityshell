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
package org.trinity.shell.api.scene.event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.media.nativewindow.util.Point;
import javax.media.nativewindow.util.PointImmutable;

import org.trinity.shell.api.scene.ShellNode;

/***************************************
 * Informs that the {@link ShellNode} that emits this event, is moved.
 *
 ***************************************
 */
@Immutable
public class ShellNodeMovedEvent extends ShellNodeEvent {

    @Nonnull
    private final PointImmutable position;

    /**
	 * Create a new {@code ShellNodeChildAddedEvent} with the given
	 * {@code ShellNode} as the node that emitted the event, and the given
	 * {@code ShellNodeTransformation} as the details coming from the given node
	 * e.g. {@link ShellNode#toGeoTransformation()}
	 *
	 * @param shellNode
	 *            the emitting {@link ShellNode}
	 * @param position
	 *            a top left {@link Point} of the shell node
	 */
	public ShellNodeMovedEvent(@Nonnull final ShellNode shellNode,
                               @Nonnull final PointImmutable position) {
		super(	shellNode);
        this.position = position;
    }

    @Nonnull
    public PointImmutable getPosition() {
        return this.position;
    }
}