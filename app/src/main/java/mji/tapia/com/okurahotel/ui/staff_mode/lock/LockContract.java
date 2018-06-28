package mji.tapia.com.okurahotel.ui.staff_mode.lock;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 1/29/2018.
 */

public class LockContract {
    interface View extends BaseView {
    }

    interface Presenter extends ScopedPresenter {
        void onPin(String pinCode);
    }
}
