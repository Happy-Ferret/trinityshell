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

import org.trinity.core.display.api.Atom;

import com.google.inject.Inject;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class PropertyInstanceNumber extends BasePropertyInstance {

	private final int number;

	// public PropertyInstanceNumber(final Display display,
	// final String typeName,
	// final int number) {
	// super(display,
	// typeName);
	// this.number = number;
	// }

	/**
	 * 
	 * @param encodingType
	 * @param number
	 */
	@Inject
	public PropertyInstanceNumber(final Atom encodingType, final int number) {
		super(encodingType);
		this.number = number;
	}

	/**
	 * 
	 * @return
	 */
	public int getNumber() {
		return this.number;
	}

}