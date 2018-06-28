package mji.tapia.com.service.alarm;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import mji.tapia.com.service.alarm.source.LocalDataSource;
import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 2/1/2018.
 *
 */

public class AlarmCallManagerImpl implements AlarmCallManager {

    static public final int ALARM_REQUEST_CODE = 133;

    static public final int MAX_ALARMS = 10;

    private LocalDataSource localDataSource;

    private Context context;

    private PendingIntent pendingIntent;

    private BehaviorSubject<AlarmState> alarmStateBehaviorSubject = BehaviorSubject.create();

    public AlarmCallManagerImpl(Context applicationContext, PreferenceUtils sharedPreferenceManager) {
        this.context = applicationContext;

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE, alarmIntent, 0);

        localDataSource = new LocalDataSource(sharedPreferenceManager);

        setupAlarm();
    }

    @Override
    public void setupAlarm() {
        final ArrayList<AlarmCallManager.AlarmState> alarms = getAlarms();

        if(alarms.size() > 0) {
            AlarmCallManager.AlarmState alarm = alarms.get(0);
            alarmStateBehaviorSubject.onNext(alarm);
        }
    }

    @Override
    public Observable<AlarmState> getAlarmStateObservable() {
        return alarmStateBehaviorSubject;
    }

    @Override
    public void setAlarm(Boolean isEnabled, int hours, int minutes) {
        if(isEnabled) {
            saveAlarm(hours, minutes);
        } else {
            cancelAlarm();
        }

        AlarmState alarm = new AlarmState(0, isEnabled, hours, minutes);
        localDataSource.addAlarm(alarm);

        alarmStateBehaviorSubject.onNext(alarm);
    }

    @Override
    public void setAlarm(Boolean isEnabled, int alarmTriggerTime) {
        if(isEnabled) {
            saveAlarm(alarmTriggerTime);
        } else {
            cancelAlarm();
        }
    }

    // stop ringing alarm and reset for tomorrow
    @Override
    public void silenceAlarm() {
        cancelAlarm();
        resetAlarm();
    }

    @Override
    public void resetAlarm() {
        final ArrayList<AlarmCallManager.AlarmState> alarms = getAlarms();
        final int totalAlarms = alarms.size();

        if(totalAlarms > 0) {
            AlarmCallManager.AlarmState alarm = alarms.get(0);
            setAlarm(alarm.enabled, alarm.hours, alarm.minutes);
        }
    }

    @Override
    public AlarmState getAlarm(int id) {
        final AlarmState alarm = localDataSource.getAlarm(id);
        return alarm;
    }

    @Override
    public ArrayList<AlarmState> getAlarms() {
        final ArrayList<AlarmState> alarms = localDataSource.getAlarms();
        return alarms;
    }

    @Override
    public void deleteAlarm(AlarmState alarm) {
        localDataSource.deleteAlarm(alarm);
    }

    @Override
    public void deleteAlarms() {
        localDataSource.deleteAlarms();
    }

    private String parseTime(int hours, int minutes) {
        return Integer.toString(Integer.parseInt(String.format(Locale.JAPAN,"%02d",hours) + String.format(Locale.JAPAN,"%02d",minutes)));
    }

    // save/trigger alarm manager with entered time interval
    @Override
    public void saveAlarm(int alarmTriggerTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, alarmTriggerTime);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,  (alarmTriggerTime * 1000) /*cal.getTimeInMillis()*/, pendingIntent);
    }

    // save/trigger alarm manager with entered time interval
    @Override
    public void saveAlarm(int hours, int minutes) {
        Calendar alarmStartTime = Calendar.getInstance();
        alarmStartTime.setTimeInMillis(System.currentTimeMillis());
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, hours);
        alarmStartTime.set(Calendar.MINUTE, minutes);
        alarmStartTime.set(Calendar.SECOND, 0);

        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);
        }

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.setExact(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), pendingIntent);
    }

    // stop/cancel alarm manager
    @Override
    public void cancelAlarm() {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        context.stopService(new Intent(context, AlarmSoundService.class));

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);

        AlarmNotificationService.isActive = false;
    }
}
