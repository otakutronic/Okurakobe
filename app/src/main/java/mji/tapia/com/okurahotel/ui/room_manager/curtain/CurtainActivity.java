package mji.tapia.com.okurahotel.ui.room_manager.curtain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;

/**
 * Created by Sami on 9/21/2017.
 */

public class CurtainActivity extends BaseActivity implements CurtainContract.View{

    @Inject
    CurtainPresenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.exit_button_0)
    View exit_btn_0;

    @BindView(R.id.exit_button_1)
    View exit_btn_1;

    @BindView(R.id.exit_button_2)
    View exit_btn_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain);
        unbinder = ButterKnife.bind(this);

        setUpExitButton(exit_btn_0);
        setUpExitButton(exit_btn_1);
        setUpExitButton(exit_btn_2);
    }

    private void setUpExitButton(View button) {
        button.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                presenter.back();
                setCurtainButtons(false);
            }
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                setCurtainButtons(true);
            }
            return true;
        });
    }

    private void setCurtainButtons(Boolean isOn) {
        exit_btn_0.setPressed(isOn);
        exit_btn_1.setPressed(isOn);
        exit_btn_2.setPressed(isOn);
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

   @OnClick(R.id.curtain_open_button)
    void onCurtainOpenButton(){
        presenter.openCurtains();
    }

    @OnClick(R.id.curtain_close_button)
    void onCurtainCloseButton(){
        presenter.closeCurtains();
    }

    @OnClick(R.id.curtain_stop_button)
    void onCurtainStopButton(){
        presenter.stopCurtains();
    }
}

