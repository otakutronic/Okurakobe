package mji.tapia.com.okurahotel.configuration;

import java.util.Iterator;
import java.util.LinkedList;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


//manage actions for mvp views
public final class ViewActionQueue<View> {

    private final LinkedList<Consumer<View>> viewActions = new LinkedList<>();
    private final Object queueLock = new Object();

    private final PublishSubject<Consumer<View>> viewActionSubject = PublishSubject.create();
    private final CompositeDisposable subscriptions = new CompositeDisposable();

    private final Scheduler observeScheduler;

    public ViewActionQueue(final Scheduler observeScheduler) {
        this.observeScheduler = observeScheduler;
    }

    private boolean isPaused;

    public void subscribeTo(final Observable<Consumer<View>> observable, final Consumer<View> onCompleteAction, final Consumer<Throwable> errorAction) {
        subscriptions.add(observable.observeOn(observeScheduler).subscribe(this::onResult, errorAction::accept, () -> onResult(onCompleteAction)));
    }

    public void subscribeTo(final Single<Consumer<View>> single, final Consumer<Throwable> errorAction) {
        subscriptions.add(single.observeOn(observeScheduler).subscribe(this::onResult, errorAction::accept));
    }

    public void subscribeTo(final Completable completable, final Consumer<View> onCompleteAction, final Consumer<Throwable> errorAction) {
        subscriptions.add(completable.observeOn(observeScheduler).subscribe(() -> onResult(onCompleteAction), errorAction::accept));
    }

    private void onResult(final Consumer<View> resultAction) {
        if (isPaused) {
            synchronized (queueLock) {
                viewActions.add(resultAction);
            }
        } else {
            viewActionSubject.onNext(resultAction);
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        consumeQueue();
    }

    public void destroy() {

        subscriptions.dispose();
        viewActionSubject.onComplete();
    }

    private void consumeQueue() {
        synchronized (queueLock) {
            final Iterator<Consumer<View>> actionIterator = viewActions.iterator();
            while (actionIterator.hasNext()) {
                final Consumer<View> pendingViewAction = actionIterator.next();
                viewActionSubject.onNext(pendingViewAction);
                actionIterator.remove();
            }
        }
    }

    public Observable<Consumer<View>> viewActionsObservable() {
        return viewActionSubject;
    }
}
