package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import java.io.Closeable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import  mji.tapia.com.service.iot.bluetooth.source.LocalDataSource;
import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 6/01/2018.
 */

public class BluetoothDiscoveryServiceImpl implements BluetoothDiscoveryService, Closeable {

    static public final String IOT_DEVICE_ID = "Room_";

    static public final String IOT_DEVICE_PIN = "180025";

    static public final String IOT_DEVICE_ADDRESS = "0";

    private BluetoothAdapter mBtAdapter;

    private BluetoothDevice boundingDevice;

    private boolean bluetoothDiscoveryScheduled;

    private final BroadcastReceiverDelegator broadcastReceiverDelegator;

    private final Context context;

    private BehaviorSubject<Boolean> pairRequest = BehaviorSubject.create();

    private BehaviorSubject<Boolean> bluetoothDiscovery = BehaviorSubject.create();

    private BehaviorSubject<Boolean> devicePaired = BehaviorSubject.create();

    private BehaviorSubject<BluetoothDevice> bluetoothDevice = BehaviorSubject.create();

    private BehaviorSubject<Boolean> devicePairingEnded = BehaviorSubject.create();

    private LocalDataSource localDataSource;

    private boolean isNewID;

    public BluetoothDiscoveryServiceImpl(Context context, PreferenceUtils sharedPreferenceManager, BluetoothAdapter btAdapter){
        this.context = context;
        this.mBtAdapter = btAdapter;
        this.broadcastReceiverDelegator = new BroadcastReceiverDelegator(context, this);
        localDataSource = new LocalDataSource(sharedPreferenceManager);
    }

    @Override
    public void turnOnBluetooth() {
        broadcastReceiverDelegator.onBluetoothTurningOn();
        mBtAdapter.enable();
    }

    @Override
    public boolean isBluetoothEnabled() {
        return mBtAdapter.isEnabled();
    }

    @Override
    public void unpairAllDevices() {
        Set<BluetoothDevice> pairedDevice = mBtAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                unpairDevice(device);
            }
        }
    }

    @Override
    public boolean isDevicePaired(IOTDevice iotDevice) {
        boolean isPaired = false;
        Set<BluetoothDevice> pairedDevice = mBtAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            for (BluetoothDevice device : pairedDevice) {
                final String deviceID = device.getName();
                if(deviceID != null && deviceID.equals(iotDevice.id)) {
                    isPaired = true;
                }
            }
        }
        return isPaired;
    }

    @Override
    public boolean pairDevice(BluetoothDevice device) {
        // Stops the discovery and then creates the pairing.
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        boolean outcome = device.createBond();

        // If the outcome is true, we are bounding with this device.
        if (outcome == true) {
            this.boundingDevice = device;
        }
        return outcome;
    }

    public void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }

    @Override
    public boolean isAlreadyPaired(BluetoothDevice device) {
        return mBtAdapter.getBondedDevices().contains(device);
    }

    @Override
    public Observable<BluetoothDevice> bluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public Observable<Boolean> bluetoothPairRequest() {
        return pairRequest;
    }

    @Override
    public Observable<Boolean> bluetoothDiscovery() {
        return bluetoothDiscovery;
    }

    @Override
    public Observable<Boolean> bluetoothConnect() {
        return devicePaired;
    }

    @Override
    public Observable<Boolean> devicePairingComplete() {
        return devicePairingEnded;
    }

    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }

    /**
     * Starts the discovery of new Bluetooth devices nearby.
     */
    @Override
    public void startDiscovery(boolean isNewID) {
        this.isNewID = isNewID;

        broadcastReceiverDelegator.onDeviceDiscoveryStarted();

        final BluetoothDiscoveryService.IOTDevice iotDevice = getIOTDevice();

        if(!isNewID && iotDevice.hub != null) {
            final boolean isDevicePaired = isDevicePaired(iotDevice);
            if(isDevicePaired) {
                onBluetoothDeviceConnected();
                return;
            }

            unpairAllDevices();

            final boolean pairOutcome = pairDevice(iotDevice.hub);
            if(pairOutcome) {
                onBluetoothDeviceConnected();
            };
        }

        unpairAllDevices();

        // If another discovery is in progress, cancels it before starting the new one.
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Tries to start the discovery. If the discovery returns false, this means that the
        // bluetooth has not started yet.
        if (!mBtAdapter.startDiscovery()) {
            Toast.makeText(context, "Error while starting device discovery!", Toast.LENGTH_SHORT)
                    .show();

            // Ends the discovery.
            broadcastReceiverDelegator.onDeviceDiscoveryEnd();
        }
    }

    @Override
    public void stopDiscovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
    }

    @Override
    public List<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDeviceSet = mBtAdapter.getBondedDevices();
        return new ArrayList<>(pairedDeviceSet);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

        }
    };

    @Override
    public void close() {
        this.broadcastReceiverDelegator.close();
    }

    /**
     * Checks if a deviceDiscovery is currently running.
     *
     * @return true if a deviceDiscovery is currently running, false otherwise.
     */
    @Override
    public boolean isDiscovering() {
        return mBtAdapter.isDiscovering();
    }

    /**
     * Cancels a device discovery.
     */
    @Override
    public void cancelDiscovery() {
        if(mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
            broadcastReceiverDelegator.onDeviceDiscoveryEnd();
        }
    }

    @Override
    public void onBluetoothDeviceConnected() {
        devicePaired.onNext(true);
        devicePairingEnded.onNext(true);
    }

    @Override
    public void turnOnBluetoothAndScheduleDiscovery() {
        this.bluetoothDiscoveryScheduled = true;
        turnOnBluetooth();
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device) {
        final String deviceID = device.getName();
        final IOTDevice iotDevice = localDataSource.getDevice();

        if(deviceID != null && deviceID.equals(iotDevice.id)) {
            final boolean pairOutcome = pairDevice(device);
            if(pairOutcome) {
                iotDevice.address = device.getAddress();
                iotDevice.hub = device;
                localDataSource.setDevice(iotDevice);
            }
        }
    }

    @Override
    public void onDeviceDiscoveryStarted() {
        bluetoothDiscovery.onNext(true);
    }

    @Override
    public void setBluetoothController(BluetoothDiscoveryServiceImpl bluetooth) {

    }

    @Override
    public void reset() {
        pairRequest.onNext(false);
        bluetoothDiscovery.onNext(false);
        devicePaired.onNext(false);
        devicePairingEnded.onNext(false);
    }

    @Override
    public void onDeviceDiscoveryEnd() {
        bluetoothDiscovery.onNext(false);

        final IOTDevice iotDevice = getIOTDevice();
        final boolean isPaired = isDevicePaired(iotDevice);
        if(isPaired) {
            onBluetoothDeviceConnected();
        } else {
            if(iotDevice.hub != null) {
                pairDevice(iotDevice.hub);
            }
        }
    }

    /**
     * Called when the Bluetooth pair request is received
     */
    public void onBluetoothPairingRequest() {
        final IOTDevice iotDevice = getIOTDevice();
        final boolean isPaired = isDevicePaired(iotDevice);
        if(isPaired) {
            onBluetoothDeviceConnected();
        }
        pairRequest.onNext(true);
    }

    /**
     * Called when the Bluetooth status changed.
     */
    public void onBluetoothStatusChanged() {
        // Does anything only if a device discovery has been scheduled.
        if (bluetoothDiscoveryScheduled) {

            int bluetoothState = mBtAdapter.getState();

            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    // Bluetooth is ON.
                    startDiscovery(isNewID);
                    // Resets the flag since this discovery has been performed.
                    bluetoothDiscoveryScheduled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    // Bluetooth is OFF.
                    Toast.makeText(context, "Error while turning Bluetooth on.", Toast.LENGTH_SHORT);
                    // Resets the flag since this discovery has been performed.
                    bluetoothDiscoveryScheduled = false;
                    break;
                default:
                    // Bluetooth is turning ON or OFF. Ignore.
                    break;
            }
        }
    }

    @Override
    public void onBluetoothTurningOn() {

    }

    @Override
    public void onDeviceBondStateChanged() {
        Log.e("TAG", "onDeviceBondStateChanged");

        final IOTDevice iotDevice = getIOTDevice();
        final boolean isPaired = isDevicePaired(iotDevice);

        if(isPaired) {
            onBluetoothDeviceConnected();
        }
    }

    public int getPairingDeviceStatus() {
        if (this.boundingDevice == null) {
            throw new IllegalStateException("No device currently bounding");
        }
        int bondState = this.boundingDevice.getBondState();
        // If the new state is not BOND_BONDING, the pairing is finished, cleans up the state.
        if (bondState != BluetoothDevice.BOND_BONDING) {
            this.boundingDevice = null;
        }
        return bondState;
    }

    public String getPairingDeviceName() {
        return getDeviceName(this.boundingDevice);
    }

    public static String getDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null) {
            deviceName = device.getAddress();
        }
        return deviceName;
    }

    public boolean isPairingInProgress() {
        return this.boundingDevice != null;
    }

    public BluetoothDevice getBoundingDevice() {
        return boundingDevice;
    }

    @Override
    public IOTDevice getIOTDevice() {
        return localDataSource.getDevice();
    }

    @Override
    public void setIOTDevice(IOTDevice iOTDevice) {
        localDataSource.setDevice(iOTDevice);
    }

    @Override
    public IOTDevice setUpIOTDevice(String roomNumber) {
        IOTDevice iOTDevice = getIOTDevice();
        iOTDevice.id = IOT_DEVICE_ID + roomNumber;
        iOTDevice.pin = IOT_DEVICE_PIN + roomNumber;
        setIOTDevice(iOTDevice);
        return iOTDevice;
    }

    /**
     * programmatically sets the pin for pairing
     */
    @Override
    public void setBluetoothPairingPin(BluetoothDevice device) {
        try {
            final String stringPin = getIOTDevice().pin;
            byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, stringPin);
            device.setPin(pin);
            device.setPairingConfirmation(false); // trying to force manual pin entry to close but doesn't work :(
            onBluetoothPairingRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
