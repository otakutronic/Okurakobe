package mji.tapia.com.service.error_manager;

import android.os.Looper;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Sami on 2/1/2018.
 */

public class ErrorManagerImpl implements ErrorManager {



    private PublishSubject<Throwable> backgroundThreadPublishSubject = PublishSubject.create();
    private PublishSubject<Throwable> mainThreadPublishSubject = PublishSubject.create();

    public ErrorManagerImpl() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if(Looper.getMainLooper().getThread() == thread) {
                //main thread exception
                mainThreadPublishSubject.onNext(throwable);
            } else {
                backgroundThreadPublishSubject.onNext(throwable);
            }
        });
    }

    @Override
    public Observable<Throwable> getMainThreadExceptionObserver() {
        return mainThreadPublishSubject;
    }

    @Override
    public Observable<Throwable> getBackgroundThreadsExceptionObserver() {
        return backgroundThreadPublishSubject;
    }
}
