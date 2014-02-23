package org.trinity.foundation.display.x11.impl.event;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import org.freedesktop.xcb.LibXcbJNI;
import org.freedesktop.xcb.xcb_generic_event_t;
import org.freedesktop.xcb.xcb_unmap_notify_event_t;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.display.DisplaySurfaceHandle;
import org.trinity.foundation.api.display.event.HideNotify;
import org.trinity.foundation.display.x11.api.XWindowHandle;
import org.trinity.foundation.display.x11.impl.DisplaySurfacePoolImpl;

import static org.freedesktop.xcb.LibXcbJNI.xcb_unmap_notify_event_t_event_get;
import static org.freedesktop.xcb.LibXcbJNI.xcb_unmap_notify_event_t_window_get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@Deprecated
@RunWith(PowerMockRunner.class)
@PrepareForTest(LibXcbJNI.class)
public class TestUnmapNotifyHandler {
    @Mock
    private EventBus xEventBus;
    @Mock
    private DisplaySurfacePoolImpl xWindowPool;
    @InjectMocks
    private UnmapNotifyHandler unmapNotifyHandler;
    @Mock
    private xcb_generic_event_t xcb_generic_event;

    private final int childWindowId = 321;
    private final int targetWindowId = 123;

    @Test
    public void testEventHandling() {
        //given
        //a UnmapNotifyHandler
        //an xcb_generic_event_t

        //when
        //an xcb_generic_event_t event arrives
        final Optional<HideNotify> hideNotifyOptional = unmapNotifyHandler.handle(xcb_generic_event);

        //then
        //the xcb_unmap_notify_event_t is posted on the x event bus
        //the event is converted to a HideNotify
        verify(xEventBus).post(isA(xcb_unmap_notify_event_t.class));
        assertTrue(hideNotifyOptional.isPresent());
    }

    @Test
    public void testGetTargetClientUnmapped() {
        //given
        //a UnmapNotifyHandler
        //an xcb_generic_event_t from a client
        mockStatic(LibXcbJNI.class);
        when(xcb_unmap_notify_event_t_event_get(anyLong(),
                (xcb_unmap_notify_event_t) any())).thenReturn(targetWindowId);
        when(xcb_unmap_notify_event_t_window_get(anyLong(),
                (xcb_unmap_notify_event_t) any())).thenReturn(targetWindowId);

        final DisplaySurface displaySurface = mock(DisplaySurface.class);
        when(displaySurface.getDisplaySurfaceHandle()).thenReturn(new XWindowHandle(targetWindowId));
        when(xWindowPool.getDisplaySurface((DisplaySurfaceHandle) any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(final InvocationOnMock invocation) throws Throwable {
                final Object arg0 = invocation.getArguments()[0];
                final XWindowHandle xWindowHandle = (XWindowHandle) arg0;
                if (xWindowHandle != null && xWindowHandle.getNativeHandle().equals(targetWindowId)) {
                    return displaySurface;
                }
                return null;
            }
        });

        //when
        //the target of the xcb_generic_event_t event is requested
        final Optional<DisplaySurface> target = unmapNotifyHandler.getTarget(xcb_generic_event);

        //then
        //the correct DisplaySurface is returned
        final ArgumentCaptor<XWindowHandle> windowHandleArgumentCaptor = ArgumentCaptor.forClass(XWindowHandle.class);
        verify(xWindowPool).getDisplaySurface(windowHandleArgumentCaptor.capture());
        assertEquals((Integer) targetWindowId,
                windowHandleArgumentCaptor.getValue().getNativeHandle());
        assertTrue(target.isPresent());
    }

    @Test
    public void testGetTargetClientChildUnmapped() {
        //given
        //a UnmapNotifyHandler
        //an xcb_generic_event_t from a child of the client
        mockStatic(LibXcbJNI.class);
        when(xcb_unmap_notify_event_t_event_get(anyLong(),
                (xcb_unmap_notify_event_t) any())).thenReturn(targetWindowId);
        when(xcb_unmap_notify_event_t_window_get(anyLong(),
                (xcb_unmap_notify_event_t) any())).thenReturn(childWindowId);

        //when
        //the target of the xcb_generic_event_t event is requested
        final Optional<DisplaySurface> target = unmapNotifyHandler.getTarget(xcb_generic_event);

        //then
        //no DisplaySurface is returned
        assertFalse(target.isPresent());
    }
}
