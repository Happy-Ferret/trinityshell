package org.hyperdrive.widget.impl;

import org.hyperdrive.widget.api.ViewImplementation;
import org.hyperdrive.widget.impl.ChildWidget.View;
import org.trinity.core.display.api.ResourceHandle;
import org.trinity.core.geometry.api.Rectangle;
import org.trinity.core.render.api.PaintInstruction;
import org.trinity.core.render.api.Paintable;

@ViewImplementation(View.class)
public class DummyChildWidgetViewImpl implements ChildWidget.View {

	@Override
	public PaintInstruction<ResourceHandle, ?> doCreate(Rectangle form,
			boolean visible, Paintable parentPaintable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaintInstruction<Void, ?> doDestroy() {
		// TODO Auto-generated method stub
		return null;
	}

}