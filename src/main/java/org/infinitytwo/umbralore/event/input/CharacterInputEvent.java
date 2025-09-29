package org.infinitytwo.umbralore.event.input;

import org.infinitytwo.umbralore.event.Event;

public class CharacterInputEvent extends Event {
    public final int codepoint;
    public final String character;

    public CharacterInputEvent(int codepoint, char[] chars) {
        super();
        this.codepoint = codepoint;
        character = String.valueOf(chars);
    }
}
