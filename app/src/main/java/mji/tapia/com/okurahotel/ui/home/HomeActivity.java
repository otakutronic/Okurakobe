package mji.tapia.com.okurahotel.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;

/**
 * Created by Sami on 9/21/2017.
 */

public class HomeActivity extends BaseActivity implements HomeContract.View{

    @Inject
    HomePresenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.clean_room)
    TextView cleanRoom_tv;

    @BindView(R.id.not_disturb)
    TextView notDisturb_tv;

    @BindView(R.id.do_laundry)
    TextView doLaundry_tv;

    @OnClick(R.id.alarm_button)
    void onAlarmClick(){
        presenter.selectAlarm();
    }

    @OnClick(R.id.room_manager_button)
    void onRoomManagerClick(){
        presenter.selectRoomManager();
    };

    @OnClick(R.id.sleep_mode_button)
    void onSleepModeClick(){
        presenter.selectSleepMode();
    };

    @OnClick(R.id.language_setting_button)
    void onLanguageSettingClick(){
        presenter.selectLanguageSetting();
    };

    @BindView(R.id.home_screen)
    View homeScreen;

    @OnClick(R.id.staff_mode)
    void onStaffModeClick() {
        presenter.selectStaffMode();
    }

    @OnClick(R.id.alarm_state)
    void onAlarmStateClick() {
        presenter.selectAlarmState();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        unbinder = ButterKnife.bind(this);

        cleanRoom_tv.setOnClickListener(v -> presenter.toggleCleanRoom());
        doLaundry_tv.setOnClickListener(v -> presenter.toggleDoLaundry());
        notDisturb_tv.setOnClickListener(v -> presenter.toggleDoNotDisturb());

        homeScreen.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN){
                //Toast.makeText(this, "Outch!!", Toast.LENGTH_SHORT).show();
                presenter.onUserActivity();
            }
            return false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void setCleanRoomTextEnable(boolean isEnabled) {
        runOnUiThread(() -> {
            if(isEnabled) {
                cleanRoom_tv.setTextColor(getResources().getColor(R.color.colorText));
                notDisturb_tv.setTextColor(getResources().getColor(R.color.colorDisabledText));
            }
            else {
                cleanRoom_tv.setTextColor(getResources().getColor(R.color.colorDisabledText));
            }
        });

    }

    @Override
    public void setDoNotDisturbTextEnable(boolean isEnabled) {
        runOnUiThread(() -> {
            if(isEnabled) {
                notDisturb_tv.setTextColor(getResources().getColor(R.color.colorText));
                cleanRoom_tv.setTextColor(getResources().getColor(R.color.colorDisabledText));
            }
            else {
                notDisturb_tv.setTextColor(getResources().getColor(R.color.colorDisabledText));
            }
        });
    }

    @Override
    public void setDoLaundryTextEnable(boolean isEnabled) {
        runOnUiThread(() -> {
            if(isEnabled) {
                doLaundry_tv.setTextColor(getResources().getColor(R.color.colorText));
            }
            else {
                doLaundry_tv.setTextColor(getResources().getColor(R.color.colorDisabledText));
            }
        });

    }

    @Override
    public void setAlarmInfo(Boolean isEnable, int hours, int minutes) {
        TextView alarmLabel = (TextView) findViewById(R.id.alarm_state);
        if(isEnable) {
            String alarmTime = String.format(Locale.getDefault(),"%d:%02d",hours,minutes);
            alarmLabel.setText(String.format(getString(R.string.home_alarm_on_label), alarmTime));
        } else {
            alarmLabel.setText(R.string.home_alarm_off_label);
        }
    }
}

