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
package org.trinity.foundation.api.display.bindkey;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.BindingAnnotation;
import org.trinity.foundation.api.shared.ExecutionContext;

import javax.inject.Singleton;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for the 'Display' {@link ExecutionContext}. A {@link Singleton}
 * {@link ListeningExecutorService} with this key is provided by the
 * org.trinity.foundation.api.display package.
 */
@BindingAnnotation
@Target({ TYPE, FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
public @interface DisplayExecutor {

}
