package me.realmupdateswithrx.domain;

import com.jakewharton.rxrelay.PublishRelay;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import me.realmupdateswithrx.ItemVm;
import me.realmupdateswithrx.model.Item;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainDomain {

    private final PublishRelay<List<ItemVm>> itemVmsChangeRelay = PublishRelay.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final RealmRepository repository;

    public MainDomain() {
        this.repository = new RealmRepository();
        connectDataChangeObservable();
    }

    public Observable<List<ItemVm>> getDataChangeObservable() {
        return itemVmsChangeRelay
                .doOnUnsubscribe(this::disconnectDataChangeObservable);
    }

    public void addItem() {
        repository.getNextItemId()
                .map(MainDomain::createDummyItem)
                .subscribe(
                        repository::upsert,
                        e -> Timber.e(e, "failed to upsert to realm")
                );
    }

    public void deleteItem(int id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    private void connectDataChangeObservable() {
        Timber.i("connectDataChangeObservable()");
        subscriptions.add(
                repository.getItemsAsObservable()
                        .map(MainDomain::mapItemsToItemVms)
                        .onErrorReturn(throwable -> Collections.emptyList())
                        .subscribe(
                                itemVmsChangeRelay,
                                e -> Timber.e(e, "item change observable error")
                        )
        );
    }

    private void disconnectDataChangeObservable() {
        Timber.i("disconnectDataChangeObservable()");
        if (!subscriptions.isUnsubscribed()) {
            subscriptions.clear();
        }
    }

    static private List<ItemVm> mapItemsToItemVms(List<Item> items) {
        return Observable.from(items)
                .map(ItemVm::new)
                .toList()
                .toBlocking()
                .single();
    }

    private static Item createDummyItem(int id) {
        String title = String.format(Locale.US, "%s %d", "Title", id);
        String content = String.format(Locale.US, "%s %d", "Content for item", id);
        return new Item(id, title, content);
    }
}
