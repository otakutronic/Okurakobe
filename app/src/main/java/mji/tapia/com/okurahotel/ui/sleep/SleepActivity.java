package mji.tapia.com.okurahotel.ui.sleep;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

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

public class SleepActivity extends BaseActivity implements SleepContract.View{

    @Inject
    SleepPresenter presenter;

    @BindView(R.id.clock_text)
    TextView clock_tv;

    @BindView(R.id.clock_screen)
    View clockScreen;

    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        unbinder = ButterKnife.bind(this);

        clockScreen.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                presenter.back();
                return true;
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
    public void updateClock(Date date) {
        //String timeString = DateFormat.format("h:mm",date).toString();
        SimpleDateFormat simpDate;
        simpDate = new SimpleDateFormat("HH:mm");
        final String timeString = simpDate.format(date).toString();
        clock_tv.setText(timeString);
    }
}

