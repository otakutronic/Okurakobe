package mji.tapia.com.service.wake_up;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.fuetrek.fsr.FSRService;
import com.fuetrek.fsr.FSRServiceEnum.BackendType;
import com.fuetrek.fsr.FSRServiceEnum.EventType;
import com.fuetrek.fsr.FSRServiceEnum.IOMode;
import com.fuetrek.fsr.FSRServiceEnum.Ret;
import com.fuetrek.fsr.FSRServiceEventListener;
import com.fuetrek.fsr.entity.AbortInfoEntity;
import com.fuetrek.fsr.entity.CodecAssignEntity;
import com.fuetrek.fsr.entity.ConstructEntity;
import com.fuetrek.fsr.entity.IOSourceEntity;
import com.fuetrek.fsr.entity.IndividualEntity;
import com.fuetrek.fsr.entity.ModelEntity;
import com.fuetrek.fsr.entity.RecognizeEntity;
import com.fuetrek.fsr.entity.ResultInfoEntity;
import com.fuetrek.fsr.entity.StartParamEntity;
import com.fuetrek.fsr.entity.VoiceControlEntity;
import com.fuetrek.fsr.exception.FSRServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import mji.tapia.com.service.R;

/**
 * Created by Sami on 9/26/2017.
 */

public class FuetrekLocalService implements WakeUpService,FSRServiceEventListener {


    private PublishSubject<String> wakeWordDetectedPublishSubject = PublishSubject.create();

    public FuetrekLocalService(Context context){
        this.context = context;
        init();
    }


    @Override
    public void startService() {
        isStopped = false;
        listen();
    }

    @Override
    public Observable<String> wakeWordDetected() {
        return wakeWordDetectedPublishSubject;
    }

    @Override
    public void stopService() {
        isStopped = true;
        stopListening();
    }

    private enum STTState {
        IDLE,
        LISTENING,
        PROCESSING,
        USER_START_SPEAKING,
        USER_STOP_SPEAKING
    }

    STTState state = STTState.IDLE;
    //TODO
    private Context context;

    private Handler handler_;

    private boolean isProgressRecog = false;
    private List<String> progressResultList = null;
    private int progressResultCounter = 0;

    private FSRService fsr_;
    private VoiceControlEntity voiceControl;
    private StartParamEntity startParam;
    private Ret ret_;
    private String result_;
    private ModelEntity model;
    final private static BackendType setting_BackendType_ = BackendType.E;
    final private static String setting_Codec_ = "PCM";
    final private static int setting_ResordSize_ = 240;

    static boolean isAllowed = true;
    private boolean isSpeaking = false;

    private boolean isStopped = true;



    private boolean requestlistening = false;
    private boolean initComplete = false;

    private void listen() {
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

    private  void stopListening() {
        if(state != STTState.IDLE){
            stop();
        }
    }


    @Override
    public void notifyEvent(Object appHandle, EventType eventType, BackendType backendType, Object eventData) {
        switch(eventType){
            case NotifyAutoStart:
                Log.e("Fuetrek","NotifyAutoStart");
                isSpeaking = true;
                setState(STTState.USER_START_SPEAKING);
                break;
            case NotifyAutoCancel:
                Log.e("Fuetrek","NotifyAutoCancel");
                break;
            case NotifyAutoStopSilence:
                Log.e("Fuetrek","NotifyAutoStopSilence");
                setState(STTState.PROCESSING);
                isSpeaking = false;
                setState(STTState.USER_STOP_SPEAKING);
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
                isSpeaking = false;
                break;
            case CompleteSetModel:
                Log.e("Fuetrek","CompleteSetModel");
                model.setDivideDataArea(null);
                try {
                    if( ret_ != Ret.RetOk ) {
                        Exception e = new Exception("filed setModel.");
                        throw e;
                    }

                    IOSourceEntity ioSource = new IOSourceEntity();
                    fsr_.setIOSource(IOMode.ModePcmMic, ioSource);

                    voiceControl = new VoiceControlEntity();
                    voiceControl.setAutoStart(true);
                    voiceControl.setAutoStop(true);
                    voiceControl.setVadOffTime((short) 500);
                    voiceControl.setVadSensibility(10);
                    voiceControl.setListenTime(0);
                    voiceControl.setLevelSensibility(10);

                    startParam = new StartParamEntity();

                    initComplete = true;
                    if (requestlistening) {
                        requestlistening = false;
                        if(!isStopped)
                        listen();
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                break;
            case CompleteStart:
                Log.e("Fuetrek","CompleteStart");
                setState(STTState.LISTENING);
                if(isStopped){
                    stop();
                }

                    break;
            case CompleteStop:
                Log.e("Fuetrek","CompleteStop");
                break;
            case CompleteConnect:
                Log.e("Fuetrek","CompleteConnect");

                try {
                    model = setModelEntity();
                    fsr_.setModelData(model);

                    ret_ = (Ret)eventData;

                    if( ret_ != Ret.RetOk ){
                        Exception e = new Exception("filed connectSession.");
                        throw e;
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

                    Log.e("Fuetrek", result);

                    if(result.equals("(no result)")){
                        if(!isStopped)
                            listen();
//                        onRecognitionCompleteListener.onRecognitionComplete(null);
                    }
                    else {
                        for (String res: resultList) {
                            if(res.contains("はいオルタン")){
                                Log.e("Fuetrek", "オルタン detected............. ");
                                wakeWordDetectedPublishSubject.onNext(res);
                                break;
                            }
                        }
                        if(!isStopped)
                            listen();
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
                Log.e("Fuetrek","NotifyLevel");
                break;
        }
    }

    @Override
    public void notifyAbort(Object o, AbortInfoEntity abortInfoEntity) {
        Exception e = new Exception("Abort!!");
    }


    /*
     * モデルファイルの読込み
     * 現状はリソース内に持っているが、大きなモデルを使用する場合はsdcardに配置するなどの対策が必要になる
     */
    private ModelEntity setModelEntity() throws IOException {
        try {
            final ModelEntity modelEntity = new ModelEntity();
            byte[] buffer=null;
            InputStream in;
            in = context.getResources().openRawResource(R.raw.mji_robot_wake_up);

            // モデルデータ読込み

            int total;
            total = in.available();
            buffer = new byte[total];
            in.read(buffer);
            in.close();

            // Entity生成
            modelEntity.setTotalSize(total);
            modelEntity.setDivideDataArea(buffer);
            modelEntity.setLast(true);

            buffer=null;
            return modelEntity;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void init(){
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
            construct.setRecognizeTime(2000);
            construct.setRapidMode(true);

            if( null == fsr_ ){
                fsr_ = new FSRService(this, this, construct);
            }

            fsr_.connectSession(setting_BackendType_, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                    onRecognitionCompleteListener.onRecognitionComplete(resultList);
                    break;
            }

            progressResultCounter = 0;
            progressResultList = null;
        }
    };
    final private Runnable notifyProgressResult = () -> {
        if( null == progressResultList ){
            return;
        }

//        String text = progressResultList.get(progressResultCounter);

//        if( 0 == progressResultCounter ){
//
//        }

        progressResultCounter++;
    };

    private void getProgressResult() {
        if( progressResultList == null ){
            progressResultList = new ArrayList<>();
        }

        try {
            String resultInfo = fsr_.getProgressText();

            resultInfo = resultInfo.replace("/", "");

            Log.e("SampleTypeD", "additional result = " + resultInfo);

            progressResultList.add(resultInfo);
            handler_.post(notifyProgressResult);
        }
        catch (FSRServiceException e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        try {
            fsr_.cancelRecognition();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setState(STTState newState){
        state = newState;
    }
}
