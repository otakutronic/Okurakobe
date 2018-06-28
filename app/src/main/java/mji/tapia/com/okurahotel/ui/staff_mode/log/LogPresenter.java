package mji.tapia.com.okurahotel.ui.staff_mode.log;

import java.util.List;

import javax.inject.Inject;

import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.data.error_log.ErrorLogRepository;
import mji.tapia.com.data.error_log.ErrorLogRepositoryImpl;
import mji.tapia.com.okurahotel.BasePresenter;

/**
 * Created by Sami on 2/1/2018.
 */

public class LogPresenter extends BasePresenter<LogContract.View> implements LogContract.Presenter {

    @Inject
    ErrorLogRepository errorLogRepository;

    private ErrorLog.Type filterType = null;

    public LogPresenter(LogContract.View view) {
        super(view);
    }

    @Override
    public void activate() {
        super.activate();
        updateErrorLogs();
    }

    @Override
    public void back() {
        router.closeScreen();
    }

    @Override
    public void onRuntimeSelect() {
        filterType = ErrorLog.Type.RUNTIME;
        updateErrorLogs();
    }

    @Override
    public void onWarningSelect() {
        filterType = ErrorLog.Type.WARNING;
        updateErrorLogs();
    }

    @Override
    public void onAllSelect() {
        filterType = null;
        updateErrorLogs();
    }

    private void updateErrorLogs() {
        if(filterType == null) {
            errorLogRepository.getErrorLogs().subscribe(errorLogs -> doIfViewNotNull(view -> view.setErrorLogList(errorLogs)));
        } else {
            errorLogRepository.getErrorLogByType(filterType).subscribe(errorLogs -> doIfViewNotNull(view -> view.setErrorLogList(errorLogs)));
        }
    }
}
