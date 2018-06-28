package mji.tapia.com.okurahotel.ui.room_manager;

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

public class RoomManagerActivity extends BaseActivity implements RoomManagerContract.View{

    @Inject
    RoomManagerPresenter presenter;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_manager);
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

    @OnClick(R.id.curtains_button)
    void onCurtainsButtonClick(){
        presenter.selectCurtain();
    }

    @OnClick(R.id.light_button)
    void onLightButtonClick(){
        presenter.selectLight();
    }

    @OnClick(R.id.exit_button)
    void onBackButtonClick(){
        presenter.back();
    }

    @OnClick(R.id.ac_button)
    void onAirConButtonClick(){
        presenter.selectAirCon();
    }
}

