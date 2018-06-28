package mji.tapia.com.okurahotel.ui.sleep;

import java.util.Date;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

public interface SleepContract {
    interface View extends BaseView {

        void updateClock(Date date);

    }

    interface Presenter extends ScopedPresenter {
    }
}
