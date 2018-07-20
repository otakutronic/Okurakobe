package mji.tapia.com.okurahotel.ui.staff_mode;

import javax.inject.Inject;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.audio.TapiaAudioManager;
import mji.tapia.com.service.audio.TapiaAudioManagerImpl;
import mji.tapia.com.service.iot.IoTService;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;
import mji.tapia.com.service.room_manager.RoomManager;

/**
 * Created by andy on 5/21/2018
 */

public class StaffModePresenter extends BasePresenter<StaffModeContract.View> implements StaffModeContract.Presenter {

    @Inject
    TapiaAudioManager tapiaAudioManager;

    @Inject
    RoomManager roomManager;

    @Inject
    IoTService iotManager;

    StaffModeContract.View view;

    private Disposable bluetoothPairDisposable;

    private Disposable bluetoothDiscoveryDisposable;

    private Disposable bluetoothConnectDisposable;

    public StaffModePresenter(StaffModeContract.View view) {
        super(view);
        this.view = view;
    }

    @Override
    public void start() {
        super.start();
        tapiaAudioManager.init();
    }

    @Override
    public void activate() {
        super.activate();
        setup();
    }

    @Override
    public void deactivate() {
        super.deactivate();

        if(bluetoothPairDisposable != null) {
            bluetoothPairDisposable.dispose();
        }

        if(bluetoothDiscoveryDisposable != null) {
            bluetoothDiscoveryDisposable.dispose();
        }

        if(bluetoothConnectDisposable != null) {
            bluetoothConnectDisposable.dispose();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {
        router.navigateToHomeScreen();
    }

    @Override
    public void setup() {
        int volume = tapiaAudioManager.getVolume();
        volume = TapiaAudioManagerImpl.volumeToPercent(volume);
        view.setVolume(volume);

        final String roomID = roomManager.getRoomID();
        view.setRoomID(roomID);

        bluetoothPairDisposable = iotManager.getBluetoothDiscoveryManager().bluetoothPairRequest().subscribe(isRequest -> {
            if(isRequest) {
                doIfViewNotNull(view -> view.setBluetoothPairRequest());
            }
        });

        bluetoothDiscoveryDisposable = iotManager.getBluetoothDiscoveryManager().bluetoothDiscovery().subscribe(isDiscovering -> {
            doIfViewNotNull(view -> view.setBluetoothDiscovery(isDiscovering));
        });

        bluetoothConnectDisposable = iotManager.getBluetoothDiscoveryManager().bluetoothConnect().subscribe(isConnected -> {
            if(isConnected) {
                doIfViewNotNull(view -> view.setBluetoothConnected());
            }
        });
    }

    @Override
    public BluetoothDiscoveryService.IOTDevice getIOTDevice() {
        return iotManager.getBluetoothDiscoveryManager().getIOTDevice();
    }

    @Override
    public void onRoomNumberUpdate(String id) {
        roomManager.setRoomID(id);
    }

    @Override
    public boolean isDeviceConnected(String roomNumber) {
        BluetoothDiscoveryService bluetoothDiscoveryService = iotManager.getBluetoothDiscoveryManager();
        bluetoothDiscoveryService.setUpIOTDevice(roomNumber);
        BluetoothDiscoveryService.IOTDevice iotDevice = bluetoothDiscoveryService.getIOTDevice();
        final boolean isDevicePaired = bluetoothDiscoveryService.isDevicePaired(iotDevice);
        return isDevicePaired;
    }

    @Override
    public void onBluetoothSelect(String roomNumber) {
        iotManager.discoverBluetoothDevice(roomNumber);
    }

    @Override
    public void onVolumeUpdate(int value) {
        value = TapiaAudioManagerImpl.percentToVolume(value);
        tapiaAudioManager.setVolume(value, true);
    }

    @Override
    public void onErrorLogSelect() {
        router.navigateToErrorLogScreen();
    }

}
