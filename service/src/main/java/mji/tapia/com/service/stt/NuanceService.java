package mji.tapia.com.service.stt;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.PcmFormat;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.RecognitionType;
import com.nuance.speechkit.RecognizedPhrase;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Sami on 10/31/2017.
 *
 */

public class NuanceService  extends Transaction.Listener implements STTService {

    private static final String APP_KEY = "adf700ef491eded90d9dae6bab46b7b7cef01ae01803b546a38f48d7a43c3f513340407d5e1f566bbc04d7684a8e1f187f39f8973daddde03c26b7e062f73295";
    private static final String APP_ID = "NMDPPRODUCTION_sami_Dridi_Tapia_20160628024326";
    private static final String SERVER_HOST = "jir.nmdp.nuancemobility.net";
    private static final String SERVER_PORT = "443";
    private static final Uri SERVER_URI = Uri.parse("nmsps://" + APP_ID + "@" + SERVER_HOST + ":" + SERVER_PORT);
    //Only needed if using NLU
    public static final String CONTEXT_TAG = "!NLU_CONTEXT_TAG!";

    public static final PcmFormat PCM_FORMAT = new PcmFormat(PcmFormat.SampleFormat.SignedLinear16, 16000, 1);
    private Audio startEarcon;
    private Audio stopEarcon;
    private Audio errorEarcon;

    private Session speechSession;
    private Transaction recoTransaction;

    private static String TAG = "Nuance";

    private Context context;

    private boolean isCancelled = false;

    private PublishSubject<List<String>> listPublishSubject = PublishSubject.create();

    private PublishSubject<Integer> voiceChangeListener = PublishSubject.create();

    private BehaviorSubject<STTState> sttStateBehaviorSubject = BehaviorSubject.create();

    public NuanceService(Context context) {
        this.context = context;
        speechSession = Session.Factory.session(context, SERVER_URI, APP_KEY);
        loadEarcons();
        sttStateBehaviorSubject.onNext(STTState.IDLE);
    }

    @Override
    public PublishSubject<List<String>> result() {
        return listPublishSubject;
    }

    @Override
    public void listen() {
        if (sttStateBehaviorSubject.getValue() == STTState.IDLE) {
            isCancelled = false;
            recognize();
        }
    }

    @Override
    public void stopListening() {
        stopRecording();
        cancel();
    }

    @Override
    public Observable<Integer> voiceLevelChange() {
        return voiceChangeListener;
    }

    @Override
    public Observable<STTState> sttStateChange() {
        return sttStateBehaviorSubject;
    }


    public void recognize() {
        //Setup our Reco transaction options.
        if (sttStateBehaviorSubject.getValue() != STTState.LISTENING) {
            Transaction.Options options = new Transaction.Options();
            options.setRecognitionType(RecognitionType.DICTATION);
            options.setDetection(DetectionType.Long);
//            if(language.equals(com.tapia.mji.tapia.Languages.Language.LanguageID.FRENCH)) {
//                options.setLanguage(new com.nuance.speechkit.Language("fra-FRA"));
//            }
//            else if(language.equals(com.tapia.mji.tapia.Languages.Language.LanguageID.JAPANESE)){
            options.setLanguage(new com.nuance.speechkit.Language("jpn-JPN"));
//            }
//            else if(language.equals(com.tapia.mji.tapia.Languages.Language.LanguageID.ARABIC)){
//                options.setLanguage(new com.nuance.speechkit.Language("ara-XWW"));
//            }
//            else {
//                options.setLanguage(new com.nuance.speechkit.Language("eng-USA"));
//            }
            //options.setEarcons(startEarcon,stopEarcon,errorEarcon,null);

            //Start listening
            recoTransaction = speechSession.recognize(options, this);
        }
    }

    @Override
    public void onStartedRecording(Transaction transaction) {
        Log.e(TAG, "\nonStartedRecording");
        //We have started recording the users voice.
        //We should update our state and start polling their volume.
        if (!isCancelled) {
            setState(STTState.LISTENING);
        }

    }

    @Override
    public void onFinishedRecording(Transaction transaction) {
        Log.e(TAG, "\nonFinishedRecording ");
        //We have finished recording the users voice.
        //We should update our state and stop polling their volume.
        if (!isCancelled) {
            setState(STTState.PROCESSING);
        }
    }

    @Override
    public void onRecognition(Transaction transaction, Recognition recognition) {
        Log.e(TAG, "\nonRecognition: " + recognition.getText());
        setState(STTState.IDLE);
        List<RecognizedPhrase> myPhrases = recognition.getDetails();
        ArrayList<String> results = new ArrayList<>();
        for (RecognizedPhrase rp : myPhrases) {
            results.add(rp.getText());
        }
//        if(onRecognitionCompleteListener != null) {
//            onRecognitionCompleteListener.onRecognitionComplete(results);
//        } else {
//            Log.e("Nuance", "There is no onRecognition complete listener");
//        }

        listPublishSubject.onNext(results);
    }

    @Override
    public void onSuccess(Transaction transaction, String s) {
        Log.e(TAG, "\nonSuccess");
        //Notification of a successful transaction. Nothing to do here.
    }

    @Override
    public void onError(Transaction transaction, String s, TransactionException e) {
        Log.e(TAG, "\nonError:" + e.getMessage() + ". " + s);
        //The user could also be offline, so be sure to handle this case appropriately.
        //We will simply reset to the idle state.
        setState(STTState.IDLE);
        switch (e.getMessage()) {
            case "The transaction was cancelled":
                isCancelled = true;
                setState(STTState.IDLE);
                break;
            default:
                recognize();
                break;
        }
    }


    private void loadEarcons() {
        //Load all the earcons from disk
//        startEarcon = new Audio(context, R.raw.sk_start, PCM_FORMAT);
//        stopEarcon = new Audio(context, R.raw.sk_stop, PCM_FORMAT);
//        errorEarcon = new Audio(context, R.raw.sk_error, PCM_FORMAT);
    }

    /**
     * Stop recording the user
     */
    private void stopRecording() {
        if (recoTransaction != null) {
            recoTransaction.stopRecording();
        }
    }

    /**
     * Cancel the Reco transaction.
     * This will only cancel if we have not received a response from the server yet.
     */
    private void cancel() {
        if (recoTransaction != null) {
            recoTransaction.cancel();
        }
    }

    public void restartListening() {
        stopRecording();
        recognize();
    }


    private void setState(STTState newState) {
        sttStateBehaviorSubject.onNext(newState);

    }
}