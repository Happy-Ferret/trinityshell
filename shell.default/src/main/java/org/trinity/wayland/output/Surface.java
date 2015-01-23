/*******************************************************************************
 * Trinity Shell Copyright (C) 2011 Erik De Rijcke
 *
 * This file is part of Trinity Shell.
 *
 * Trinity Shell is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * Trinity Shell is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 ******************************************************************************/
package org.trinity.wayland.output;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import org.freedesktop.wayland.server.WlBufferResource;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.media.nativewindow.util.Point;
import javax.media.nativewindow.util.PointImmutable;
import javax.media.nativewindow.util.RectangleImmutable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;

@NotThreadSafe
@AutoFactory(className = "SurfaceFactory")
public class Surface extends EventBus implements SurfaceConfigurable {

    private final RegionFactory pixmanRegionFactory;

    //pending states
    @Nonnull
    private Optional<Region>           pendingOpaqueRegion = Optional.empty();
    @Nonnull
    private Optional<Region>           pendingInputRegion  = Optional.empty();
    @Nonnull
    private Optional<Region>           pendingDamage       = Optional.empty();
    @Nonnull
    private Optional<WlBufferResource> pendingBuffer       = Optional.empty();
    @Nonnull
    private float[]                    pendingTransform    = new float[]{
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
    };
    @Nonnull
    private Point                      pendingBufferOffset = new Point();

    //committed states
    @Nonnull
    private final List<IntConsumer>          callbacks    = Lists.newLinkedList();
    @Nonnull
    private       Optional<Region>           opaqueRegion = Optional.empty();
    @Nonnull
    private       Optional<Region>           inputRegion  = Optional.empty();
    @Nonnull
    private       Optional<Region>           damage       = Optional.empty();
    @Nonnull
    private       Optional<WlBufferResource> buffer       = Optional.empty();
    @Nonnull
    private       float[]                    transform    = new float[]{
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
    };
    @Nonnull
    private       Point                      position     = new Point();
    //additional server side states
    @Nonnull
    private       Boolean                    destroyed    = Boolean.FALSE;

    Surface(@Provided final RegionFactory pixmanRegionFactory,
            @Nonnull final Optional<WlBufferResource> optionalBuffer) {
        this.pixmanRegionFactory = pixmanRegionFactory;
        this.buffer = optionalBuffer;
    }

    public void accept(@Nonnull final SurfaceConfiguration config) {
        config.visit(this);
    }

    @Nonnull
    public List<IntConsumer> getPaintCallbacks() {
        return this.callbacks;
    }

    @Nonnull
    public Boolean isDestroyed() {
        return this.destroyed;
    }

    @Nonnull
    public float[] getTransform() {
        return this.transform;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable markDestroyed() {
        this.destroyed = true;
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable markDamaged(@Nonnull final RectangleImmutable damage) {
        this.pendingDamage = Optional.of(this.pendingDamage.orElse(this.pixmanRegionFactory.create())
                                                           .add(damage));
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable attachBuffer(@Nonnull final WlBufferResource buffer,
                                                 @Nonnull final Integer relX,
                                                 @Nonnull final Integer relY) {

        this.pendingBuffer = Optional.of(buffer);
        this.pendingBufferOffset = new Point(relX,
                                             relY);
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable setTransform(final float[] transform) {
        this.pendingTransform = transform;
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable removeTransform() {
        this.pendingTransform = new float[]{1,
                0,
                0,
                0,
                1,
                0,
                0,
                0,
                1};
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable detachBuffer() {
        this.pendingBuffer = Optional.empty();
        this.pendingDamage = Optional.empty();
        this.pendingBufferOffset = new Point();
        return this;
    }

    @Nonnull
    public PointImmutable getPosition() {
        return this.position;
    }

    @Nonnull
    public Optional<Region> getInputRegion() {
        return this.inputRegion;
    }

    @Nonnull
    public Optional<Region> getDamage() {
        return this.damage;
    }

    @Nonnull
    public Optional<Region> getOpaqueRegion() {
        return this.opaqueRegion;
    }

    @Nonnull
    public Optional<WlBufferResource> getBuffer() {
        return this.buffer;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable commit() {
        //flush
        this.transform = this.pendingTransform;
        if (this.buffer.isPresent()) {
            //signal client that the previous buffer can be reused as we will now use the
            //newly attached buffer.
            final WlBufferResource wlBufferResource = this.buffer.get();
            wlBufferResource.release();
        }
        this.buffer = this.pendingBuffer;
        this.position = this.position.translate(this.pendingBufferOffset);
        this.damage = this.pendingDamage;
        this.inputRegion = this.pendingInputRegion;
        this.opaqueRegion = this.pendingOpaqueRegion;
        //reset
        detachBuffer();
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable addCallback(final IntConsumer callback) {
        this.callbacks.add(callback);
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable removeOpaqueRegion() {
        this.pendingOpaqueRegion = Optional.empty();
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable setOpaqueRegion(@Nonnull final Region opaqueRegion) {
        this.pendingOpaqueRegion = Optional.of(opaqueRegion);
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable removeInputRegion() {
        this.pendingInputRegion = Optional.empty();
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable setInputRegion(@Nonnull final Region inputRegion) {
        this.pendingInputRegion = Optional.of(inputRegion);
        return this;
    }

    @Nonnull
    @Override
    public SurfaceConfigurable setPosition(@Nonnull final PointImmutable position) {
        this.position = new Point(position.getX(),
                                  position.getY());
        return this;
    }

    public Surface firePaintCallbacks(final int serial) {
        final List<IntConsumer> callbacks = new ArrayList<>(getPaintCallbacks());
        getPaintCallbacks().clear();
        callbacks.forEach(paintCallback -> paintCallback.accept(serial));
        return this;
    }
}