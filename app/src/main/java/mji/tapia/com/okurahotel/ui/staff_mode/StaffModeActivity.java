package mji.tapia.com.okurahotel.ui.staff_mode;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.ui.splash.SplashActivity;

/**
 * Created by andy on 9/21/2017.
 */

public class StaffModeActivity extends BaseActivity implements StaffModeContract.View{

    @Inject
    StaffModePresenter presenter;

    @BindView(R.id.back_bt)
    View back_btn;

    @BindView(R.id.bluetooth_bt)
    View bluetooth_btn;

    @BindView(R.id.volume_bar)
    SeekBar volume_bar;

    @BindView(R.id.error_log_bt)
    View error_log_btn;

    @BindView(R.id.room_id)
    EditText room_id_txt;

    private Unbinder unbinder;

    private ProgressDialog bondingProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_menu);
        unbinder = ButterKnife.bind(this);
        bondingProgressDialog = null;

        back_btn.setOnClickListener(v -> onBackButtonSelected());
        bluetooth_btn.setOnClickListener(v -> onBluetoothButtonSelect());
        error_log_btn.setOnClickListener(v -> presenter.onErrorLogSelect());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void onBackButtonSelected() {
        saveSettings();
        presenter.back();
    }

    private void saveSettings() {
        final String roomID = String.valueOf(room_id_txt.getText());
        presenter.onRoomNumberUpdate(roomID);
        final int volume = volume_bar.getProgress();
        presenter.onVolumeUpdate(volume);
    }

    private void onBluetoothButtonSelect() {
        saveSettings();
        setPermissions();

        final String roomID = String.valueOf(room_id_txt.getText());
        presenter.onBluetoothSelect(roomID);

        back_btn.setEnabled(false);
    }

    private void setPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
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
    public void setRoomID(String id) {
        room_id_txt.setText(id);
    }

    @Override
    public void setVolume(int volume) {
        volume_bar.setProgress(volume);
    }

    @Override
    public void setBluetoothDiscovery(Boolean isDiscovering) {
        if(isDiscovering) {
            if(bondingProgressDialog == null) {
                final String deviceName = presenter.getIOTDevice().id;
                bondingProgressDialog = ProgressDialog.show(this, "", "Pairing with device " + deviceName,
                        true, false);
            }
        } else {
            if(bondingProgressDialog != null) {
                bondingProgressDialog.dismiss();
                bondingProgressDialog = null;
            }
        }
    }

    @Override
    public void setBluetoothConnected() {
        restartProcess(true);
    }

    @Override
    public void setBluetoothPairRequest() {
        back_btn.setEnabled(true);
    }

    private boolean restartProcess(boolean restartProcess) {
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}

