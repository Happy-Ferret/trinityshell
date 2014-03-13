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
package org.trinity.shell.scene.api.event;

import org.trinity.shell.scene.api.ShellSurface;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/***************************************
 * Request to reparent the {@link org.trinity.shell.scene.api.ShellSurface} that emits this event.
 *
 ***************************************
 */
@Immutable
public class ShellNodeReparentRequestEvent extends ShellNodeEvent {

    @Nonnull
    private final ShellSurface parent;

    /**
	 * Create a new {@code ShellNodeChildAddedEvent} with the given
	 * {@code ShellNode} as the node that emitted the event, and the given
	 * {@code ShellNodeTransformation} as the details coming from the given node
	 * e.g. {@link org.trinity.shell.scene.api.ShellSurface#toGeoTransformation()}
	 *
	 * @param shellSurface
	 *            the emitting {@link org.trinity.shell.scene.api.ShellSurface}
	 * @param parent
	 *            a parent {@link org.trinity.shell.scene.api.ShellSurface}
	 */
	public ShellNodeReparentRequestEvent(@Nonnull final ShellSurface shellSurface,
                                         @Nonnull final ShellSurface parent) {
		super(shellSurface);
        this.parent = parent;
    }

    @Nonnull
    public ShellSurface getParent() {
        return this.parent;
    }
}