package org.trinity.foundation.render.qt.impl.eventconverters;

import org.trinity.foundation.api.display.event.KeyNotifyEvent;
import org.trinity.foundation.api.display.input.InputModifiers;
import org.trinity.foundation.api.display.input.Key;
import org.trinity.foundation.api.display.input.KeyboardInput;
import org.trinity.foundation.api.display.input.Momentum;
import org.trinity.foundation.render.qt.impl.QJRenderEventConversion;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QKeyEvent;

public abstract class AbstractQJKeyConversion implements QJRenderEventConversion {

	@Override
	public KeyNotifyEvent convertEvent(	final Object eventTarget,
										final Object view,
										final QObject qObject,
										final QEvent qEvent) {

		qEvent.accept();
		final QKeyEvent keyEvent = (QKeyEvent) qEvent;

		if (keyEvent.isAutoRepeat()) {
			return null;
		}

		final int keyCode = keyEvent.nativeScanCode();

		final int state = keyEvent.nativeModifiers();

		final Key key = new Key(keyCode);
		final InputModifiers inputModifiers = new InputModifiers(state);
		final KeyboardInput input = new KeyboardInput(	getMomemtum(),
														key,
														inputModifiers);

		final KeyNotifyEvent keyNotifyEvent = new KeyNotifyEvent(	eventTarget,
																	input);

		return keyNotifyEvent;
	}

	public abstract Momentum getMomemtum();
}