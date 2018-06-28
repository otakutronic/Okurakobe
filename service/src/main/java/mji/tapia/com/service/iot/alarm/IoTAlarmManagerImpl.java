package mji.tapia.com.service.iot.alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Sami on 2/1/2018.
 *
 */

public class IoTAlarmManagerImpl implements IoTAlarmManager {

    private static final String COMMAND_ALARM = "ALMT";

    private IoTService.CommandEditor commandEditor;

    private BehaviorSubject<AlarmState> alarmStateBehaviorSubject = BehaviorSubject.create();

    public IoTAlarmManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_ALARM, Boolean.class, String.class));
        return commandDefinitionList;
    }

    @Override
    public void onCommandUpdate(IoTCommand command) {
        if(command.name.equals(COMMAND_ALARM)) {
            AlarmState alarmState = new AlarmState();
            alarmState.enabled = (Boolean) command.params.get(0);
            String alarmTime = (String) command.params.get(1);
            try {
                Date timeDate;
                if(alarmTime.length() == 2 || alarmTime.length() == 1) {
                    timeDate = new SimpleDateFormat("mm", Locale.JAPAN).parse(alarmTime);
                } else if(alarmTime.length() == 3){
                    timeDate = new SimpleDateFormat("Hmm", Locale.JAPAN).parse(alarmTime);
                } else {
                    timeDate = new SimpleDateFormat("HHmm", Locale.JAPAN).parse(alarmTime);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(timeDate);
                alarmState.hours = calendar.get(Calendar.HOUR_OF_DAY);
                alarmState.minutes = calendar.get(Calendar.MINUTE);
                alarmStateBehaviorSubject.onNext(alarmState);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Observable<AlarmState> getAlarmStateObservable() {
        return alarmStateBehaviorSubject;
    }

    @Override
    public Completable setAlarm(Boolean isEnabled, int hours, int minutes) {
        return commandEditor.write(COMMAND_ALARM, isEnabled, parseTime(hours, minutes));
    }

    private String parseTime(int hours, int minutes) {
        return Integer.toString(Integer.parseInt(String.format(Locale.JAPAN,"%02d",hours) + String.format(Locale.JAPAN,"%02d",minutes)));
    }
}
