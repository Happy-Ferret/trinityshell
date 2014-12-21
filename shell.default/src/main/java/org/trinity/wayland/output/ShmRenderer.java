package org.trinity.wayland.output;

import com.google.auto.factory.AutoFactory;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.freedesktop.wayland.server.ShmBuffer;
import org.freedesktop.wayland.server.WlBufferResource;
import org.trinity.shell.scene.api.ShellSurface;
import org.trinity.wayland.protocol.WlSurface;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@AutoFactory(className = "ShmRendererFactory")
public class ShmRenderer {

    private final EventBus dispatcher = new EventBus();

    private final ShmRenderEngine shmRenderEngine;

    private ShellSurface current;

    @Inject
    ShmRenderer(final ShmRenderEngine shmRenderEngine) {
        this.shmRenderEngine = shmRenderEngine;
        this.dispatcher.register(this);
    }

    public void render(final WlSurface wlSurface) {
        this.current = wlSurface.getShellSurface();
        this.dispatcher.post(this.current.getBuffer()
                                         .get());
    }

    @Subscribe
    public void unknownBufferType(final DeadEvent deadEvent) {
        throw new IllegalArgumentException(String.format("Buffer %s is not a known type.",
                                                         deadEvent.getEvent()
                                                                  .getClass()
                                                                  .getName()));
    }

    @Subscribe
    public void render(final WlBufferResource bufferResource) throws ExecutionException, InterruptedException {

        final ShmBuffer shmBuffer = ShmBuffer.get(bufferResource);
        if (shmBuffer == null) {
            throw new IllegalArgumentException("Buffer is not an ShmBuffer.");
        }

        this.shmRenderEngine.draw(this.current,
                                  shmBuffer)
                            .get();
        this.current.firePaintCallbacks((int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
    }

    public void beginRender() throws ExecutionException, InterruptedException {
        this.shmRenderEngine.begin()
                            .get();
    }

    public void endRender() throws ExecutionException, InterruptedException {
        this.shmRenderEngine.end()
                            .get();
    }
}
