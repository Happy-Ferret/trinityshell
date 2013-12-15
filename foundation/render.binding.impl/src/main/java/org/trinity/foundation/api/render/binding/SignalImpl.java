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

package org.trinity.foundation.api.render.binding;

import com.google.common.base.Optional;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.assistedinject.Assisted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.render.binding.view.delegate.Signal;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class SignalImpl implements Signal {

	private static final Logger                          LOG                 = LoggerFactory.getLogger(SignalImpl.class);
	private static final Map<HashCode, Optional<Method>> EVENT_SLOTS_BY_HASH = new HashMap<>();
	private static final HashFunction                    HASH_FUNCTION       = Hashing.goodFastHash(16);

	private final ListeningExecutorService dataModelExecutor;
	private final Object                   eventSignalReceiver;
	private final Optional<Method>         slot;

	@Inject
	SignalImpl(@Assisted final ListeningExecutorService dataModelExecutor,
			   @Assisted final Object eventSignalReceiver,
			   @Assisted final String inputSlotName) {
		this.dataModelExecutor = dataModelExecutor;
		this.eventSignalReceiver = eventSignalReceiver;

		this.slot = findSlot(this.eventSignalReceiver.getClass(),
							 inputSlotName);
	}

	private static Optional<Method> findSlot(final Class<?> modelClass,
											 final String methodName) {

		final HashCode hashCode = HASH_FUNCTION.newHasher().putInt(modelClass.hashCode())
											   .putUnencodedChars(methodName).hash();

		Optional<Method> methodOptional = EVENT_SLOTS_BY_HASH.get(hashCode);
		if(methodOptional == null) {
			methodOptional = getSlot(modelClass,
									 methodName);
			EVENT_SLOTS_BY_HASH.put(hashCode,
									methodOptional);
		}

		return methodOptional;
	}

	private static Optional<Method> getSlot(final Class<?> modelClass,
											final String methodName) {
		Method inputSlot = null;
		try {
			inputSlot = modelClass.getMethod(methodName);
		}
		catch(final SecurityException e) {
			LOG.error("Error while trying to find an input slot for class=" + modelClass + " with slotname="
							  + methodName,
					  e);
		}
		catch(final NoSuchMethodException e) {
			LOG.warn("No event slot found for class=" + modelClass + " with slotname=" + methodName,
					 e);
		}
		return Optional.fromNullable(inputSlot);
	}

	@Override
	public ListenableFuture<Void> fire() {
		return this.dataModelExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				if(SignalImpl.this.slot.isPresent()) {
					final Method method = SignalImpl.this.slot.get();
					method.setAccessible(true);
					method.invoke(SignalImpl.this.eventSignalReceiver);
				}
				return null;
			}
		});
	}
}
