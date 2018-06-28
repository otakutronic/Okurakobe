package mji.tapia.com.okurahotel.ui.staff_mode;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import java.util.List;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;

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
        final String roomID = String.valueOf(room_id_txt.getText());
        presenter.onRoomNumberUpdate(roomID);
        final int volume = volume_bar.getProgress();
        presenter.onVolumeUpdate(volume);
        presenter.back();
    }

    private void onBluetoothButtonSelect() {
        setPermissions();

        final String roomID = String.valueOf(room_id_txt.getText());
        final boolean isConnected = presenter.isDeviceConnected(roomID);
        final BluetoothDiscoveryService.IOTDevice iotDevice = presenter.getIOTDevice();

        if(isConnected){
            Toast.makeText(this, "Already connected with device: " + iotDevice.id.toString(), Toast.LENGTH_LONG).show();
        } else {
            presenter.onBluetoothSelect(roomID);
        }
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
    public void setBluetoothPairRequest() {

    }

    @Override
    public void setBluetoothDiscovery(Boolean isDiscovering) {
        final String deviceName = presenter.getIOTDevice().id;
        if(isDiscovering) {
           if(bondingProgressDialog == null) {
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
        final BluetoothDiscoveryService.IOTDevice iotDevice = presenter.getIOTDevice();
        Toast.makeText(this, "Connected to: " + iotDevice.id.toString(), Toast.LENGTH_LONG).show();
    }
}

