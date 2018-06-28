package mji.tapia.com.service.battery;


import io.reactivex.Observable;

/**
 * Created by andy on 2/1/2018.
 */

public interface BatteryManager {

    void init();
    Observable<Float> getBatteryLevelObservable();

    //true= charging false = not charging
    Observable<Boolean> getBatteryChargingStateObservable();
}
