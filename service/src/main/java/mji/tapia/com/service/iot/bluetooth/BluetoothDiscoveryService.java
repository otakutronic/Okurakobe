package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import io.reactivex.Observable;
import mji.tapia.com.service.alarm.AlarmCallManager;

/**
 * Created by Andy on 9/26/2018.
 */

public interface BluetoothDiscoveryService {

    class IOTDevice {
        public String id;
        public String address;
        public String pin;
    }

    void startDiscovery();

    void stopDiscovery();

    List<BluetoothDevice> getPairedDevices();

    boolean isBluetoothEnabled();

    boolean isDiscovering();

    void cancelDiscovery();

    void turnOnBluetooth();

    boolean pairDevice(BluetoothDevice device);

    void unpairAllDevices();

    void unpairDevice(BluetoothDevice device);

    void turnOnBluetoothAndScheduleDiscovery();

    boolean isAlreadyPaired(BluetoothDevice device);

    Observable<BluetoothDevice> bluetoothDevice();

    Observable<Boolean> bluetoothPairRequest();

    Observable<Boolean> bluetoothDiscovery();

    Observable<Boolean> bluetoothConnect();

    IOTDevice getIOTDevice();

    public void setIOTDevice(IOTDevice iOTDevice);

    IOTDevice setUpIOTDevice(String roomNumber);

    void onBluetoothDeviceConnected();

    void onDeviceDiscovered(BluetoothDevice device);

    void onDeviceDiscoveryStarted();

    void setBluetoothController(BluetoothDiscoveryServiceImpl bluetooth);

    void onDeviceDiscoveryEnd();

    void onBluetoothStatusChanged();

    void onBluetoothTurningOn();

    void onDevicePairingEnded();

    boolean isDevicePaired(IOTDevice iotDevice);
}
