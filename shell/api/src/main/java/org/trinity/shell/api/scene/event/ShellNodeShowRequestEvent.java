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
package org.trinity.shell.api.scene.event;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import org.trinity.foundation.api.shared.ExecutionContext;
import org.trinity.shell.api.bindingkey.ShellExecutor;
import org.trinity.shell.api.scene.ShellNode;
import org.trinity.shell.api.scene.ShellNodeTransformation;

/***************************************
 * Request to show the {@link ShellNode} that emits this event.
 *
 ***************************************
 */
@Immutable
@ExecutionContext(ShellExecutor.class)
public class ShellNodeShowRequestEvent extends ShellNodeVisibilityRequestEvent {

	/**
	 * Create a new {@code ShellNodeChildAddedEvent} with the given
	 * {@code ShellNode} as the node that emitted the event, and the given
	 * {@code ShellNodeTransformation} as the details coming from the given node
	 * e.g. {@link ShellNode#toGeoTransformation()}
	 *
	 * @param shellNode
	 *            the emitting {@link ShellNode}
	 * @param shellNodeTransformation
	 *            a {@link ShellNodeTransformation}
	 */
	public ShellNodeShowRequestEvent(@Nonnull final ShellNode shellNode,
                                     @Nonnull final ShellNodeTransformation shellNodeTransformation) {
		super(	shellNode,
				shellNodeTransformation);
	}
}