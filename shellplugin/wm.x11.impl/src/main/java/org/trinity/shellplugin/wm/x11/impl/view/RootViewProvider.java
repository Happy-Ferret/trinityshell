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

package org.trinity.shellplugin.wm.x11.impl.view;

import org.trinity.foundation.api.display.DisplaySurfacePool;
import org.trinity.foundation.api.display.bindkey.DisplayExecutor;
import org.trinity.shellplugin.widget.impl.view.qt.AbstractQWidgetViewReferenceProvider;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;

public class RootViewProvider extends AbstractQWidgetViewReferenceProvider {

	@Inject
	RootViewProvider(	@DisplayExecutor final ListeningExecutorService displayExecutor,
						final DisplaySurfacePool displaySurfacePool) {
		super(	displayExecutor,
				displaySurfacePool);
	}

	@Override
	protected RootView createViewCall() {
		return new RootView();
	}
}
