package mji.tapia.com.service.iot.bluetooth.source;

import android.content.SharedPreferences;
import com.google.gson.Gson;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryServiceImpl;
import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 4/9/2018.
 *
 */

public class LocalDataSource {

    static final private String IOT_DEVICE_SHARED_PREFERENCE = "iot_device_shared_preference";
    static final private String ID = "0";

    private SharedPreferences sharedPreferences;

    private BluetoothDiscoveryService.IOTDevice device;

    public LocalDataSource(PreferenceUtils sharedPreferenceManager) {
        sharedPreferences = sharedPreferenceManager.getSharedPreference(IOT_DEVICE_SHARED_PREFERENCE);
        setupDevice();
    }

    private void setupDevice() {
        String value = sharedPreferences.getString(ID, null);
        if(value == null) {
            device = getDefaultDevice();
            return;
        }
        Gson gson = new Gson();
        device = gson.fromJson(value, BluetoothDiscoveryService.IOTDevice.class);
    }

    public BluetoothDiscoveryService.IOTDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDiscoveryService.IOTDevice device) {
        this.device = device;

        Gson gson = new Gson();
        String deviceJsonString = gson.toJson(device);

        sharedPreferences.edit().putString(ID, deviceJsonString).apply();
    }

    public void deleteDevice() {
        String value = sharedPreferences.getString(ID, null);
        if(value == null) {
            return;
        }
        sharedPreferences.edit().remove(ID).apply();
        sharedPreferences.edit().remove(ID).commit();
    }

    private BluetoothDiscoveryService.IOTDevice getDefaultDevice() {
        BluetoothDiscoveryService.IOTDevice iOTDevice = new BluetoothDiscoveryService.IOTDevice();
        iOTDevice.id = BluetoothDiscoveryServiceImpl.IOT_DEVICE_ID + "0101";
        iOTDevice.pin = BluetoothDiscoveryServiceImpl.IOT_DEVICE_PIN + "0101";
        iOTDevice.address = BluetoothDiscoveryServiceImpl.IOT_DEVICE_ADDRESS;
        return iOTDevice;
    }
}
