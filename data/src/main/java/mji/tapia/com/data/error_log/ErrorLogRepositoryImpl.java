package mji.tapia.com.data.error_log;

import android.arch.persistence.room.Room;
import android.util.Log;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.SingleSubject;
import mji.tapia.com.data.AppDatabase;
import mji.tapia.com.service.error_manager.ErrorManager;

/**
 * Created by Sami on 2/1/2018.
 */

public class ErrorLogRepositoryImpl implements ErrorLogRepository {

    private static final String TAG = "ErrorLogRepository";

    private AppDatabase appDatabase;

    public ErrorLogRepositoryImpl(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }


    @Override
    public Single<List<ErrorLog>> getErrorLogs() {
        SingleSubject<List<ErrorLog>> listSingleSubject = SingleSubject.create();
        Schedulers.io().scheduleDirect(() -> listSingleSubject.onSuccess(appDatabase.errorLogDao().getAll()));
        return listSingleSubject;
    }

    @Override
    public Single<List<ErrorLog>> getErrorLogByType(ErrorLog.Type type) {
        SingleSubject<List<ErrorLog>> listSingleSubject = SingleSubject.create();
        Schedulers.io().scheduleDirect(() -> listSingleSubject.onSuccess(appDatabase.errorLogDao().loadAllByType(type)));
        return listSingleSubject;
    }

    @Override
    public Completable deleteErrorLog(ErrorLog errorLog) {
        CompletableSubject completableSubject = CompletableSubject.create();
        Schedulers.io().scheduleDirect(() -> {
            appDatabase.errorLogDao().delete(errorLog);
            completableSubject.onComplete();
        });

        return completableSubject;
    }

    @Override
    public Completable addErrorLog(ErrorLog errorLog) {
        CompletableSubject completableSubject = CompletableSubject.create();
        Schedulers.io().scheduleDirect(() -> {
            appDatabase.errorLogDao().insert(errorLog);
            completableSubject.onComplete();
        });

        return completableSubject;
    }
}
