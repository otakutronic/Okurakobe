package mji.tapia.com.okurahotel.ui.room_manager.light;

import android.view.View;
import android.widget.Button;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

interface LightContract {
    interface View extends BaseView {
        void setLightSwitch(boolean state, boolean immediate);
        void setLightButton(CommandType commandType, boolean state);
    }

    interface Presenter extends ScopedPresenter {
        void sendCommand(CommandType commandType, boolean state);
    }
}
