package org.trinity.render.qt.impl.painter.instructions;

import static com.google.common.base.Preconditions.checkArgument;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.trinity.foundation.render.api.PaintRoutine;
import org.trinity.render.qt.api.QJPaintContext;
import org.trinity.render.qt.impl.QJRenderEventConverter;
import org.trinity.shell.api.widget.ShellWidget;
import org.trinity.shellplugin.widget.api.binding.BindingDiscovery;
import org.trinity.shellplugin.widget.api.binding.InputSignalDispatcher;
import org.trinity.shellplugin.widget.api.binding.ViewProperty;
import org.trinity.shellplugin.widget.api.binding.ViewSlotInvocationHandler;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.eventbus.EventBus;
import com.trolltech.qt.core.Qt.WidgetAttribute;
import com.trolltech.qt.core.Qt.WindowType;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsDropShadowEffect;
import com.trolltech.qt.gui.QWidget;

public class QJBindViewRoutine implements PaintRoutine<Void, QJPaintContext> {

	private final BindingDiscovery bindingDiscovery;
	private final ViewSlotInvocationHandler viewSlotInvocationHandler;
	private final QJRenderEventConverter renderEventConverter;
	private final EventBus displayEventBus;

	private final Optional<QWidget> parentView;
	private final QWidget view;

	public QJBindViewRoutine(	BindingDiscovery bindingDiscovery,
								ViewSlotInvocationHandler viewSlotInvocationHandler,
								EventBus displayEventBus,
								QJRenderEventConverter renderEventConverter,
								Optional<QWidget> parentView,
								QWidget view) {

		this.bindingDiscovery = bindingDiscovery;
		this.viewSlotInvocationHandler = viewSlotInvocationHandler;
		this.renderEventConverter = renderEventConverter;
		this.displayEventBus = displayEventBus;

		this.parentView = parentView;
		this.view = view;
	}

	@Override
	public Void call(QJPaintContext paintContext) {
		checkArgument(paintContext.getPaintableSurfaceNode() instanceof ShellWidget);
		ShellWidget shellWidget = (ShellWidget) paintContext.getPaintableSurfaceNode();
		initView(paintContext);
		bindViewProperties(	bindingDiscovery,
							viewSlotInvocationHandler,
							shellWidget,
							view);
		bindViewEventListeners(shellWidget);
		bindViewInputEmitters(shellWidget);

		return null;
	}

	private void bindViewEventListeners(ShellWidget shellWidget) {
		new QJViewEventTracker(	this.displayEventBus,
								this.renderEventConverter,
								shellWidget,
								view);

	}

	private void bindViewInputEmitters(ShellWidget shellWidget) {
		shellWidget.addShellNodeEventHandler(new InputSignalDispatcher(	shellWidget,
																		bindingDiscovery));

		new QJViewInputTracker(	this.displayEventBus,
								this.renderEventConverter,
								shellWidget,
								view);

	}

	private void initView(final QJPaintContext paintContext) {
		if (parentView.isPresent()) {
			view.setParent(parentView.get());
		}

		final QGraphicsDropShadowEffect effect = new QGraphicsDropShadowEffect();
		effect.setBlurRadius(10);
		effect.setOffset(	0,
							5);
		effect.setColor(QColor.darkGray);
		view.setGraphicsEffect(effect);

		view.setWindowFlags(WindowType.X11BypassWindowManagerHint);
		view.setAttribute(	WidgetAttribute.WA_DeleteOnClose,
							true);
		view.setAttribute(	WidgetAttribute.WA_DontCreateNativeAncestors,
							true);

		paintContext.syncVisualGeometryToSurfaceNode(view);

	}

	private void bindViewProperties(BindingDiscovery bindingDiscovery,
									ViewSlotInvocationHandler viewSlotInvocationHandler,
									ShellWidget shellWidget,
									Object view) {
		Method[] fields;

		try {
			fields = bindingDiscovery.lookupAllViewProperties(shellWidget.getClass());
			for (final Method method : fields) {
				final ViewProperty viewProperty = method.getAnnotation(ViewProperty.class);
				final Optional<Method> viewSlot = bindingDiscovery.lookupViewPropertySlot(	view.getClass(),
																							viewProperty.value());
				if (!viewSlot.isPresent()) {
					continue;
				}

				final Object argument = method.invoke(shellWidget);
				viewSlotInvocationHandler.invokeSlot(	shellWidget,
														viewProperty,
														view,
														viewSlot.get(),
														argument);
			}
		} catch (ExecutionException e) {
			Throwables.propagate(e);
		} catch (IllegalAccessException e) {
			Throwables.propagate(e);
		} catch (IllegalArgumentException e) {
			Throwables.propagate(e);
		} catch (InvocationTargetException e) {
			Throwables.propagate(e);
		}
	}
}