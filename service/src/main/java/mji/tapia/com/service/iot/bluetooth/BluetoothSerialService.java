package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothDevice;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by Sami on 9/25/2017.
 */

public interface BluetoothSerialService {


    enum ConnectionStatus{
        CONNECTED,
        LISTENING,
        CONNECTING,
        DISCONNECTED,
    }

    Completable connect(BluetoothDevice bluetoothDevice);

    Observable<ConnectionStatus> connectionStatusChange();

    Completable write(byte[] out);

    Observable<byte[]> read();

    void stop();

}
