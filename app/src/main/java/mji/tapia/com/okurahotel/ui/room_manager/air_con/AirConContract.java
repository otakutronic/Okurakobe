package mji.tapia.com.okurahotel.ui.room_manager.air_con;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Andy on 1/30/2018.
 */

public class AirConContract {
    public interface View extends BaseView{
        void setAirConSwitch(boolean state, boolean immediate);
    }
    public interface Presenter extends ScopedPresenter{
        void sendCommand(boolean state);
    }
}
