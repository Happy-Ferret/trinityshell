package org.hyperdrive.widget.impl;

import org.hydrogen.display.api.ResourceHandle;
import org.hydrogen.geometry.api.Rectangle;
import org.hydrogen.paint.api.PaintCall;
import org.hydrogen.paint.api.Paintable;
import org.hyperdrive.widget.api.ViewImplementation;
import org.hyperdrive.widget.impl.ChildWidget.View;

@ViewImplementation(View.class)
public class DummyChildWidgetViewImpl implements ChildWidget.View {

	@Override
	public PaintCall<ResourceHandle, ?> doCreate(Rectangle form,
			boolean visible, Paintable parentPaintable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaintCall<Void, ?> doDestroy() {
		// TODO Auto-generated method stub
		return null;
	}

}
