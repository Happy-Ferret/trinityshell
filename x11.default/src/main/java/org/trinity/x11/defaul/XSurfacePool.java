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
package org.trinity.x11.defaul;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.eventbus.Subscribe;
import org.freedesktop.xcb.xcb_destroy_notify_event_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.common.Listenable;
import org.trinity.x11.defaul.simple.SimpleXCompositor;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@ThreadSafe
public class XSurfacePool {

    private static final Logger LOG = LoggerFactory.getLogger(XSurfacePool.class);

    private final BiMap<Integer, Listenable> surfaces = HashBiMap.create(32);
    private final SimpleXCompositor compositor;

    @Inject
    XSurfacePool(final SimpleXCompositor compositor) {
        this.compositor = compositor;
    }

    public Listenable get(final Integer surfaceHandle) {
        Listenable surface = this.surfaces.get(surfaceHandle);
        if (surface == null) {
            surface = registerNewSurface(surfaceHandle);
        }
        return surface;
    }

    private Listenable registerNewSurface(final Integer surfaceHandle) {
        LOG.debug("Xwindow={} added to cache.",
                surfaceHandle);
		final Listenable surface = this.compositor.createSurface(surfaceHandle);
		surface.register(new DestroyListener(surface));
		this.surfaces.put(surfaceHandle,
						  surface);
		return surface;
	}

    private class DestroyListener {
        private final Listenable surface;

        public DestroyListener(final Listenable surface) {
            this.surface = surface;
        }

        @Subscribe
        public void destroyed(final xcb_destroy_notify_event_t destroyNotifyEvent) {
            XSurfacePool.this.surfaces.remove(this.surface);
			this.surface.unregister(this);
        }
    }
}