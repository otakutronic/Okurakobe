package mji.tapia.com.okurahotel.ui.staff_mode.log;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.widget.ExpandableLayoutListView;

/**
 * Created by Sami on 2/1/2018.
 */

public class LogActivity extends BaseActivity implements LogContract.View {

    @Inject
    LogPresenter presenter;

    @BindView(R.id.expandableLayout)
    ExpandableLayoutListView expandableListView;

    @BindView(R.id.runtime_cb)
    CheckBox runtime_cb;

    @BindView(R.id.warning_cb)
    CheckBox warning_cb;

    @BindView(R.id.back_bt)
    View back_bt;

    private LogAdapter logAdapter;



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
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        logAdapter = new LogAdapter(getLayoutInflater(), new ArrayList<>());
        expandableListView.setAdapter(logAdapter);

        back_bt.setOnClickListener(view -> presenter.back());

        runtime_cb.setOnCheckedChangeListener((compoundButton, b) -> {
            if(warning_cb.isChecked() == b) {
                presenter.onAllSelect();
            } else if(b) {
                presenter.onRuntimeSelect();
            } else {
                presenter.onWarningSelect();
            }
        });

        warning_cb.setOnCheckedChangeListener((compoundButton, b) -> {
            if(runtime_cb.isChecked() == b) {
                presenter.onAllSelect();
            } else if(b) {
                presenter.onWarningSelect();
            } else {
                presenter.onRuntimeSelect();
            }
        });
    }

    @Override
    public void setErrorLogList(List<ErrorLog> errorLogList) {
        logAdapter.refreshList(errorLogList);
    }
}
