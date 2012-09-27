package org.trinity.shell.api.node.event;

import org.trinity.shell.api.node.ShellNode;
import org.trinity.shell.api.node.ShellNodeTransformation;

public class ShellNodeMoveRequestEvent extends ShellNodeEvent {

	public ShellNodeMoveRequestEvent(final ShellNode shellNode, final ShellNodeTransformation shellNodeTransformation) {
		super(	shellNode,
				shellNodeTransformation);
	}
}