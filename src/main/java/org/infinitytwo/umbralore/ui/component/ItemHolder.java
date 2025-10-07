package org.infinitytwo.umbralore.ui.component;

import org.infinitytwo.umbralore.item.Item;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.registry.ItemRegistry;
import org.infinitytwo.umbralore.renderer.FontRenderer;
import org.infinitytwo.umbralore.ui.Screen;
import org.infinitytwo.umbralore.ui.position.Anchor;
import org.infinitytwo.umbralore.ui.position.Pivot;
import org.joml.Vector2i;

public class ItemHolder extends TextureComponent {
    private final ItemRegistry registry;
    private final Text text;
    private Item item;
    private final int padding = 2;

    public ItemHolder(TextureAtlas atlas, Screen screen, ItemRegistry registry, FontRenderer fontRenderer) {
        super(0, atlas, screen.getUIBatchRenderer());
        this.registry = registry;

        text = new Text(fontRenderer, screen);
        text.setParent(this);
        text.setPosition(new Anchor(0, 1), new Pivot(0, 1), new Vector2i(padding, -padding));
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        if (item == null) {
            setTextureIndex(-1);
            text.setText("");
            this.item = null;
            return;
        }

        this.item = item;
        setTextureIndex(registry.getTextureIndex(item.getType().getIndex()));

        // Display stack size if > 1
        if (item.getCount() > 1)
            text.setText(String.valueOf(item.getCount()));
        else
            text.setText("");
    }

    @Override
    public void draw() {
        if (textureIndex >= 0) super.draw();
        text.setText(String.valueOf(item.getCount()));
        text.draw();
    }

    public Screen getScreen() {
        return text.screen;
    }
}
