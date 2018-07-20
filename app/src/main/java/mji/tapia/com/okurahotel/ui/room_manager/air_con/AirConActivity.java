package mji.tapia.com.okurahotel.ui.room_manager.air_con;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.widget.SwitchButton;

/**
 * Created by Andy on 06/01/2018.
 */

public class AirConActivity extends BaseActivity implements AirConContract.View{

    @Inject
    AirConPresenter presenter;

    @BindView(R.id.switch_button)
    SwitchButton switchButtonAirCon;

    @BindView(R.id.exit_button)
    View back_bt;

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_con);
        ButterKnife.bind(this);

        // on/off toggle
        switchButtonAirCon.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                final Boolean switchState = !switchButtonAirCon.isChecked();
                presenter.sendCommand(switchState);
                setAirConSwitch(switchState, false);
            }
            return true;
        });

        back_bt.setOnClickListener(v -> presenter.back());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void setAirConSwitch(boolean state, boolean immediate) {
        if(immediate) {
            runOnUiThread(() -> switchButtonAirCon.setCheckedImmediately(state));
        } else {
            runOnUiThread(() -> switchButtonAirCon.setChecked(state));
        }
    }
}
