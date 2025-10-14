package org.infinitytwo.umbralore.data;

import org.infinitytwo.umbralore.RGB;

public record TextComponent(String text, RGB color) {

    @Override
    public String toString() {
        return text;
    }
}
