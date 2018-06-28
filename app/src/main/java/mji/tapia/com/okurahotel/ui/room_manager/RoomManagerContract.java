package mji.tapia.com.okurahotel.ui.room_manager;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

public interface RoomManagerContract {
    interface View extends BaseView {
    }

    interface Presenter extends ScopedPresenter {

        void selectAirCon();

        void selectLight();

        void selectExit();

        void selectCurtain();
    }
}
