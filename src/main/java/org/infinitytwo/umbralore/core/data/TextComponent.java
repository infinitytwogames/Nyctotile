package org.infinitytwo.umbralore.core.data;

import org.infinitytwo.umbralore.core.RGB;

public record TextComponent(String text, RGB color) {

    @Override
    public String toString() {
        return text;
    }
}
