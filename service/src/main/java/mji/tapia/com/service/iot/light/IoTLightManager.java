package mji.tapia.com.service.iot.light;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.IoTManager;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Sami on 2/1/2018.
 */

public interface IoTLightManager extends IoTManager {

    String TAG = "IoTLightManager";

    enum IoTLight {
        ROOM,
        SPOT,
        FOOT,
        ALL
    }

    class LightState {
        public Boolean isOn;
        public LightState(boolean isOn) {
            this.isOn = isOn;
        }
    }

    Completable setLightState(int lightType, boolean state);

    Completable setLightStateAll(boolean state);

    Observable<Boolean> getLightStateObserver();

    Observable<Boolean> getSpotStateObserver();

    Observable<Boolean> getFootStateObserver();

}
