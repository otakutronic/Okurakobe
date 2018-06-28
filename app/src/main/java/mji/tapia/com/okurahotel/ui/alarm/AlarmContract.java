package mji.tapia.com.okurahotel.ui.alarm;

import java.util.ArrayList;

import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.service.alarm.AlarmCallManager;

/**
 * Created by anshumanrohella on 2018/01/24.
 */

public interface AlarmContract {

    interface View extends BaseView{
        void setAlarmValue(int hours, int minutes);
        void setAlarmSwitch(boolean state, boolean immediate);
    }

    interface Presenter extends ScopedPresenter{
        void setAlarm(boolean state, int hours, int minutes);
        void setAlarm(boolean state, int alarmTriggerTime);
        AlarmCallManager.AlarmState getAlarm(int id);
        ArrayList<AlarmCallManager.AlarmState> getAlarms();
        void cancelAlarm();
    }
}
