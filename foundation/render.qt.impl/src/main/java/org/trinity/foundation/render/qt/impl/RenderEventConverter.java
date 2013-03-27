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
package org.trinity.foundation.render.qt.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.trinity.foundation.api.display.event.DisplayEvent;
import org.trinity.foundation.api.shared.AsyncListenable;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.To;
import de.devsurf.injection.guice.annotations.To.Type;

@Bind(to = @To(Type.IMPLEMENTATION))
@Singleton
public class RenderEventConverter {

	private final Map<QEvent.Type, Optional<RenderEventConversion>> conversionByQEventType = new HashMap<QEvent.Type, Optional<RenderEventConversion>>();

	@Inject
	RenderEventConverter(final Set<RenderEventConversion> eventConversions) {
		for (final RenderEventConversion eventConverter : eventConversions) {
			this.conversionByQEventType.put(eventConverter.getQEventType(),
											Optional.of(eventConverter));
		}
	}

	public boolean convertRenderEvent(	final AsyncListenable eventTarget,
										final Object view,
										final QObject eventProducer,
										final QEvent event) {

		final Optional<RenderEventConversion> eventConverter = this.conversionByQEventType.get(event.type());
		DisplayEvent displayEvent = null;
		if (eventConverter.isPresent()) {
			displayEvent = eventConverter.get().convertEvent(	view,
																eventProducer,
																event);

			eventTarget.post(displayEvent);
			return true;
		}

		return false;
	}
}