package mji.tapia.com.service.iot.alarm;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.IoTManager;

/**
 * Created by Sami on 2/1/2018.
 */

public interface IoTAlarmManager extends IoTManager {
    String TAG = "IoTAlarmManager";

    class AlarmState {
        public Boolean enabled;
        public Integer hours;
        public Integer minutes;
    }

    Observable<AlarmState> getAlarmStateObservable();

    Completable setAlarm(Boolean isEnabled, int hours, int minutes);
}
