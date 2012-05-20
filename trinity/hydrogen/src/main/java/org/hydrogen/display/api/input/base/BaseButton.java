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
package org.hydrogen.display.api.input.base;

import org.hydrogen.display.api.input.Button;

import com.google.inject.Inject;

// TODO documentation
/**
 * A <code>Button</code> represents a button on a mouse.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class BaseButton implements Button {

	private final int buttonCode;

	/**
	 * The native button code.
	 * 
	 * @param buttonCode
	 */
	@Inject
	protected BaseButton(final int buttonCode) {
		this.buttonCode = buttonCode;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getButtonCode() {
		return this.buttonCode;
	}
}