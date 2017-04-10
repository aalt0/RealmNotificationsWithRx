package me.realmupdateswithrx;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolderItem> {

    private final MainPresenter presenter;
    private final List<ItemVm> items = new ArrayList<>();

    ItemAdapter(MainPresenter presenter) {
        this.presenter = presenter;
    }

    void setItems(List<ItemVm> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    List<ItemVm> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(this.items));
    }

    @Override
    public ViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolderItem(v);
    }

    @Override
    public void onBindViewHolder(ViewHolderItem holder, int position) {
        ItemVm itemVm = items.get(position);
        holder.bind(itemVm);
        int itemId = itemVm.getId();
        holder.deleteItem.setOnClickListener(view -> presenter.deleteItem(itemId));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolderItem extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.delete_item)
        Button deleteItem;

        ViewHolderItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(ItemVm itemVm) {
            title.setText(itemVm.getTitle());
            content.setText(itemVm.getContent());
        }
    }
}
