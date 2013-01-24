package org.trinity.foundation.render.qt.impl.binding.view.delegate;

import org.trinity.foundation.api.render.binding.view.ViewElementTypes;

import com.google.inject.Singleton;
import com.trolltech.qt.gui.QWidget;

import de.devsurf.injection.guice.annotations.Bind;

@Bind
@Singleton
public class ViewElementTypesImpl implements ViewElementTypes {

	private final Class<?>[] viewElementTypes = { QWidget.class };

	@Override
	public Class<?>[] getViewElementTypes() {
		return this.viewElementTypes;
	}

}