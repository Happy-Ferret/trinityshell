package org.trinity.wayland.output;

import com.google.common.util.concurrent.Service;
import com.sun.jna.Native;
import dagger.Module;
import dagger.Provides;
import org.freedesktop.wayland.server.Display;

import javax.inject.Singleton;

import static dagger.Provides.Type.SET;

@Module(
        injects = {
                ShmRendererFactory.class,
                CompositorFactory.class,
                Display.class,
                RegionFactory.class,
                SurfaceFactory.class
        },
        library = true,
        //needs render engine implementation, defined at startup.
        complete = false
)
public class OutputModule {

    @Provides
    @Singleton
    CLibrary provideCLibrary() {
        return (CLibrary) Native.loadLibrary(CLibrary.JNA_LIBRARY_NAME,
                                             CLibrary.class);
    }

    @Provides
    @Singleton
    Display provideDisplay() {
        return Display.create();
    }

    @Provides
    @Singleton
    Scene provideWlScene() {
        return new Scene();
    }

    @Singleton
    @Provides
    JobExecutor provideWlJobExecutor(final Display display,
                                     final CLibrary libc) {
        final int[] pipe = configure(pipe(libc),
                                     libc);
        final int pipeR = pipe[0];
        final int pipeWR = pipe[1];

        return new JobExecutor(display,
                               pipeR,
                               pipeWR,
                               libc);
    }

    private int[] pipe(final CLibrary libc) {
        final int[] pipeFds = new int[2];
        libc.pipe(pipeFds);
        return pipeFds;
    }

    private int[] configure(final int[] pipeFds,
                            final CLibrary libc) {
        final int readFd = pipeFds[0];
        final int writeFd = pipeFds[1];

        final int readFlags = libc.fcntl(readFd,
                                         CLibrary.F_GETFD,
                                         0);
        libc.fcntl(readFd,
                   CLibrary.F_SETFD,
                   readFlags | CLibrary.FD_CLOEXEC);

        final int writeFlags = libc.fcntl(writeFd,
                                          CLibrary.F_GETFD,
                                          0);
        libc.fcntl(writeFd,
                   CLibrary.F_SETFD,
                   writeFlags | CLibrary.FD_CLOEXEC);

        return pipeFds;
    }

    @Provides(type = SET)
    Service provideService(final ShellService shellService) {
        return shellService;
    }
}
