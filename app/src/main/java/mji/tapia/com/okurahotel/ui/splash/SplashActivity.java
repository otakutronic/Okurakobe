package mji.tapia.com.okurahotel.ui.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

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

public class SplashActivity extends BaseActivity implements SplashContract.View{

    @Inject
    SplashPresenter presenter;

    private Unbinder unbinder;

    @BindView(R.id.splash_progressBar)
    ProgressBar splashProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
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

    @OnClick(R.id.splash_screen)
    void start() {
        presenter.userStart();
    }

    @Override
    public void stopLoadingAnimation() {
        splashProgressBar.setVisibility(View.INVISIBLE);
    }
}

