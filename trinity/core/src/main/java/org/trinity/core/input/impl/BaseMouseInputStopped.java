/*
 * Trinity Window Manager and Desktop Shell Copyright (C) 2012 Erik De Rijcke
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.core.input.impl;

import org.trinity.core.input.api.Momentum;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/*****************************************
 * @author Erik De Rijcke
 ****************************************/
public class BaseMouseInputStopped extends BaseMouseInput {

	/*****************************************
	 * @param momentum
	 * @param baseButton
	 * @param baseInputModifiers
	 * @param rootX
	 * @param rootY
	 * @param relativeX
	 * @param relativeY
	 ****************************************/
	@Inject
	protected BaseMouseInputStopped(@Named("Stopped") final Momentum momentum,
									@Assisted final BaseButton baseButton,
									@Assisted final BaseInputModifiers baseInputModifiers,
									@Assisted("rootX") final int rootX,
									@Assisted("rootY") final int rootY,
									@Assisted("relativeX") final int relativeX,
									@Assisted("relativeY") final int relativeY) {
		super(	momentum,
				baseButton,
				baseInputModifiers,
				rootX,
				rootY,
				relativeX,
				relativeY);
	}

}