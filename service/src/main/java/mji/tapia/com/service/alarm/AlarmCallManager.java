package mji.tapia.com.service.alarm;

import java.util.ArrayList;
import io.reactivex.Observable;

/**
 * Created by andy on 2/1/2018.
 */

public interface AlarmCallManager {
    String TAG = "AlarmCallManager";

    class AlarmState {
        public int id;
        public Boolean enabled;
        public Integer hours;
        public Integer minutes;

        public AlarmState(int id, Boolean enabled, int hours, int minutes){
            this.id = id;
            this.hours = hours;
            this.minutes = minutes;
            this.enabled = enabled;
        }
    }

    Observable<AlarmState> getAlarmStateObservable();

    void setAlarm(Boolean isEnabled, int hours, int minutes);
    void setAlarm(Boolean isEnabled, int alarmTriggerTime);
    AlarmState getAlarm(int id);
    void setupAlarm();
    ArrayList<AlarmState> getAlarms();
    void deleteAlarm(AlarmState alarm);
    void deleteAlarms();
    void saveAlarm(int hours, int minutes);
    void saveAlarm(int alarmTriggerTime);
    void cancelAlarm();
    void silenceAlarm();
    void resetAlarm();
}
