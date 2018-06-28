package mji.tapia.com.service.iot.curtain;

import io.reactivex.Completable;
import io.reactivex.Observable;
import mji.tapia.com.service.iot.IoTManager;

/**
 * Created by Sami on 2/1/2018.
 * curtain manager
 */

public interface IoTCurtainManager extends IoTManager {

    String TAG = "IoTCurtainManager";

    enum IoTCurtain {
        CURTAIN
    }

    enum CurtainState {
        OPEN,
        STOP,
        CLOSE
    }

    Observable<CurtainState> curtainStateObservable(IoTCurtain iotCurtain);

    Completable setCurtainState(IoTCurtain targetCurtain, CurtainState curtainState);

}
