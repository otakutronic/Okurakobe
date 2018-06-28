package mji.tapia.com.okurahotel.ui.room_manager.curtain;

import android.os.Bundle;
import android.support.annotation.Nullable;
import javax.inject.Inject;
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

    @OnClick(R.id.curtains_back_button)
    void onBackButtonClick(){
        presenter.back();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curtain);
        unbinder = ButterKnife.bind(this);
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

    @OnClick(R.id.curtains_up_button)
    void onCurtainUpButton(){
        presenter.raiseCurtains();
    }

    @OnClick(R.id.curtains_down_button)
    void onCurtainDownButton(){
        presenter.closeCurtains();
    }

    @OnClick(R.id.curtains_stop_button)
    void onCurtainStopButton(){
        presenter.stopCurtains();
    }

}

