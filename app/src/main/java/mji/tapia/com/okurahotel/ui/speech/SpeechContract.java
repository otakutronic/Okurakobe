package mji.tapia.com.okurahotel.ui.speech;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.service.stt.STTService;
import mji.tapia.com.service.tts.TTSService;

/**
 * Created by Sami on 9/22/2017.
 */

public interface SpeechContract {
    interface View extends BaseView {

        void updateAnimation(STTService.STTState sttState);

        void updateIconSize(Integer level);

        void updateExampleText();

        void updateSpeechText(String string);
    }

    interface Presenter extends ScopedPresenter {

    }
}
