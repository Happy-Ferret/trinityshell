package org.trinity.wayland.defaul.protocol;

import org.freedesktop.wayland.server.Client;
import org.freedesktop.wayland.server.Resource;
import org.trinity.common.Listenable;
import org.trinity.wayland.defaul.events.ResourceDestroyed;

import java.util.Set;

/**
 * Created by Erik De Rijcke on 6/3/14.
 */
public interface ProtocolObject<T extends Resource> extends Listenable {
    Set<T> getResources();

    default T add(final Client client,
                  final int version,
                  final int id){
        final T resource = create(client,
                                  version,
                                  id);
        resource.setImplementation(this);
        getResources().add(resource);
        return  resource;
    }

    T create(final Client client,
             final int version,
             final int id);

    default void destroy(final T resource){
        post(new ResourceDestroyed(resource));
        getResources().remove(resource);
        resource.destroy();
    }
}