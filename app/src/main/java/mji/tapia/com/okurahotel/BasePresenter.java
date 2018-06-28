package mji.tapia.com.okurahotel;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.reactivestreams.Subscription;

import java.lang.ref.WeakReference;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import mji.tapia.com.okurahotel.configuration.ViewActionQueue;
import mji.tapia.com.okurahotel.configuration.ViewActionQueueProvider;
import mji.tapia.com.okurahotel.dagger.application.module.ThreadingModule;
import mji.tapia.com.service.connectivity.ConnectivityReceiver;

/**
 * Created by Sami on 8/8/2017.
 */

public abstract class BasePresenter<View extends BaseView> implements ScopedPresenter {

    @Inject
    protected ViewActionQueueProvider viewActionQueueProvider;

    @Inject
    protected ConnectivityReceiver connectivityReceiver;

    @Inject
    protected Router router;

    @Inject
    @Named(ThreadingModule.MAIN_SCHEDULER)
    Scheduler mainThreadScheduler;

    private WeakReference<View> viewReference = new WeakReference<>(null);
    private Disposable viewActionsDisposable;

    protected String viewId;
    protected ViewActionQueue<View> viewActionQueue;

    private CompositeDisposable disposables = new CompositeDisposable();

    public BasePresenter(final View view) {
        viewReference = new WeakReference<>(view);
    }

    @Override
    @CallSuper
    public void start() {
        viewId = getIfViewNotNull(BaseView::getViewId, "");
        viewActionQueue = viewActionQueueProvider.queueFor(viewId);
        subscribeToConnectivityChange();
    }

    private void subscribeToConnectivityChange() {
        addSubscription(connectivityReceiver.getConnectivityStatus()
                .observeOn(mainThreadScheduler)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onConnectivityChange, this::logError));
    }

    protected void onConnectivityChange(final boolean isConnected) {
        // Template method
    }

    @Override
    @CallSuper
    public void activate() {
        viewActionsDisposable = viewActionQueue.viewActionsObservable()
                .observeOn(mainThreadScheduler)
                .subscribe(this::onViewAction);
        viewActionQueue.resume();
    }

    protected void onViewAction(final Consumer<View> viewAction) {
        doIfViewNotNull(viewAction::accept);
    }

    @Override
    @CallSuper
    public void deactivate() {
        viewActionQueue.pause();
        viewActionsDisposable.dispose();
        disposables.clear();
    }

    @Override
    @CallSuper
    public void destroy() {
        viewActionQueue.destroy();
        viewActionQueueProvider.dispose(viewId);
    }

    @Override
    public void back() {
        router.goBack();
    }

    protected void addSubscription(final Disposable subscription) {
        if (disposables == null || disposables.isDisposed()) {
            disposables = new CompositeDisposable();
        }
        disposables.add(subscription);
    }

    protected final void doIfConnectedToInternet(final Action ifConnected, final Action ifNotConnected) {
        addSubscription(connectivityReceiver.isConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(mainThreadScheduler)
                .subscribe(isConnected -> onConnectedToInternet(isConnected, ifConnected, ifNotConnected), this::logError)
        );
    }

    private void onConnectedToInternet(final boolean isConnected, final Action ifConnected, final Action ifNotConnected) {
        try {
            ((isConnected) ? ifConnected : ifNotConnected).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final void logError(final Throwable throwable) {
        if (!TextUtils.isEmpty(throwable.getMessage())) {
            // ErrorDialog reporting, Crashlytics in example
            Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
        }
    }

    protected void doIfViewNotNull(final Consumer<View> whenViewNotNull) {
        final View view = getNullableView();
        if (view != null) {
            try {
                whenViewNotNull.accept(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected <R> R getIfViewNotNull(final Function<View, R> whenViewNotNull, final R defaultValue) {
        final View view = getNullableView();
        if (view != null) {
            try {
                return whenViewNotNull.apply(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    @Nullable
    protected View getNullableView() {
        return viewReference.get();
    }
}
