package mji.tapia.com.service.wake_up;

import io.reactivex.Observable;

/**
 * Created by Sami on 9/26/2017.
 */

public interface WakeUpService {


    void startService();

    Observable<String> wakeWordDetected();

    void stopService();
}
