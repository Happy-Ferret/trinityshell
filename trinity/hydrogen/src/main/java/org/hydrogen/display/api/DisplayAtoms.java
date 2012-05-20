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
package org.hydrogen.display.api;

//TODO documentation
/**
 * <code>DisplayAtoms</code> keeps track of all defined {@link Atom}s on a
 * single {@link Display}. It provides functionality to query a known
 * </code>Atom</code> by name or by number.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public interface DisplayAtoms {

	/**
	 * 
	 * @param atomName
	 * @return
	 */
	Atom getAtomByName(String atomName);

	/**
	 * 
	 * @param atomNames
	 * @return
	 */
	Atom[] getAtomsByNames(String... atomNames);
}