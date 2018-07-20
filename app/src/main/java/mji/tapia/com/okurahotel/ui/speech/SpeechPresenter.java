package mji.tapia.com.okurahotel.ui.speech;

import android.app.Activity;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.data.voice_command.VoiceCommand;
import mji.tapia.com.data.voice_command.VoiceCommandRepository;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.service.iot.air_con.IoTAirConManager;
import mji.tapia.com.service.iot.curtain.IoTCurtainManager;
import mji.tapia.com.service.iot.light.IoTLightManager;
import mji.tapia.com.service.stt.STTService;
import mji.tapia.com.service.tts.TTSService;

/**
 * Created by Sami on 9/21/2017.
 */

public class SpeechPresenter extends BasePresenter<SpeechContract.View> implements SpeechContract.Presenter {

    static private final String  OPEN_CURTAIN = "open_curtain";
    static private final String  CLOSE_CURTAIN = "close_curtain";
    static private final String  STOP_CURTAIN = "stop_curtain";

    static private final String  ROOM_LIGHT_ON = "turn_on_room";
    static private final String  ROOM_LIGHT_OFF = "turn_off_room";
    static private final String  SPOT_LIGHT_ON = "turn_on_spot";
    static private final String  SPOT_LIGHT_OFF = "turn_off_spot";
    static private final String  FOOT_LIGHT_ON = "turn_on_foot";
    static private final String  FOOT_LIGHT_OFF = "turn_off_foot";
    static private final String  ALL_LIGHT_ON = "turn_on_all";
    static private final String  ALL_LIGHT_OFF = "turn_off_all";

    static private final String  TURN_AIR_CON_ON = "turn_air_con_on";
    static private final String  TURN_AIR_CON_OFF = "turn_air_con_off";

    @Inject
    IoTLightManager lightManager;

    @Inject
    IoTAirConManager airConManager;

    @Inject
    IoTCurtainManager curtainManager;

    @Inject
    STTService sttService;

    @Inject
    TTSService ttsService;

    @Inject
    Activity activity;

    @Inject
    VoiceCommandRepository voiceCommandRepository;

    private HashMap<String, Runnable> commandActionMap = new HashMap<>();

    private Disposable resultDisposable;
    private Disposable volumeDisposable;
    private Disposable sttStateDisposable;

    private Disposable timerExampleDisposable;

    private Disposable timeOutDisposable;

    private int failedCounter = 0;

    private Completable timerOutCompletable = Completable.timer(20000,TimeUnit.MILLISECONDS);

    public SpeechPresenter(SpeechContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        commandActionMap.put(ALL_LIGHT_ON, () -> lightManager.setLightStateAll(true));
        commandActionMap.put(ALL_LIGHT_OFF, () -> lightManager.setLightStateAll(false));

        commandActionMap.put(ROOM_LIGHT_ON, () -> lightManager.setLightState(0, true));
        commandActionMap.put(ROOM_LIGHT_OFF, () -> lightManager.setLightState(0, false));

        commandActionMap.put(SPOT_LIGHT_ON, () -> lightManager.setLightState(1, true));
        commandActionMap.put(SPOT_LIGHT_OFF, () -> lightManager.setLightState(1, false));

        commandActionMap.put(FOOT_LIGHT_ON, () -> lightManager.setLightState(2, true));
        commandActionMap.put(FOOT_LIGHT_OFF, () -> lightManager.setLightState(2, false));

        commandActionMap.put(OPEN_CURTAIN, () -> curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.OPEN));
        commandActionMap.put(STOP_CURTAIN, () -> curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.STOP));
        commandActionMap.put(CLOSE_CURTAIN, () -> curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.CLOSE));

        commandActionMap.put(TURN_AIR_CON_ON, () -> airConManager.setAirConState(true));
        commandActionMap.put(TURN_AIR_CON_OFF, () -> airConManager.setAirConState(false));
    }

    @Override
    public void activate() {
        super.activate();
        doIfViewNotNull(view -> view.updateSpeechText(activity.getString(R.string.start_discussion)));
        ttsService.say(activity.getString(R.string.start_discussion)).subscribe(() -> sttService.listen(), err -> {
            //TODO manage errors
            err.printStackTrace();
        });
        resultDisposable = sttService.result().subscribe(strings -> {
            timeOutDisposable.dispose();
            timeOutDisposable = timerOutCompletable.retry().subscribe(() -> {
                router.navigateToSleepScreen();
                router.closeScreen();
            });

            VoiceCommand voiceCommand = voiceCommandRepository.getBestMatchIn(R.xml.command_activity_speech, strings);

            if(voiceCommand != null && commandActionMap.containsKey(voiceCommand.getId())){
                failedCounter= 0;
                commandActionMap.get(voiceCommand.getId()).run();
                doIfViewNotNull(view -> view.updateSpeechText(getAnswerToCommandString(voiceCommand)));
                ttsService.say(getAnswerToCommandString(voiceCommand)).subscribe(() -> sttService.listen()
                        , err -> {
                            //TODO manage error
                            err.printStackTrace();
                        });
            }
            else {
                //no match found
                if(failedCounter >= 4){
                    router.navigateToSleepScreen();
                    router.closeScreen();
                    return;
                }
                failedCounter++;
                Random random = new Random();
                String[] dont_understand_strings = activity.getResources().getStringArray(R.array.dont_understand);
                String dont_understand_str = dont_understand_strings[random.nextInt(dont_understand_strings.length)];
                doIfViewNotNull(view -> view.updateSpeechText(dont_understand_str));
                ttsService.say(dont_understand_str).subscribe((
                        () -> sttService.listen()
                ), err -> {
                    //TODO manage error
                    err.printStackTrace();
                });
            }
        }, err -> {

        });

        volumeDisposable = sttService.voiceLevelChange().subscribe(integer -> doIfViewNotNull(view -> view.updateIconSize(integer)));

        timeOutDisposable = timerOutCompletable.subscribe(() -> {
            router.navigateToSleepScreen();
            router.closeScreen();
        });

        sttStateDisposable = sttService.sttStateChange().subscribe(sttState -> {
            doIfViewNotNull(view -> view.updateAnimation(sttState));
        });

        timerExampleDisposable =  Observable.interval(3000,15000, TimeUnit.MILLISECONDS).subscribe(aLong -> {
            doIfViewNotNull(SpeechContract.View::updateExampleText);
        });
    }

    @Override
    public void deactivate() {
        super.deactivate();
        ttsService.stopSpeaking(false);
        sttService.stopListening();
        resultDisposable.dispose();
        volumeDisposable.dispose();
        timerExampleDisposable.dispose();
        timeOutDisposable.dispose();
    }

    @Override
    public void destroy() {
        super.destroy();
        commandActionMap.clear();
    }

    @Override
    public void back() {
        router.closeScreen();
    }

    private String getAnswerToCommandString(VoiceCommand voiceCommand){
        switch (voiceCommand.getId()){
            case OPEN_CURTAIN:
                return activity.getString(R.string.curtain_opened);
            case CLOSE_CURTAIN:
                return activity.getString(R.string.curtain_closed);
            case STOP_CURTAIN:
                return activity.getString(R.string.curtain_stopped);
            case ROOM_LIGHT_ON:
                return activity.getString(R.string.room_turned_on);
            case ROOM_LIGHT_OFF:
                return activity.getString(R.string.room_turned_off);
            case FOOT_LIGHT_ON:
                return activity.getString(R.string.foot_turned_on);
            case FOOT_LIGHT_OFF:
                return activity.getString(R.string.foot_turned_off);
            case SPOT_LIGHT_ON:
                return activity.getString(R.string.spot_turned_on);
            case SPOT_LIGHT_OFF:
                return activity.getString(R.string.spot_turned_off);
            case ALL_LIGHT_ON:
                return activity.getString(R.string.all_turned_on);
            case ALL_LIGHT_OFF:
                return activity.getString(R.string.all_turned_off);
            case TURN_AIR_CON_ON:
                return activity.getString(R.string.ac_turned_on);
            case TURN_AIR_CON_OFF:
                return activity.getString(R.string.ac_turned_off);
        }
        return null;
    }
}
