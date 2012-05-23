/*
 * This file is part of Hydrogen.
 * 
 * Hydrogen is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Hydrogen is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Hydrogen. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.core.display.impl;

import java.util.Arrays;

import org.trinity.core.display.api.Atom;
import org.trinity.core.display.api.PlatformRenderArea;

import com.google.inject.Inject;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class PropertyInstancePlatformRenderAreas extends BasePropertyInstance {

	private final PlatformRenderArea[] platformRenderAreas;

	// public PropertyInstancePlatformRenderAreas(final Display display,
	// final String typeName,
	// final PlatformRenderArea... platformRenderAreas) {
	// super(display, typeName);
	// this.platformRenderAreas = platformRenderAreas;
	// }

	/**
	 * 
	 * @param type
	 * @param platformRenderAreas
	 */
	@Inject
	protected PropertyInstancePlatformRenderAreas(final Atom type,
			final PlatformRenderArea... platformRenderAreas) {
		super(type);
		this.platformRenderAreas = platformRenderAreas;
	}

	/**
	 * 
	 * @return
	 */
	public PlatformRenderArea[] getPlatformRenderAreas() {
		// return a copy so manipulation of the returned instance can take
		// place without interfering with the source.
		return Arrays.copyOf(this.platformRenderAreas,
				this.platformRenderAreas.length);
	}
}