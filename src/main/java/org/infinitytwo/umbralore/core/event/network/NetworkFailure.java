package org.infinitytwo.umbralore.core.event.network;

import org.infinitytwo.umbralore.core.event.Event;

public class NetworkFailure extends Event {
    public final Exception exception;

    public NetworkFailure(Exception e) {
        exception = e;
    }
}
