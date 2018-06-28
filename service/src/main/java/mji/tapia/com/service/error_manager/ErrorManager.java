package mji.tapia.com.service.error_manager;


import io.reactivex.Observable;

/**
 * Created by Sami on 2/1/2018.
 */

public interface ErrorManager {

    Observable<Throwable> getMainThreadExceptionObserver();

    Observable<Throwable> getBackgroundThreadsExceptionObserver();
}
