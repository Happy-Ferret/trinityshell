package org.hypercube.view.fusionqtjambi;

import org.fusion.qt.painter.QFusionPaintCall;
import org.fusion.x11.core.XResourceHandle;
import org.hydrogen.display.api.ResourceHandle;
import org.hydrogen.geometry.api.Rectangle;
import org.hydrogen.paint.api.PaintCall;
import org.hydrogen.paint.api.PaintContext;
import org.hydrogen.paint.api.Paintable;
import org.hydrogen.paint.api.PaintableRef;
import org.hydrogen.paint.api.base.BasePaintableRef;
import org.hyperdrive.widget.api.ViewImplementation;
import org.hyperdrive.widget.api.Widget;
import org.hyperdrive.widget.impl.BaseWidget;

import com.trolltech.qt.core.Qt.WidgetAttribute;
import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.QFrame;
import com.trolltech.qt.gui.QWidget;

@ViewImplementation(Widget.View.class)
public class WidgetView implements Widget.View {

	@Override
	public QFusionPaintCall<ResourceHandle, QWidget> doCreate(Rectangle form,
			boolean visible, final Paintable parentPaintable) {
		return new QFusionPaintCall<ResourceHandle, QWidget>() {
			@Override
			public ResourceHandle call(final PaintContext<QWidget> paintContext) {
				// TODO use factory to get paintableRef.
				final PaintableRef parentPaintableRef = new BasePaintableRef(
						parentPaintable);

				final QWidget parentPaintPeer = (QWidget) paintContext
						.queryPaintPeer(parentPaintableRef);
				final QWidget paintPeer = createPaintPeer(paintContext,
						parentPaintPeer);
				return preperatePaintPeer(paintContext, paintPeer);
			}
		};
	}

	protected QWidget createPaintPeer(
			final PaintContext<? extends QWidget> paintContext,
			final QWidget parentPaintPeer) {

		// we instantiate all visuals as a qframe for more styling options.
		final QWidget paintPeer = new QFrame(parentPaintPeer);
		return paintPeer;
	}

	protected ResourceHandle preperatePaintPeer(
			final PaintContext<QWidget> paintContext, final QWidget newPaintPeer) {

		final long winId = newPaintPeer.effectiveWinId();

		newPaintPeer.setWindowFlags(WindowType.X11BypassWindowManagerHint);
		newPaintPeer.setAttribute(WidgetAttribute.WA_DeleteOnClose, true);
		newPaintPeer.setAttribute(WidgetAttribute.WA_DontCreateNativeAncestors,
				true);

		final PaintableRef paintable = paintContext.getPaintableRef();

		// TODO visibility from arg
		newPaintPeer.setVisible(paintable.isVisible());
		paintContext.bindPaintPeer(paintable, newPaintPeer);

		// set the styling id to the full classname of the paintable.
		final String name = paintable.getClass().getSimpleName();
		newPaintPeer.setObjectName(name);

		// TODO platform independent resource handle
		final ResourceHandle resourceHandle = XResourceHandle.valueOf(Long
				.valueOf(winId));

		return resourceHandle;
	}

	@Override
	public PaintCall<Void, ?> doDestroy() {
		// TODO Auto-generated method stub
		return null;
	}
}