package mji.tapia.com.service.tts;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by Sami on 9/26/2017.
 */

public interface TTSService {

    enum TTSState {
        IDLE,
        SPEAKING
    }

    Completable say(String speech);

    void stopSpeaking(boolean callOnComplete);

    Observable<TTSState> ttsStateChange();

    Completable init();

}
