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
package org.trinity.foundation.display.x11.impl;

import static com.google.common.util.concurrent.Futures.transform;
import static org.freedesktop.xcb.LibXcb.xcb_change_window_attributes;
import static org.freedesktop.xcb.LibXcb.xcb_configure_window;
import static org.freedesktop.xcb.LibXcb.xcb_destroy_window;
import static org.freedesktop.xcb.LibXcb.xcb_flush;
import static org.freedesktop.xcb.LibXcb.xcb_get_geometry;
import static org.freedesktop.xcb.LibXcb.xcb_get_geometry_reply;
import static org.freedesktop.xcb.LibXcb.xcb_map_window;
import static org.freedesktop.xcb.LibXcb.xcb_reparent_window;
import static org.freedesktop.xcb.LibXcb.xcb_set_input_focus;
import static org.freedesktop.xcb.LibXcb.xcb_unmap_window;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import javax.annotation.concurrent.ThreadSafe;

import org.freedesktop.xcb.SWIGTYPE_p_xcb_connection_t;
import org.freedesktop.xcb.xcb_config_window_t;
import org.freedesktop.xcb.xcb_cw_t;
import org.freedesktop.xcb.xcb_event_mask_t;
import org.freedesktop.xcb.xcb_generic_error_t;
import org.freedesktop.xcb.xcb_get_geometry_cookie_t;
import org.freedesktop.xcb.xcb_get_geometry_reply_t;
import org.freedesktop.xcb.xcb_input_focus_t;
import org.freedesktop.xcb.xcb_stack_mode_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.display.DisplayArea;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.display.DisplaySurfaceHandle;
import org.trinity.foundation.api.shared.AsyncListenableEventBus;
import org.trinity.foundation.api.shared.ImmutableRectangle;
import org.trinity.foundation.api.shared.Rectangle;

import com.google.common.base.Function;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

@ThreadSafe
public final class XWindow implements DisplaySurface {

	private static final Logger logger = LoggerFactory.getLogger(XWindow.class);

	private static int moveValueMask = xcb_config_window_t.XCB_CONFIG_WINDOW_X
			| xcb_config_window_t.XCB_CONFIG_WINDOW_Y;
	private static final ByteBuffer moveValueListBuffer = ByteBuffer
			.allocateDirect(8).order(ByteOrder.nativeOrder());

	private static final int moveResizeValueMask = xcb_config_window_t.XCB_CONFIG_WINDOW_X
			| xcb_config_window_t.XCB_CONFIG_WINDOW_Y
			| xcb_config_window_t.XCB_CONFIG_WINDOW_WIDTH
			| xcb_config_window_t.XCB_CONFIG_WINDOW_HEIGHT;
	private static final ByteBuffer moveResizeValueListBuffer = ByteBuffer
			.allocateDirect(16).order(ByteOrder.nativeOrder());

	private static final int resizeValueMask = xcb_config_window_t.XCB_CONFIG_WINDOW_WIDTH
			| xcb_config_window_t.XCB_CONFIG_WINDOW_HEIGHT;
	private static final ByteBuffer resizeValueList = ByteBuffer
			.allocateDirect(8).order(ByteOrder.nativeOrder());

	private static final int lowerValueMask = xcb_config_window_t.XCB_CONFIG_WINDOW_STACK_MODE;
	private static final ByteBuffer lowerValueListBuffer = ByteBuffer
			.allocateDirect(4).order(ByteOrder.nativeOrder())
			.putInt(xcb_stack_mode_t.XCB_STACK_MODE_BELOW);

	private static final int raiseValueMask = xcb_config_window_t.XCB_CONFIG_WINDOW_STACK_MODE;
	private static final ByteBuffer raiseValueListBuffer = ByteBuffer
			.allocateDirect(4).order(ByteOrder.nativeOrder())
			.putInt(xcb_stack_mode_t.XCB_STACK_MODE_ABOVE);

	private final DisplaySurfaceHandle resourceHandle;
	private final XConnection xConnection;
	private final XTime xTime;
	private final ListeningExecutorService xExecutor;

	private final AsyncListenableEventBus xWindowEventBus;

	@Inject
	XWindow(final XTime xTime,
			final XConnection xConnection,
			@Assisted final DisplaySurfaceHandle resourceHandle,
			@Named("Display") final ListeningExecutorService xExecutor) {
		this.xTime = xTime;
		this.xConnection = xConnection;
		this.resourceHandle = resourceHandle;
		this.xExecutor = xExecutor;
		this.xWindowEventBus = new AsyncListenableEventBus(xExecutor);
	}

	@Override
	public void register(final Object listener) {
		this.xWindowEventBus.register(listener);
	}

	@Override
	public void post(final Object event) {
		this.xWindowEventBus.post(event);
	}

	@Override
	public void unregister(final Object listener) {
		this.xWindowEventBus.unregister(listener);
	}

	@Override
	public void register(	final Object listener,
							final ExecutorService executor) {
		this.xWindowEventBus.register(	listener,
										executor);
	}

	@Override
	public DisplaySurfaceHandle getDisplaySurfaceHandle() {
		return this.resourceHandle;
	}

	@Override
	public ListenableFuture<Void> destroy() {
		final int winId = getWindowId();
		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] destroy.",
																winId);
												xcb_destroy_window(	getConnectionRef(),
																	winId);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	private int getWindowId() {
		final int windowId = ((Integer) this.resourceHandle.getNativeHandle())
				.intValue();
		return windowId;
	}

	private SWIGTYPE_p_xcb_connection_t getConnectionRef() {
		final SWIGTYPE_p_xcb_connection_t connection_t = this.xConnection
				.getConnectionReference();
		return connection_t;
	}

	@Override
	public ListenableFuture<Void> setInputFocus() {

		final int winId = getWindowId();
		final int time = this.xTime.getTime();

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] set input focus.",
																winId);
												xcb_set_input_focus(getConnectionRef(),
																	(short) xcb_input_focus_t.XCB_INPUT_FOCUS_NONE,
																	winId,
																	time);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> lower() {

		final int winId = getWindowId();

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] lower.",
																winId);
												xcb_configure_window(	getConnectionRef(),
																		winId,
																		XWindow.lowerValueMask,
																		XWindow.lowerValueListBuffer);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> show() {
		final int winId = getWindowId();

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] show.",
																winId);
												xcb_map_window(	getConnectionRef(),
																winId);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> move(	final int x,
										final int y) {

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.moveValueListBuffer
														.clear();
												XWindow.moveValueListBuffer
														.putInt(x).putInt(y);
												final int winId = getWindowId();

												XWindow.logger
														.debug(	"[winId={}] move x={}, y={}.",
																x,
																y,
																winId);
												xcb_configure_window(	getConnectionRef(),
																		winId,
																		XWindow.moveValueMask,
																		XWindow.moveValueListBuffer);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> moveResize(	final int x,
												final int y,
												final int width,
												final int height) {
		// we have to adjust the size with the X border. This sucks because it
		// introduces an extra roundtrip to the X server. -_-

		final int winId = getWindowId();
		final ListenableFuture<xcb_get_geometry_cookie_t> geometryRequest = this.xExecutor
				.submit(new Callable<xcb_get_geometry_cookie_t>() {
					@Override
					public xcb_get_geometry_cookie_t call() {
						XWindow.logger
								.debug(	"[winId={}] get geometry request.",
										winId);

						final xcb_get_geometry_cookie_t cookie_t = xcb_get_geometry(getConnectionRef(),
																					winId);
						return cookie_t;
					}
				});

		final ListenableFuture<Integer> borderFuture = transform(	geometryRequest,
																	new Function<xcb_get_geometry_cookie_t, Integer>() {
																		@Override
																		public
																				Integer
																				apply(final xcb_get_geometry_cookie_t cookie_t) {
																			final xcb_generic_error_t e = new xcb_generic_error_t();
																			final xcb_get_geometry_reply_t reply = xcb_get_geometry_reply(	getConnectionRef(),
																																			cookie_t,
																																			e);

																			checkError(e);
																			return reply
																					.getBorder_width();
																		}
																	});

		return transform(	borderFuture,
							new Function<Integer, Void>() {
								@Override
								public Void apply(final Integer border) {

									final int borderAdjust = 2 * border
											.intValue();
									final int adjustedWidth = width
											- borderAdjust;
									final int adjustedHeight = height
											- borderAdjust;

									XWindow.moveResizeValueListBuffer.clear();
									XWindow.moveResizeValueListBuffer.putInt(x)
											.putInt(y).putInt(adjustedWidth)
											.putInt(adjustedHeight);
									final int winId = getWindowId();

									XWindow.logger
											.debug(	"[winId={}] move resize x={}, y={}, width={}, height={}.",
													winId,
													x,
													y,
													adjustedWidth,
													adjustedHeight);
									xcb_configure_window(	getConnectionRef(),
															winId,
															XWindow.moveResizeValueMask,
															XWindow.moveResizeValueListBuffer);
									xcb_flush(getConnectionRef());
									return null;
								}
							});
	}

	@Override
	public ListenableFuture<Void> raise() {
		final int winId = getWindowId();

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] raise.",
																winId);
												xcb_configure_window(	getConnectionRef(),
																		winId,
																		XWindow.raiseValueMask,
																		XWindow.raiseValueListBuffer);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> setParent(final DisplayArea parent,
											final int x,
											final int y) {

		final int parentId = ((Integer) ((DisplaySurface) parent)
				.getDisplaySurfaceHandle().getNativeHandle()).intValue();
		final int winId = getWindowId();

		return this.xExecutor.submit(	new Runnable() {

											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] set parent parentId={}, x={}, y={}.",
																parentId,
																x,
																y,
																winId);

												xcb_reparent_window(getConnectionRef(),
																	winId,
																	parentId,
																	(short) x,
																	(short) y);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Void> resize(	final int width,
											final int height) {
		// we have to adjust the size with the X border. This sucks because it
		// introduces an extra roundtrip to the X server. -_-

		final int winId = getWindowId();
		final ListenableFuture<xcb_get_geometry_cookie_t> geometryRequest = this.xExecutor
				.submit(new Callable<xcb_get_geometry_cookie_t>() {
					@Override
					public xcb_get_geometry_cookie_t call() {
						XWindow.logger
								.debug(	"[winId={}] get geometry request.",
										winId);

						final xcb_get_geometry_cookie_t cookie_t = xcb_get_geometry(getConnectionRef(),
																					winId);
						return cookie_t;
					}
				});

		final ListenableFuture<Integer> borderFuture = transform(	geometryRequest,
																	new Function<xcb_get_geometry_cookie_t, Integer>() {
																		@Override
																		public
																				Integer
																				apply(final xcb_get_geometry_cookie_t cookie_t) {
																			final xcb_generic_error_t e = new xcb_generic_error_t();
																			final xcb_get_geometry_reply_t reply = xcb_get_geometry_reply(	getConnectionRef(),
																																			cookie_t,
																																			e);

																			checkError(e);
																			return reply
																					.getBorder_width();
																		}
																	});

		return transform(	borderFuture,
							new Function<Integer, Void>() {
								@Override
								public Void apply(final Integer border) {
									final int borderAdjust = 2 * border
											.intValue();
									final int adjustedWidth = width
											- borderAdjust;
									final int adjustedHeight = height
											- borderAdjust;

									XWindow.logger
											.debug(	"[winId={}] resize width={}, height={}.",
													adjustedWidth,
													adjustedHeight,
													winId);

									XWindow.resizeValueList.clear();
									XWindow.resizeValueList
											.putInt(adjustedWidth)
											.putInt(adjustedHeight);
									xcb_configure_window(	getConnectionRef(),
															winId,
															XWindow.resizeValueMask,
															XWindow.resizeValueList);
									xcb_flush(getConnectionRef());
									return null;
								}
							});
	}

	@Override
	public ListenableFuture<Void> hide() {
		final int winId = getWindowId();
		return this.xExecutor.submit(	new Runnable() {
											@Override
											public void run() {
												XWindow.logger
														.debug(	"[winId={}] hide.",
																winId);

												xcb_unmap_window(	getConnectionRef(),
																	winId);
												xcb_flush(getConnectionRef());
											}
										},
										null);
	}

	@Override
	public ListenableFuture<Rectangle> getGeometry() {
		final int winId = getWindowId();

		final ListenableFuture<xcb_get_geometry_cookie_t> geometryRequest = this.xExecutor
				.submit(new Callable<xcb_get_geometry_cookie_t>() {
					@Override
					public xcb_get_geometry_cookie_t call() {
						XWindow.logger
								.debug(	"[winId={}] get geometry request.",
										winId);

						final xcb_get_geometry_cookie_t cookie_t = xcb_get_geometry(getConnectionRef(),
																					winId);
						return cookie_t;
					}
				});

		final ListenableFuture<Rectangle> geometryReply = transform(geometryRequest,
																	new AsyncFunction<xcb_get_geometry_cookie_t, Rectangle>() {
																		@Override
																		public
																				ListenableFuture<Rectangle>
																				apply(final xcb_get_geometry_cookie_t input) {
																			return getGeometryReply(input);
																		}
																	});
		return geometryReply;
	}

	protected ListenableFuture<Rectangle>
			getGeometryReply(final xcb_get_geometry_cookie_t cookie_t) {
		return this.xExecutor.submit(new Callable<Rectangle>() {
			@Override
			public Rectangle call() {
				XWindow.logger.debug("get geometry reply.");

				final xcb_generic_error_t e = new xcb_generic_error_t();
				final xcb_get_geometry_reply_t reply = xcb_get_geometry_reply(	getConnectionRef(),
																				cookie_t,
																				e);

				checkError(e);
				final int width = reply.getWidth() + 2
						* reply.getBorder_width();
				final int height = reply.getHeight() + 2
						* reply.getBorder_width();
				final int x = reply.getX();
				final int y = reply.getY();

				return new ImmutableRectangle(	x,
												y,
												width,
												height);
			}
		});
	}

	private void checkError(final xcb_generic_error_t e) {
		if (xcb_generic_error_t.getCPtr(e) != 0) {
			XWindow.logger.error(	"X error: {}.",
									XcbErrorUtil.toString(e));
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof XWindow) {
			final XWindow otherWindow = (XWindow) obj;
			return otherWindow.getDisplaySurfaceHandle().getNativeHandle()
					.equals(getDisplaySurfaceHandle().getNativeHandle());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getDisplaySurfaceHandle().getNativeHandle().hashCode();
	}

	/***************************************
	 * Enable events coming from a client. Calling this method marks a window as
	 * a client window.
	 * <p>
	 * Should only be called by the Display thread.
	 *************************************** 
	 */
	public void configureClientEvents() {

		final ByteBuffer values = ByteBuffer.allocateDirect(4)
				.order(ByteOrder.nativeOrder());
		values.putInt(xcb_event_mask_t.XCB_EVENT_MASK_PROPERTY_CHANGE //
				| xcb_event_mask_t.XCB_EVENT_MASK_ENTER_WINDOW //
				| xcb_event_mask_t.XCB_EVENT_MASK_LEAVE_WINDOW //
				| xcb_event_mask_t.XCB_EVENT_MASK_STRUCTURE_NOTIFY//
		);
		final int winId = getWindowId();

		XWindow.logger.debug(	"[winId={}] configure client evens.",
								winId);

		xcb_change_window_attributes(	XWindow.this.xConnection.getConnectionReference(),
										winId,
										xcb_cw_t.XCB_CW_EVENT_MASK,
										values);
		xcb_flush(getConnectionRef());
	}

	@Override
	public String toString() {
		return String.format(	"%s=%s",
								getClass().getSimpleName(),
								getDisplaySurfaceHandle().getNativeHandle());
	}
}