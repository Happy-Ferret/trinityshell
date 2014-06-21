package org.trinity.wayland.defaul.protocol;

import dagger.Module;
import dagger.Provides;
import org.trinity.wayland.defaul.WlDefaultModule;

import javax.inject.Singleton;

/**
 * Created by Erik De Rijcke on 5/22/14.
 */
@Module(
        includes = {
                WlDefaultModule.class
        },
        staticInjections = {
          EagerSingletons.class
        },
        injects = {
                WlSeatFactory.class,
                WlDataDeviceFactory.class,
                WlDataDeviceFactory.class,
                WlShmPoolFactory.class,
                WlShmBufferFactory.class,
                WlSurfaceFactory.class,
                WlRegionFactory.class,
                WlShellSurfaceFactory.class,
                WlSubSurfaceFactory.class
        },
        library = true
)
public class WlProtocolModule {

    @Provides
    @Singleton
    WlSubCompositor provideWlSubCompositor(final WlSubSurfaceFactory wlSubSurfaceFactory){
        return new WlSubCompositor(wlSubSurfaceFactory);
    }

    @Provides
    @Singleton
    WlPointer provideWlPointer(){
        return new WlPointer();
    }

    @Provides
    @Singleton
    WlKeyboard provideWlKeyboard(){
        return new WlKeyboard();
    }

    @Provides
    @Singleton
    WlTouch provideWlTouch(){
        return new WlTouch();
    }
}