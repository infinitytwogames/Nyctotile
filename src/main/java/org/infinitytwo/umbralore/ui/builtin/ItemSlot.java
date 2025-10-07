package org.infinitytwo.umbralore.ui.builtin;

import org.infinitytwo.umbralore.debug.Main;
import org.infinitytwo.umbralore.event.input.MouseButtonEvent;
import org.infinitytwo.umbralore.event.input.MouseHoverEvent;
import org.infinitytwo.umbralore.item.Item;
import org.infinitytwo.umbralore.registry.ItemRegistry;
import org.infinitytwo.umbralore.renderer.FontRenderer;
import org.infinitytwo.umbralore.ui.Screen;
import org.infinitytwo.umbralore.ui.UI;
import org.infinitytwo.umbralore.ui.component.ItemHolder;

public class ItemSlot extends UI {
    public ItemHolder item;

    public ItemSlot(Screen screen, FontRenderer renderer) {
        super(screen.getUIBatchRenderer());
        item = new ItemHolder(ItemRegistry.getTextureAtlas(), screen, ItemRegistry.getMainRegistry(), renderer);
    }

    @Override
    public void draw() {
        super.draw();
        item.draw();
    }

    @Override
    public void onMouseClicked(MouseButtonEvent e) {
        if (Main.getMouse().getItem() != null && item.getItem() != null) {
            Item mouseItem = Main.getMouse().getItem();
            Item slotItem = item.getItem();

            if (mouseItem.getType() == slotItem.getType()) {
                int newCount = slotItem.getCount() + mouseItem.getCount();
                slotItem.setCount(newCount);
                Main.getMouse().setItem(null);
            }
        }
    }

    @Override
    public void onMouseHover(MouseHoverEvent e) {
        if (item.getItem() != null)
            item.getScreen().showTooltip(item.getItem().getType().getName());
    }

    @Override
    public void onMouseHoverEnded() {
        item.getScreen().hideTooltip();
    }

    @Override
    public void cleanup() {

    }

    public void setItem(Item item) {
        this.item.setItem(item);
    }

    public Item getItem() {
        return item.getItem();
    }
}
