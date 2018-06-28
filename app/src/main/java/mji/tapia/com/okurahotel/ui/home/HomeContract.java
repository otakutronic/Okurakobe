package mji.tapia.com.okurahotel.ui.home;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

public interface HomeContract {
    interface View extends BaseView {

        void setCleanRoomTextEnable(boolean isEnabled);

        void setDoNotDisturbTextEnable(boolean isEnabled);

        void setDoLaundryTextEnable(boolean isEnabled);

        void setAlarmInfo(Boolean isEnable, int hours, int minutes);
    }

    interface Presenter extends ScopedPresenter {

        void selectAlarm();

        void selectRoomManager();

        void selectLanguageSetting();

        void selectSleepMode();

        void selectStaffMode();

        void selectAlarmState();

        void toggleCleanRoom();

        void toggleDoNotDisturb();

        void toggleDoLaundry();

        void onUserActivity();
    }
}
