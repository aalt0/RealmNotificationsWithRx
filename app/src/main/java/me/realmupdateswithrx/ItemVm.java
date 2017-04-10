package me.realmupdateswithrx;

import me.realmupdateswithrx.model.Item;

public class ItemVm {

    private final Item item;

    public ItemVm(Item item) {
        this.item = item;
    }

    public int getId() {
        return item.getId();
    }

    String getTitle() {
        return item == null ? "" : item.getTitle();
    }

    String getContent() {
        return item == null ? "" : item.getContent();
    }
}
