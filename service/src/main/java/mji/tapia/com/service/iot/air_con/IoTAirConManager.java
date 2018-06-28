package mji.tapia.com.service.iot.air_con;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.IoTManager;

/**
 * Created by Andy on 06/01/2018.
 */

public interface IoTAirConManager extends IoTManager {

    String TAG = "IoTAirConManager";

    Completable setAirConState(boolean state);

    Observable<Boolean> getAirConState();
}
