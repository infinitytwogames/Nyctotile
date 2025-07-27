package org.infinitytwo.umbralore.core.exception;

public class ChannelDoesNotExist extends VerboseException {
    public ChannelDoesNotExist(String message) {
        super(message, "The Event channel is not registered by EventBus or cleared");
    }
}
