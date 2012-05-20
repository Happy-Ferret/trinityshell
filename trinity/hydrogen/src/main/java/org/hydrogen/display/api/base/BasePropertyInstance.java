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
package org.hydrogen.display.api.base;

import org.hydrogen.display.api.Atom;
import org.hydrogen.display.api.PropertyInstance;

import com.google.inject.Inject;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class BasePropertyInstance implements PropertyInstance {

	private final Atom type;

	// public BasePropertyInstance(final Display display,
	// final String typeName) {
	// // TODO check if type exists and create it if not?
	// this(display.getDisplayAtoms().getAtomByName(typeName));
	// }

	/**
	 * 
	 * @param type
	 */
	@Inject
	public BasePropertyInstance(final Atom type) {
		this.type = type;
	}

	@Override
	public Atom getType() {
		return this.type;
	}

}