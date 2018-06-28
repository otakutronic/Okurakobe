/**
 * MIT License
 * <p>
 * Copyright (c) 2017 Donato Rimenti
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.io.Closeable;
import java.lang.reflect.Method;

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
                bluetoothService.onDevicePairingEnded();
                break;
            case BluetoothDevice.ACTION_PAIRING_REQUEST:
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                setBluetoothPairingPin(device);
                //bluetoothService.onDeviceDiscoveryEnd();
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:

                break;
            default :
                // Does nothing.
                break;
        }
    }

    /**
     * programmatically sets the pin for pairing
     */
    public void setBluetoothPairingPin(BluetoothDevice device) {
        try {
            final String stringPin = bluetoothService.getIOTDevice().pin;
            byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, stringPin);
            Method m = device.getClass().getMethod("setPin", byte[].class);
            m.invoke(device, pin);
            //device.getClass().getMethod("cancelPairingUserInput", boolean.class).invoke(device);
            //device.getClass().getMethod("cancelPairingUserInput").invoke(device);
            //device.cancelPairingUserInput();
            //device.setPairingConfirmation( true );
            device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);

        } catch (Exception e) {

            e.printStackTrace();
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
