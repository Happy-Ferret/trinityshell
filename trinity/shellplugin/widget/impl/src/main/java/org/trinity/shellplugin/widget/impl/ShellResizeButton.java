/*
 * This file is part of HyperDrive. HyperDrive is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. HyperDrive is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with HyperDrive. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.trinity.shellplugin.widget.impl;

import org.trinity.foundation.render.api.PainterFactory;
import org.trinity.shell.api.node.ShellNodeExecutor;
import org.trinity.shell.api.surface.ShellDisplayEventDispatcher;
import org.trinity.shell.api.surface.ShellSurface;
import org.trinity.shell.api.widget.ShellWidgetView;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;

// TODO documentation
/**
 * @author Erik De Rijcke
 * @since 1.0
 */
@Bind
public class ShellResizeButton extends ShellDragButton {

	private int desiredWidth;
	private int desiredHeight;

	@Inject
	protected ShellResizeButton(final EventBus eventBus,
								@Named("ShellRootSurface") final ShellSurface root,
								final ShellDisplayEventDispatcher shellDisplayEventDispatcher,
								final PainterFactory painterFactory,
								@Named("shellWidgetGeoExecutor") final ShellNodeExecutor shellNodeExecutor,
								@Named("ShellButtonView") final ShellWidgetView view) {
		super(	eventBus,
				root,
				shellDisplayEventDispatcher,
				painterFactory,
				shellNodeExecutor,
				view);
	}

	@Override
	public void startDragClient() {
		this.desiredWidth = getClient().getWidth();
		this.desiredHeight = getClient().getHeight();
		super.startDragClient();
	}

	@Override
	protected void mutate(	final int vectX,
							final int vectY) {
		this.desiredWidth += vectX;
		this.desiredHeight += vectY;

		getClient().setWidth(this.desiredWidth);
		getClient().setHeight(this.desiredHeight);

		getClient().requestResize();
	}
}