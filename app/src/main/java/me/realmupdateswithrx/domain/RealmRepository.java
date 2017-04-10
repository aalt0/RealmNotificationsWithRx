package me.realmupdateswithrx.domain;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import me.realmupdateswithrx.model.Item;
import rx.Observable;
import rx.Scheduler;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;

public class RealmRepository {

    static private final HandlerThread backgroundThread;

    static {
        backgroundThread = new HandlerThread("realm_bg_thread", Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
    }

    private static Scheduler getRealmScheduler() {
        return AndroidSchedulers.from(backgroundThread.getLooper());
    }

    private Realm realm;

    void upsert(Item item) {
        execute(theRealm -> theRealm.insertOrUpdate(item));
    }

    void deleteById(int modelId) {
        execute(theRealm ->
                theRealm.where(Item.class)
                        .equalTo("id", modelId)
                        .findAll()
                        .deleteAllFromRealm());
    }

    void deleteAll() {
        execute(theRealm -> theRealm.deleteAll());
    }

    Observable<List<Item>> getItemsAsObservable() {
        return Observable.defer(() ->
                getRealm().where(Item.class)
                        .findAllSorted("title", Sort.ASCENDING)
                        .asObservable())
                .subscribeOn(getRealmScheduler())
                .unsubscribeOn(getRealmScheduler())
//                .doOnUnsubscribe(this::deleteRealm)
                .map(theMemberships -> getRealm().copyFromRealm(theMemberships));
    }

    Single<Integer> getNextItemId() {
        return Observable.fromCallable(() -> getRealm().where(Item.class).max("id").intValue() + 1)
                .subscribeOn(getRealmScheduler())
                .onErrorReturn(throwable -> 0)
                .toSingle();
    }

    private void execute(Realm.Transaction transaction) {
        new Handler(backgroundThread.getLooper()).post(() ->
                getRealm().executeTransaction(transaction));
    }

    // use single realm instance to avoid overhead with opening and closing often
    private Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            realm = Realm.getDefaultInstance();
        }
        return realm;
    }

//    private void deleteRealm() {
//        Timber.i("deleteAll()");
//        new Handler(backgroundThread.getLooper()).post(() -> {
//            try {
//                getRealm().executeTransaction(theRealm -> theRealm.deleteAll());
//                getRealm().close();
//                boolean deleteSuccess = Realm.deleteRealm(getRealm().getConfiguration());
//                Timber.d("deleteRealm()", deleteSuccess);
//            } catch (IllegalStateException e) {
//                Timber.e(e, "failed to delete realm");
//            }
//        });
//    }

}
