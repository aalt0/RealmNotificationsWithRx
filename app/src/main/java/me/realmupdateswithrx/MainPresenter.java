package me.realmupdateswithrx;

import java.util.List;

import me.realmupdateswithrx.domain.MainDomain;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainPresenter implements BasePresenter {

    private MainView view;
    private MainDomain domain;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    public MainPresenter(MainView view) {
        this.view = view;
        this.domain = new MainDomain();
        startObservingDataChanges();
    }

    @Override
    public void onDiscard() {
        view = new MainView.NoOpMainView();
        if (!subscriptions.isUnsubscribed()) {
            subscriptions.clear();
        }
    }

    void deleteItem(int itemId) {
        Timber.i("deleteItem()");
        domain.deleteItem(itemId);
    }

    void deleteAll() {
        domain.deleteAll();
    }

    void addItem() {
        Timber.i("addItem()");
        domain.addItem();
    }

    private void handleDataChanged(List<ItemVm> items) {
        Timber.i("handleDataChanged() " + items.toString());
        view.setItems(items);
    }

    private void startObservingDataChanges() {
        subscriptions.add(
                domain.getDataChangeObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleDataChanged)
        );
    }

}
