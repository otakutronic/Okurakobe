package mji.tapia.com.okurahotel.ui.staff_mode.log;

import java.util.List;

import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 2/1/2018.
 */

public interface LogContract {

    interface View extends BaseView{
        void setErrorLogList(List<ErrorLog> errorLogList);
    }

    interface Presenter extends ScopedPresenter {
        void onRuntimeSelect();
        void onWarningSelect();
        void onAllSelect();
    }
}
