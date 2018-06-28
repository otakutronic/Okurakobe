package mji.tapia.com.service.iot;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.air_con.IoTAirConManager;
import mji.tapia.com.service.iot.alarm.IoTAlarmManager;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;
import mji.tapia.com.service.iot.bluetooth.BluetoothSerialService;
import mji.tapia.com.service.iot.curtain.IoTCurtainManager;
import mji.tapia.com.service.iot.light.IoTLightManager;
import mji.tapia.com.service.iot.message.IoTMessageManager;
import mji.tapia.com.service.iot.room_state.IoTRoomStateManager;

/**
 * Created by Sami on 2/2/2018.
 *
 */

public interface IoTService {


    enum IoTConnectionState{
        CONNECTED,
        CONNECTING,
        DISCONNECTED
    }


    interface CommandEditor {
        Completable write(String commandName, Object... params);
    }

    interface IoTCommandChangeListener {

        void onCommandChange(IoTCommand command);
    }

    void start();

    void restart();

    void setAutoReconnection(boolean isEnable);

    boolean isRunning();

    Observable<IoTConnectionState> getConnectionStateObservable();

    Completable pingIoTBox();

    Completable updateData();

    IoTAlarmManager getAlarmManager();

    IoTAirConManager getAirConManager();

    IoTCurtainManager getCurtainManager();

    IoTLightManager getLightManager();

    IoTRoomStateManager getRoomManager();

    IoTMessageManager getMessageManager();

    BluetoothDiscoveryService getBluetoothDiscoveryManager();

    BluetoothSerialService getBluetoothSerialManager();

    void pairBluetoothDevice(final String roomNumber);

    void connectSerialDevice(final String hubAddress);

}
