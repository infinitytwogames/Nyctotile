package org.infinitytwo.umbralore.core.event.input;

import org.infinitytwo.umbralore.core.event.Event;

public class CharacterInputEvent extends Event {
    public final int codepoint;
    public final String character;

    public CharacterInputEvent(int codepoint, char[] chars) {
        super();
        this.codepoint = codepoint;
        character = String.valueOf(chars);
    }
}
