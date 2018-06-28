package mji.tapia.com.okurahotel.ui.alarm;

import java.util.ArrayList;
import javax.inject.Inject;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.alarm.AlarmCallManager;

/**
 * Created by andy on 2018/05/24.
 */

public class AlarmPresenter extends BasePresenter<AlarmContract.View> implements AlarmContract.Presenter {

    @Inject
    AlarmCallManager alarmManager;

    private Disposable alarmStateDisposable;

    public AlarmPresenter(AlarmContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
        if(alarmStateDisposable != null) {
            alarmStateDisposable.dispose();
        }
    }

    @Override
    public void back(){
        router.closeScreen();
    }

    @Override
    public void setAlarm(boolean state, int hours, int minutes) {
        alarmManager.setAlarm(state, hours, minutes);
    }

    @Override
    public void setAlarm(boolean state, int alarmTriggerTime) {
        alarmManager.setAlarm(state, alarmTriggerTime);
    }

    @Override
    public AlarmCallManager.AlarmState getAlarm(int id) {
        return alarmManager.getAlarm(id);
    }

    @Override
    public ArrayList<AlarmCallManager.AlarmState> getAlarms() {
        return alarmManager.getAlarms();
    }

    @Override
    public void cancelAlarm() {
        alarmManager.cancelAlarm();
    }

}
