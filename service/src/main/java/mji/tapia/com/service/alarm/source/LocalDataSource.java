package mji.tapia.com.service.alarm.source;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import java.util.ArrayList;
import mji.tapia.com.service.alarm.AlarmCallManager;
import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 4/9/2018.
 *
 */

public class LocalDataSource {

    static final private String ALARMS_SHARED_PREFERENCE = "alarm_shared_preference";

    private ArrayList<AlarmCallManager.AlarmState> alarmList = new ArrayList<AlarmCallManager.AlarmState>();

    private SharedPreferences sharedPreferences;

    public LocalDataSource(PreferenceUtils sharedPreferenceManager) {
        sharedPreferences = sharedPreferenceManager.getSharedPreference(ALARMS_SHARED_PREFERENCE);
    }

    public AlarmCallManager.AlarmState getAlarm(int id) {
        final AlarmCallManager.AlarmState alarm = alarmList.get(id);
        return alarm;
    }

    public void addAlarm(AlarmCallManager.AlarmState alarm) {
        if (null == alarmList) {
            alarmList = new ArrayList<AlarmCallManager.AlarmState>();
        }

        deleteAlarms();

        final int id = alarmList.size();
        alarm.id = id;
        alarmList.add(alarm);

        Gson gson = new Gson();
        String alarmJsonString = gson.toJson(alarm);

        sharedPreferences.edit().putString(String.valueOf(id), alarmJsonString).apply();
    }

    public void deleteAlarm(AlarmCallManager.AlarmState alarm) {
        final int index = alarm.id;
        alarmList.remove(index);
        final ArrayList<AlarmCallManager.AlarmState> tempAlarms = alarmList;

        deleteAlarms();

        for(int i = 0; i < tempAlarms.size(); i++) {
            final AlarmCallManager.AlarmState tempAlarm = tempAlarms.get(i);
            addAlarm(tempAlarm);
        }
    }

    public ArrayList<AlarmCallManager.AlarmState> getAlarms() {
        alarmList = new ArrayList<AlarmCallManager.AlarmState>();

        while(true) {
            String alarmID = String.valueOf(alarmList.size());
            String value = sharedPreferences.getString(alarmID, null);
            if(value == null) {
                break;
            }
            Gson gson = new Gson();
            AlarmCallManager.AlarmState alarm = gson.fromJson(value, AlarmCallManager.AlarmState.class);
            alarmList.add(alarm);
        }

        return alarmList;
    }

    public void deleteAlarms() {
        int index = 0;

        while(true) {
            String value = sharedPreferences.getString(String.valueOf(index), null);
            if(value == null) {
                break;
            }
            sharedPreferences.edit().remove(String.valueOf(index)).apply();
            sharedPreferences.edit().remove(String.valueOf(index)).commit();
            index++;
        }

        alarmList = new ArrayList<AlarmCallManager.AlarmState>();
    }
}
