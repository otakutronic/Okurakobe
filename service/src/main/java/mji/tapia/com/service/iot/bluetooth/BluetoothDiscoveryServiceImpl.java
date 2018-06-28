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

    static public final String IOT_DEVICE_PIN = "160062";

    static public final String IOT_DEVICE_ADDRESS = "00:1B:DC:06:C1:D0";

    private BluetoothAdapter mBtAdapter;

    private PublishSubject<BluetoothDevice> bluetoothDevicePublishSubject = PublishSubject.create();

    private BluetoothDevice boundingDevice;

    private boolean bluetoothDiscoveryScheduled;

    private final BroadcastReceiverDelegator broadcastReceiverDelegator;

    private final Context context;

    private BehaviorSubject<Boolean> pairRequest = BehaviorSubject.create();

    private BehaviorSubject<Boolean> bluetoothDiscovery = BehaviorSubject.create();

    private BehaviorSubject<Boolean> devicePaired = BehaviorSubject.create();

    private BehaviorSubject<BluetoothDevice> bluetoothDevice = BehaviorSubject.create();

    private LocalDataSource localDataSource;

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

    /*public void setBluetoothPairingPin(BluetoothDevice device)
    {
        byte[] pinBytes = convertPinToBytes("0000");
        try {
            Log.d(TAG, "Try to set the PIN");
            Method m = device.getClass().getMethod("setPin", byte[].class);
            m.invoke(device, pinBytes);
            Log.d(TAG, "Success to add the PIN.");
            try {
                device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                Log.d(TAG, "Success to setPairingConfirmation.");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }*/

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

   /* public void pairDevice(BluetoothDevice device) {
        try {
            //if (D)
            Log.e("TAG", "Start Pairing...");

            //waitingForBonding = true;

            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);

            //if (D)
            Log.e("TAG", "Pairing finished.");
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }*/

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

    public static String deviceToString(BluetoothDevice device) {
        return "[Address: " + device.getAddress() + ", Name: " + device.getName() + "]";
    }

    /**
     * Starts the discovery of new Bluetooth devices nearby.
     */
    public void startDiscovery() {
        broadcastReceiverDelegator.onDeviceDiscoveryStarted();

        // If another discovery is in progress, cancels it before starting the new one.
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Tries to start the discovery. If the discovery returns false, this means that the
        // bluetooth has not started yet.
        Log.e("TAG", "Bluetooth starting discovery.");
        if (!mBtAdapter.startDiscovery()) {
            Toast.makeText(context, "Error while starting device discovery!", Toast.LENGTH_SHORT)
                    .show();
            Log.e("TAG", "StartDiscovery returned false. Maybe Bluetooth isn't on?");

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
    public void onDeviceDiscoveryEnd() {
        bluetoothDiscovery.onNext(false);
    }

    /**
     * Called when the Bluetooth status changed.
     */
    public void onBluetoothStatusChanged() {
        Log.e("TAG", "onBluetoothStatusChanged");
        Set<BluetoothDevice> pairedDevice = mBtAdapter.getBondedDevices();
        Log.e("TAG", "pairedDevice size: " + pairedDevice.size());

        try {
            if (pairedDevice.size() > 0) {
                for (BluetoothDevice device : pairedDevice) {
                    final String connectedDeviceName = device.getName();
                    final String iotDeviceName = getIOTDevice().id;
                    Log.e("TAG", "connectedDeviceName: " +connectedDeviceName);
                    Log.e("TAG", "iotDeviceName: " + iotDeviceName);
                    if(connectedDeviceName.equals(iotDeviceName)) {
                        onBluetoothDeviceConnected();
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        // Does anything only if a device discovery has been scheduled.
        if (bluetoothDiscoveryScheduled) {

            int bluetoothState = mBtAdapter.getState();

            switch (bluetoothState) {
                case BluetoothAdapter.STATE_ON:
                    // Bluetooth is ON.
                    Log.e("TAG", "Bluetooth succesfully enabled, starting discovery");
                    startDiscovery();
                    // Resets the flag since this discovery has been performed.
                    bluetoothDiscoveryScheduled = false;
                    break;
                case BluetoothAdapter.STATE_OFF:
                    // Bluetooth is OFF.
                    Log.e("TAG", "Error while turning Bluetooth on.");
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
    public void onDevicePairingEnded() {
        Log.e("TAG", "onDevicePairingEnded");

        Set<BluetoothDevice> pairedDevice = mBtAdapter.getBondedDevices();
        Log.e("TAG", "pairedDevice size: " + pairedDevice.size());

        try {
            if (pairedDevice.size() > 0) {
                for (BluetoothDevice device : pairedDevice) {
                    final String connectedDeviceName = device.getName();
                    final String iotDeviceName = getIOTDevice().id;
                    Log.e("TAG", "connectedDeviceName: " +connectedDeviceName);
                    Log.e("TAG", "iotDeviceName: " + iotDeviceName);
                    if(connectedDeviceName.equals(iotDeviceName)) {
                        onBluetoothDeviceConnected();
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
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
}
