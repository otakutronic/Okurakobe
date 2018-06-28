package mji.tapia.com.data.error_log;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Sami on 2/1/2018.
 */

public interface ErrorLogRepository {

    Single<List<ErrorLog>> getErrorLogs();

    Single<List<ErrorLog>> getErrorLogByType(ErrorLog.Type type);

    Completable deleteErrorLog(ErrorLog errorLog);

    Completable addErrorLog(ErrorLog errorLog);
}
