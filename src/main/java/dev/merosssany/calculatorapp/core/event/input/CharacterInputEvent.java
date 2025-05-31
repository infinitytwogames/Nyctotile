package dev.merosssany.calculatorapp.core.event.input;

import dev.merosssany.calculatorapp.core.event.Event;

public class CharacterInputEvent extends Event {
    public int codepoint;

    public CharacterInputEvent(int codepoint) {
        super();
        this.codepoint = codepoint;
    }
}
