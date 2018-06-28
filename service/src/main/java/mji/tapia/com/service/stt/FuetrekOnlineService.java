package mji.tapia.com.service.stt;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fuetrek.fsr.FSRService;
import com.fuetrek.fsr.FSRServiceEnum.BackendType;
import com.fuetrek.fsr.FSRServiceEnum.EncryptType;
import com.fuetrek.fsr.FSRServiceEnum.EventType;
import com.fuetrek.fsr.FSRServiceEnum.IOMode;
import com.fuetrek.fsr.FSRServiceEnum.LogLevel;
import com.fuetrek.fsr.FSRServiceEnum.Ret;
import com.fuetrek.fsr.FSRServiceEventListener;
import com.fuetrek.fsr.entity.AbortInfoEntity;
import com.fuetrek.fsr.entity.CodecAssignEntity;
import com.fuetrek.fsr.entity.ConstructEntity;
import com.fuetrek.fsr.entity.DtypeInfoEntity;
import com.fuetrek.fsr.entity.IOSourceEntity;
import com.fuetrek.fsr.entity.IndividualEntity;
import com.fuetrek.fsr.entity.RecognizeEntity;
import com.fuetrek.fsr.entity.ResultInfoEntity;
import com.fuetrek.fsr.entity.StartParamEntity;
import com.fuetrek.fsr.entity.VoiceControlEntity;
import com.fuetrek.fsr.entity.VoiceEncryptEntity;
import com.fuetrek.fsr.exception.FSRServiceException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.SingleSubject;
import mji.tapia.com.service.wake_up.FuetrekLocalService;

import static com.fuetrek.fsr.FSRServiceEnum.EventType.NotifyAutoStart;

/**
 * Created by Sami on 9/26/2017.
 */

public class FuetrekOnlineService implements STTService, FSRServiceEventListener {

    //TODO
    private Context context;
    private STTState state = STTState.IDLE;

    private Handler handler_;

    private boolean isProgressRecog = false;
    private List<String> progressResultList = null;
    private int progressResultCounter = 0;

    FSRService fsr_;
    VoiceControlEntity voiceControl;
    StartParamEntity startParam;
    Ret ret_;
    String result_;

    private static BackendType setting_BackendType_ = BackendType.D;
    private static String setting_Codec_ = "SPEEX";
    private static int setting_ResordSize_ = 240;

    private static String setting_BackendURL_ = "asp.fuetrek.co.jp/FSSP/?group=mji-tapia";
    private static String setting_BackendService_ = "mji-tapia";
    private static int    setting_PortNumber = 80;

    //private static String encryptPhrase = "SKAONBDEA";
    private static String encryptPhrase = null;

    static boolean isAllowed = true;

    BehaviorSubject<STTState> sttStateBehaviorSubject = BehaviorSubject.create();

    PublishSubject<Integer> voiceLevelChange = PublishSubject.create();

    public FuetrekOnlineService(Context context){
        this.context = context;
        init();
    }

    boolean requestlistening = false;
    boolean initComplete = false;

    PublishSubject<List<String>> listenSubject = PublishSubject.create();

    @Override
    public PublishSubject<List<String>> result() {
        return listenSubject;
    }

    @Override
    public void listen() {


        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (initComplete) {
            try {
                fsr_.startRecognition(setting_BackendType_, voiceControl, startParam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            requestlistening = true;
        }
    }

    @Override
    public void stopListening() {
        if(sttStateBehaviorSubject.getValue() != STTState.IDLE) {
            stop();
        }
    }

    @Override
    public Observable<Integer> voiceLevelChange() {
        return voiceLevelChange;
    }

    @Override
    public Observable<STTState> sttStateChange() {
        return sttStateBehaviorSubject;
    }

    @Override
    public void notifyEvent(Object appHandle, EventType eventType, BackendType backendType, Object eventData) {
        switch(eventType){
            case NotifyAutoStart:
                Log.e("Fuetrek","NotifyAutoStart");
                break;
            case NotifyAutoCancel:
                Log.e("Fuetrek","NotifyAutoCancel");
                break;
            case NotifyAutoStopSilence:
                Log.e("Fuetrek","NotifyAutoStopSilence");
                setState(STTState.PROCESSING);
                break;
            case NotifyAutoStopTimeout:
                Log.e("Fuetrek","NotifyAutoStopTimeout");
                break;
            case NotifySessionResult:
                Log.e("Fuetrek","NotifySessionResult");
                break;
            case CompleteCancel:
                Log.e("Fuetrek","CompleteCancel");
                setState(STTState.IDLE);
                break;
            case CompleteSetModel:
                Log.e("Fuetrek","CompleteSetModel");
                break;
            case CompleteStart:
                Log.e("Fuetrek","CompleteStart");
                setState(STTState.LISTENING);
                break;
            case CompleteStop:
                Log.e("Fuetrek","CompleteStop");
                break;
            case CompleteConnect:
                Log.e("Fuetrek","CompleteConnect");
                ret_ = (Ret)eventData;
                try {
                    if( ret_ != Ret.RetOk ){
                        Exception e = new Exception("filed connectSession.");
                        throw e;
                    }

                    IOSourceEntity ioSource = new IOSourceEntity();
                    fsr_.setIOSource(IOMode.ModePcmMic, ioSource);

                    voiceControl = new VoiceControlEntity();
                    voiceControl.setAutoStart(true);
                    voiceControl.setAutoStop(true);
                    voiceControl.setVadOffTime((short) 500);
                    voiceControl.setVadSensibility(0);
                    voiceControl.setListenTime(0);
                    voiceControl.setLevelSensibility(10);

                    startParam = new StartParamEntity();
                    startParam.setDTypeService(setting_BackendService_);
                    startParam.setDTypeResultProgress(isProgressRecog);

                    if (null != encryptPhrase) {
                        final VoiceEncryptEntity dTypeVoiceEncrypt = new VoiceEncryptEntity();
                        dTypeVoiceEncrypt.setType(EncryptType.DS);
                        dTypeVoiceEncrypt.setEncryptData(encryptPhrase);
                        startParam.setDTypeVoiceEncrypt(dTypeVoiceEncrypt);
                    }

                    initComplete = true;
                    if (requestlistening) {
                        requestlistening = false;
                        listen();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case CompleteDisconnect:
                Log.e("Fuetrek","CompleteDisconnect");
                break;

            case NotifyEndRecognition:
                Log.e("Fuetrek","NotifyEndRecognition");
                setState(STTState.IDLE);
                try {
                    RecognizeEntity recog = fsr_.getSessionResultStatus(setting_BackendType_);
                    String result = "(no result)";
                    final List<String> resultList = new ArrayList<>();
                    if (recog.getCount() > 0) {
                        ResultInfoEntity info = fsr_.getSessionResult(setting_BackendType_, 1);
                        result = info.getText();
                    }
                    for(int i = 0; i < recog.getCount(); i++){
                        ResultInfoEntity info = fsr_.getSessionResult(setting_BackendType_, i + 1);
                        Log.e("Fuertrek",info.getText());
                        resultList.add(info.getText());
                    }

                    if(result.equals("(no result)")){
                        listen();
                    }
                    else {
                        listenSubject.onNext(resultList);
                        setState(STTState.IDLE);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case NotifyResultProgress:
                Log.e("Fuetrek","NotifyResultProgress");
                getProgressResult();
                break;

            case NotifyLevel:
                Log.e("Fuetrek","NotifyLevel " + eventData );
                voiceLevelChange.onNext((int)eventData);
                break;
        }
    }

    @Override
    public void notifyAbort(Object o, AbortInfoEntity abortInfoEntity) {
        Exception e = new Exception("Abort!!");
    }

    void init(){
        try{
            final IndividualEntity individual = new IndividualEntity();
            individual.setApplication(context.getApplicationContext().getPackageName());
            individual.setTerminalId("SIM Serial");
            individual.setTerminalType("IMEI number");

            final CodecAssignEntity codecAssign = new CodecAssignEntity();
            codecAssign.setCodec(setting_Codec_);
            codecAssign.setRecordSize(setting_ResordSize_);

            final ConstructEntity construct = new ConstructEntity();
            construct.setIndividual(individual);
            construct.setCodecAssign(codecAssign);
            construct.setSpeechTime(10000);
            construct.setRecognizeTime(10000);
            construct.setLogLevel(LogLevel.Debug);
            construct.setRapidMode(false);

            if( null == fsr_ ){
                fsr_ = new FSRService(this, this, construct);
            }
            DtypeInfoEntity dtypeInfo = new DtypeInfoEntity();
            dtypeInfo.setBackend(setting_BackendURL_);
            dtypeInfo.setPortNo(setting_PortNumber);
            dtypeInfo.setConnectLimit(10000);
            fsr_.connectSession(setting_BackendType_, dtypeInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run() {

    }

    public void setAccount(String backendURL, String backendService, int portNumber){
        setting_BackendService_ = backendService;
        setting_BackendURL_ = backendURL;
        setting_PortNumber = portNumber;
    }

    final Runnable notifyFinished = new Runnable() {
        public void run() {
            switch (result_){
                case "(no result)":
                    break;
                case "(error)":
                    break;
                default:
                    List<String> resultList = new ArrayList<>();
                    resultList.add(result_);
                    listenSubject.onNext(resultList);
                    break;
            }

            progressResultCounter = 0;
            progressResultList = null;
        }
    };
    final Runnable notifyProgressResult = new Runnable() {
        public void run() {
            if( null == progressResultList ){
                return;
            }

            String text = progressResultList.get(progressResultCounter);

            if( 0 == progressResultCounter ){

            }

            progressResultCounter++;
        }
    };

    private void getProgressResult() {
        if( progressResultList == null ){
            progressResultList = new ArrayList<String>();
        }

        try {
            String resultInfo = fsr_.getProgressText();

            resultInfo = resultInfo.replace("/", "");

            Log.e("SampleTypeD", "additional result = " + resultInfo.toString());

            progressResultList.add(resultInfo);
            handler_.post(notifyProgressResult);
        }
        catch (FSRServiceException e) {
            e.printStackTrace();
        }

        return;
    }

    public void stop() {
        try {
            fsr_.cancelRecognition();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setState(STTState newState){
        sttStateBehaviorSubject.onNext(newState);
    }
}
