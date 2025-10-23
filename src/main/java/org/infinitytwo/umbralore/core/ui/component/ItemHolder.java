package org.infinitytwo.umbralore.core.ui.component;

import org.infinitytwo.umbralore.core.RGB;
import org.infinitytwo.umbralore.core.data.ItemType;
import org.infinitytwo.umbralore.core.data.Item;
import org.infinitytwo.umbralore.core.model.TextureAtlas;
import org.infinitytwo.umbralore.core.registry.ItemRegistry;
import org.infinitytwo.umbralore.core.renderer.FontRenderer;
import org.infinitytwo.umbralore.core.ui.display.Screen;
import org.infinitytwo.umbralore.core.ui.position.Anchor;
import org.infinitytwo.umbralore.core.ui.position.Pivot;
import org.joml.Vector2i;

public class ItemHolder extends TextureComponent {
    private final ItemRegistry registry;
    private final Text text;
    private Item item;
    private final int padding = 2;

    public ItemHolder(TextureAtlas atlas, Screen screen, int index, FontRenderer fontRenderer) {
        super(index, atlas, screen.getUIBatchRenderer());
        this.registry = null;

        text = new Text(fontRenderer, screen);
        text.setParent(this);
        text.setPosition(new Anchor(0, 1), new Pivot(0, 1), new Vector2i(padding, -padding));
        text.setColor(new RGB(0.75f,0.75f,0.75f));
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
    }

    public ItemType getType() {
        return item.getType();
    }

    public int getDurability() {
        return item.getDurability();
    }

    public void setDurability(int durability) {
        item.setDurability(durability);
    }

    public int getCount() {
        return item.getCount();
    }

    public void setCount(int count) {
        item.setCount(count);
    }

    public void damage(int amount) {
        item.damage(amount);
    }

    public void repair(int amount) {
        item.repair(amount);
    }

    public boolean isBroken() {
        return item.isBroken();
    }

    public boolean isStackable() {
        return item.isStackable();
    }

    @Override
    public void draw() {
        if (item == null) {
            return;
        }
        text.setText(item.getCount() == 1? "" : String.valueOf(item.getCount()));
        super.draw();
        text.draw();
    }

    public void renderUsing(TextureAtlas atlas, int index) {
        setTextureIndex(index);
        setAtlas(atlas);
    }

    public Screen getScreen() {
        return text.screen;
    }
}
