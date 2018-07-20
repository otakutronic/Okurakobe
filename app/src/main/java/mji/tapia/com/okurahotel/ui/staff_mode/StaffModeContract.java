package mji.tapia.com.okurahotel.ui.staff_mode;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;

/**
 * Created by Andy on 5/22/2018
 */

public interface StaffModeContract {
    interface View extends BaseView {
        void setRoomID(String number);
        void setBluetoothPairRequest();
        void setBluetoothDiscovery(Boolean isDiscovering);
        void setBluetoothConnected();
        void setVolume(int volume);
    }

    interface Presenter extends ScopedPresenter {
        void onRoomNumberUpdate(String id);
        void onBluetoothSelect(String roomNumber);
        void onVolumeUpdate(int volume);
        void onErrorLogSelect();
        void setup();
        BluetoothDiscoveryService.IOTDevice getIOTDevice();
        boolean isDeviceConnected(String roomNumber);
    }
}
