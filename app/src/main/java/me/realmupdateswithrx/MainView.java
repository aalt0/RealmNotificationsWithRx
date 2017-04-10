package me.realmupdateswithrx;

import java.util.List;

interface MainView {

    void setItems(List<ItemVm> newItems);
    List<ItemVm> getItems();

    class NoOpMainView implements MainView {
        @Override
        public void setItems(List<ItemVm> newItems) {

        }

        @Override
        public List<ItemVm> getItems() {
            return null;
        }
    }
}
