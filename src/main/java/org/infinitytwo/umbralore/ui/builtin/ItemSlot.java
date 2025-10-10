package org.infinitytwo.umbralore.ui.builtin;

import org.infinitytwo.umbralore.Mouse;
import org.infinitytwo.umbralore.RGBA;
import org.infinitytwo.umbralore.Window;
import org.infinitytwo.umbralore.debug.Main;
import org.infinitytwo.umbralore.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.item.Item;
import org.infinitytwo.umbralore.model.TextureAtlas;
import org.infinitytwo.umbralore.registry.ItemRegistry;
import org.infinitytwo.umbralore.registry.ResourceManager;
import org.infinitytwo.umbralore.renderer.FontRenderer;
import org.infinitytwo.umbralore.ui.Screen;
import org.infinitytwo.umbralore.ui.UI;
import org.infinitytwo.umbralore.ui.component.ItemHolder;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class ItemSlot extends UI {
    public final ItemHolder item;

    public ItemSlot(Screen screen, FontRenderer fontRenderer, Window window) {
        super(screen.getUIBatchRenderer());
        item = new ItemHolder(ResourceManager.items,screen,0,fontRenderer);
        screen.register(this);
    }

    public Item getItem() {
        return item.getItem();
    }

    public void setItem(Item item) {
        if (item == null) {
            // --- FIX: CLEAR THE ITEM HOLDER STATE ---
            this.item.setItem(null);           // Set the item reference to null
            this.item.setTextureIndex(-1);     // Set a known 'empty' texture index
            // Optionally, you might draw a background frame here, but we'll
            // rely on the ItemHolder to not draw the item texture for now.
        } else {
            this.item.setItem(item);
            // Ensure this returns a non-negative index for a valid texture
            int textureIndex = ItemRegistry.getMainRegistry().getTextureIndex(item.getType().getIndex());
            this.item.setTextureIndex(textureIndex);
        }
    }

    public Screen getScreen() {
        return item.getScreen();
    }

    public RGBA getForegroundColor() {
        return item.getForegroundColor();
    }

    public void setForegroundColor(RGBA foregroundColor) {
        item.setForegroundColor(foregroundColor);
    }

    public int getTextureIndex() {
        return item.getTextureIndex();
    }

    public void setTextureIndex(int textureIndex) {
        item.setTextureIndex(textureIndex);
    }

    public TextureAtlas getAtlas() {
        return item.getAtlas();
    }

    public void setAtlas(TextureAtlas atlas) {
        item.setAtlas(atlas);
    }

    @Override
    public void draw() {
        item.setSize(width,height);
        item.setPosition(anchor,pivot,offset);
        super.draw();
        item.draw();
    }

    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (e.action == GLFW_PRESS) {
            if (Mouse.getItem() != null && getItem() != null &&
                getItem().getType() == Mouse.getItem().getType()) {
                item.setCount(item.getCount() + Mouse.getItem().getCount());
                Mouse.setItem(null);
            } else {
                Item i = getItem();
                Mouse.renderUsing(getAtlas(),getTextureIndex());
                setItem(Mouse.getItem());
                Mouse.setItem(i);
            }
        }
    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {

    }

    @Override
    public void onMouseHoverEnded() {

    }

    @Override
    public void cleanup() {

    }
}
