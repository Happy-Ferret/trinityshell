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
package org.trinity.foundation.api.render.binding.view;

import java.lang.reflect.Method;

/***************************************
 * Transforms a model property value to one or more other values so it can be
 * used to invoke a view method. To transform to multiple different types of
 * objects, simply return an array of objects containing the values. The value
 * returned by {@link #adapt(Object)} will be used directly as the var args
 * argument of {@link Method#invoke(Object, Object...)}
 * 
 * @see PropertySlot
 *************************************** 
 */
public interface PropertyAdapter<T> {

	/***************************************
	 * Transforms a model property value to one or more other values.
	 * 
	 * @param property
	 *            A model property
	 * @return the transformatin.
	 *************************************** 
	 */
	Object adapt(T property);
}