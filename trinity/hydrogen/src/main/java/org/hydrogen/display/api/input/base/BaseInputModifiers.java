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

import org.hydrogen.display.api.input.InputModifiers;
import org.hydrogen.display.api.input.Modifier;

import com.google.inject.Inject;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class BaseInputModifiers implements InputModifiers {

	private final int inputModifersMask;

	/**
	 * 
	 */
	public BaseInputModifiers() {
		this.inputModifersMask = 0;
	}

	/**
	 * 
	 * @param inputModifersMask
	 */
	@Inject
	protected BaseInputModifiers(final int inputModifersMask) {
		this.inputModifersMask = inputModifersMask;
	}

	// /**
	// *
	// * @param modifiers
	// */
	// public BaseInputModifiers(final Modifier... modifiers) {
	// int inputModMask = 0;
	// for (final Modifier modifier : modifiers) {
	// inputModMask |= modifier.getModifierMask();
	// }
	// this.inputModifersMask = inputModMask;
	// }

	/**
	 * 
	 * @param modifier
	 * @return
	 */
	@Override
	public boolean isModifierSet(final Modifier modifier) {
		return (this.inputModifersMask & modifier.getModifierMask()) != 0;
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public int getInputModifiersMask() {
		return this.inputModifersMask;
	}
}