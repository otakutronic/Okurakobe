package mji.tapia.com.okurahotel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MotionEvent;
import android.view.View;
import java.util.List;
import javax.inject.Inject;
import mji.tapia.com.okurahotel.configuration.ViewIdGenerator;
import mji.tapia.com.okurahotel.dagger.activity.DaggerActivity;
import mji.tapia.com.service.alarm.AlarmCallManager;
import mji.tapia.com.service.alarm.AlarmNotificationService;

/**
 * Created by Sami on 7/19/2017.
 */

public abstract class BaseActivity extends DaggerActivity implements BaseView {
    private static final String KEY_VIEW_ID = "view_id";

    @Inject
    protected FragmentManager fragmentManager;

    @Inject
    ViewIdGenerator viewIdGenerator;

    @Inject
    AlarmCallManager alarmManager;

    private String viewId;
    private boolean isViewRecreated;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView(savedInstanceState);
        getPresenter().start();
        checkForAlarm();
    }

    private void initializeView(@Nullable final Bundle savedInstanceState) {
        isViewRecreated = (savedInstanceState != null);
        viewId = (savedInstanceState == null) ? viewIdGenerator.newId()
                : savedInstanceState.getString(KEY_VIEW_ID);
    }

    @Override
    public String getViewId() {
        return viewId;
    }

    @Override
    public boolean isRecreated() {
        return isViewRecreated;
    }

    @Override
    protected void onResume() {
        super.onResume();
        configWindow();
        getPresenter().activate();
    }

    @Override
    protected void onPause() {
        getPresenter().deactivate();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            getPresenter().destroy();
            clearFragments();
        }
        super.onDestroy();
    }

    private void checkForAlarm() {
        getWindow().getDecorView().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if(AlarmNotificationService.isActive) {
                    alarmManager.silenceAlarm();
                }
            }
            return false;
        });
    }

    private void clearFragments() {
        final List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments == null)
            return;
        for (Fragment fragment: fragments) {
            if (fragment instanceof BaseFragment) {
                ((BaseFragment) fragment).onPreDestroy();
            }
        }
    }

    protected abstract ScopedPresenter getPresenter();


    public void configWindow(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
