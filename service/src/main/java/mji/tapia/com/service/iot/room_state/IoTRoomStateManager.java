package mji.tapia.com.service.iot.room_state;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.IoTManager;

/**
 * Created by Sami on 2/1/2018.
 *
 */

public interface IoTRoomStateManager extends IoTManager {

    String TAG = "IoTRoomStateManager";

    enum RoomOccupancyState {
        VACANT,
        GUEST,
        STAFF;
    }

    Completable setDoNotDisturbState(Boolean isEnable);

    Completable setMakeUpState(Boolean isEnable);

    Completable setLaundryServiceState(Boolean isEnable);

    Observable<Boolean> getDoNotDisturbStateStateObservable();

    Observable<Boolean> getMakeUpStateObservable();

    Observable<Boolean> getLaundryServiceStateObservable();

    Observable<Boolean> getRoomOccupancyStateObservable();
}
