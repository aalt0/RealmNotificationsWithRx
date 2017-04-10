package me.realmupdateswithrx;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        presenter = new MainPresenter(this);

        ItemAdapter adapter = new ItemAdapter(presenter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setItems(List<ItemVm> newItems) {
        getItemAdapter().setItems(newItems);
    }

    @Override
    public List<ItemVm> getItems() {
        return getItemAdapter().getItems();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.add_item)
    public void onAddItemClicked(Button button) {
        presenter.addItem();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.delete_all)
    public void onDeleteAllClicked(Button button) {
        presenter.deleteAll();
    }

    private ItemAdapter getItemAdapter() {
        return (ItemAdapter) recyclerView.getAdapter();
    }

}
