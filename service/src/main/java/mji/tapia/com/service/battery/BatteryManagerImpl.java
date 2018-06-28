package mji.tapia.com.service.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by Sami on 2/1/2018.
 */

public class BatteryManagerImpl implements BatteryManager {

    private Context context;

    private BehaviorSubject<Boolean> isChargingBehaviorSubject;

    private BehaviorSubject<Float> batteryLevelBehaviorSubject;

    public BatteryManagerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);

        int level = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1);

        isChargingBehaviorSubject.onNext(status == android.os.BatteryManager.BATTERY_STATUS_CHARGING || status == android.os.BatteryManager.BATTERY_STATUS_FULL);
        batteryLevelBehaviorSubject.onNext(level/ (float) scale);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1);
                int status = intent.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1);

                isChargingBehaviorSubject.onNext(status == android.os.BatteryManager.BATTERY_STATUS_CHARGING || status == android.os.BatteryManager.BATTERY_STATUS_FULL);
                batteryLevelBehaviorSubject.onNext(level/ (float) scale);
            }
        }, intentFilter);

    }

    @Override
    public Observable<Float> getBatteryLevelObservable() {
        return batteryLevelBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getBatteryChargingStateObservable() {
        return isChargingBehaviorSubject;
    }

}
