package mji.tapia.com.okurahotel.ui.room_manager.light;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import java.util.HashMap;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.widget.SwitchButton;

/**
 * Created by Andy on 6/21/2018.
 */

public class LightActivity extends BaseActivity implements LightContract.View{

    private static final String TAG = "LightActivity";

    @Inject
    LightPresenter presenter;

    @BindView(R.id.room_light_button)
    View room_btn;

    @BindView(R.id.spot_light_button)
    View spot_btn;

    @BindView(R.id.foot_light_button)
    View foot_btn;

    @BindView(R.id.exit_button)
    View back_bt;

    @BindView(R.id.switch_button)
    SwitchButton switchButton;

    private Unbinder unbinder;

    HashMap<CommandType, View> hashMapCommandButton = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        unbinder = ButterKnife.bind(this);

        hashMapCommandButton.put(CommandType.ROOM, room_btn);
        hashMapCommandButton.put(CommandType.SPOT, spot_btn);
        hashMapCommandButton.put(CommandType.FOOT, foot_btn);

        back_bt.setOnClickListener(v -> presenter.back());

        switchButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                final Boolean switchState = !switchButton.isChecked();
                onLightSwitchSelect(switchState);
            }
            return true;
        });

        for (CommandType cmd: hashMapCommandButton.keySet()) {
            hashMapCommandButton.get(cmd).setOnClickListener(v -> onLightButtonSelect(cmd));
        }
    }

    public void onLightButtonSelect(CommandType cmd) {
        final View button = hashMapCommandButton.get(cmd);
        final Boolean isSelected = !button.isSelected();
        presenter.sendCommand(cmd, isSelected);
        setLightButton(cmd, isSelected);
    }

    public void onLightSwitchSelect(boolean state) {
        presenter.sendCommand(CommandType.ALL, state);
        setLightSwitch(state, false);
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
    public void setLightSwitch(boolean state, boolean immediate) {
        if(immediate) {
            runOnUiThread(() -> switchButton.setCheckedImmediately(state));
        } else {
            runOnUiThread(() -> switchButton.setChecked(state));
        }
    }

    @Override
    public void setLightButton(CommandType commandType, boolean state) {
        View button = hashMapCommandButton.get(commandType);
        runOnUiThread(() -> button.setSelected(state));
    }
}