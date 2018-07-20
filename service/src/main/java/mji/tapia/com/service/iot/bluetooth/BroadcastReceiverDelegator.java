package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.io.Closeable;

public class BroadcastReceiverDelegator extends BroadcastReceiver implements Closeable {

    /**
     * Callback for Bluetooth events.
     */
    //private final BluetoothDiscoveryDeviceListener listener;

    /**
     * Tag string used for logging.
     */
    private final String TAG = "BroadcastReceiver";

    /**
     * The context of this object.
     */
    private final Context context;

    private BluetoothDiscoveryServiceImpl bluetoothService;

    public BroadcastReceiverDelegator(Context context, BluetoothDiscoveryServiceImpl bluetoothService) {
        this.context = context;
        this.bluetoothService = bluetoothService;
        this.bluetoothService.setBluetoothController(bluetoothService);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(String.valueOf(BluetoothDevice.BOND_BONDED));
        filter.addAction(String.valueOf(BluetoothDevice.ACTION_ACL_CONNECTED));
        context.registerReceiver(this, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("TAG", "Device discovered! " + action);

        switch (action) {
            case BluetoothDevice.ACTION_FOUND :
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothService.onDeviceDiscovered(device);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED :
                // Discovery has ended.
                bluetoothService.onDeviceDiscoveryEnd();
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED :
                // Discovery state changed.
                bluetoothService.onBluetoothStatusChanged();
                break;
            case BluetoothDevice.ACTION_BOND_STATE_CHANGED :
                // Pairing state has changed.
                bluetoothService.onDeviceBondStateChanged();
                break;

            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothService.setBluetoothPairingPin(device);
                //bluetoothService.onBluetoothStatusChanged();
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                //bluetoothService.onBluetoothStatusChanged();
                break;
            default :
                // Does nothing.
                break;
        }
    }

    /**
     * Called when device discovery starts.
     */
    public void onDeviceDiscoveryStarted() {
        bluetoothService.onDeviceDiscoveryStarted();
    }

    /**
     * Called when device discovery ends.
     */
    public void onDeviceDiscoveryEnd() {
        bluetoothService.onDeviceDiscoveryEnd();
    }

    /**
     * Called when the Bluetooth has been enabled.
     */
    public void onBluetoothTurningOn() {
        bluetoothService.onBluetoothTurningOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        context.unregisterReceiver(this);
    }

}
