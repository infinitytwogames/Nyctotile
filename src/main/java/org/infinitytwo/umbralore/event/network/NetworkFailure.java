package org.infinitytwo.umbralore.event.network;

import org.infinitytwo.umbralore.event.Event;

public class NetworkFailure extends Event {
    public final Exception exception;

    public NetworkFailure(Exception e) {
        exception = e;
    }
}
