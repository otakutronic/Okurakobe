package mji.tapia.com.service.stt;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Sami on 9/26/2017.
 */

public interface STTService {


    class TimeOutException extends Exception {

    }

    enum STTState {
        IDLE,
        LISTENING,
        PROCESSING,
        USER_START_SPEAKING,
        USER_STOP_SPEAKING
    }

    PublishSubject<List<String>> result();

    void listen();

    void stopListening();

    Observable<Integer> voiceLevelChange();

    Observable<STTState> sttStateChange();

}
