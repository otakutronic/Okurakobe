package mji.tapia.com.okurahotel.ui.alarm;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import java.util.ArrayList;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.widget.PickerView;
import mji.tapia.com.okurahotel.widget.SwitchButton;
import mji.tapia.com.service.alarm.AlarmCallManager;

/**
 * Created by andy on 2018/04/24.
 */

public class AlarmActivity extends BaseActivity implements AlarmContract.View {

    @Inject
    AlarmPresenter alarmPresenter;

    @BindView(R.id.ok_alarm)
    TextView ok_alarm;

    @BindView(R.id.picker_view_hour)
    PickerView pickerViewHour;

    @BindView(R.id.picker_view_minute)
    PickerView pickerViewMinute;

    @BindView(R.id.switch_button_alarm)
    SwitchButton switchButtonAlarm;

    private boolean isResume = false;

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return alarmPresenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        setPickerViewValues(pickerViewHour,pickerViewMinute);

        setAlarmViewValues();

        // back pressed
        ok_alarm.setOnClickListener((v) -> {
            final int hours = pickerViewHour.getItemSelected();
            final int minutes = pickerViewMinute.getItemSelected();
            final Boolean alarmState = switchButtonAlarm.isChecked();
            alarmPresenter.setAlarm(alarmState, hours, minutes);
            alarmPresenter.back();
        });

        // on/off toggle
        switchButtonAlarm.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                final Boolean switchState = switchButtonAlarm.isChecked();
                setAlarmSwitch(!switchState, false);
            }
            return true;
        });

    }

    private void setPickerViewValues(PickerView pickerViewHour,PickerView pickerViewMinute){
        String[] hourArray = new String[24];

        for(int i =0;i<24;i++){
            hourArray[i]= String.format("%02d", i);
        }
        pickerViewHour.setItemArray(hourArray);

        String[] minuteArray = new String[60];
        for(int i = 0 ;i<60;i++){
            minuteArray[i] = String.format("%02d", i);
        }
        pickerViewMinute.setItemArray(minuteArray);
    }

    private void setAlarmViewValues() {
        final ArrayList<AlarmCallManager.AlarmState> alarms = alarmPresenter.getAlarms();

        if(alarms.size() > 0) {
            AlarmCallManager.AlarmState alarm = alarms.get(0);
            setAlarmValue(alarm.hours, alarm.minutes);
            setAlarmSwitch(alarm.enabled, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void setAlarmValue(int hours, int minutes) {
        pickerViewHour.setSelectedItem(hours, isResume);
        pickerViewMinute.setSelectedItem(minutes, isResume);
    }

    @Override
    public void setAlarmSwitch(boolean state, boolean immediate) {
        if(immediate) {
            switchButtonAlarm.setCheckedImmediately(state);
        } else {
            switchButtonAlarm.setChecked(state);
        }
    }
}
