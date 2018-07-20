package mji.tapia.com.okurahotel.ui.room_manager.curtain;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

public interface CurtainContract {
    interface View extends BaseView {
    }

    interface Presenter extends ScopedPresenter {

        void openCurtains();

        void stopCurtains();

        void closeCurtains();
    }
}
