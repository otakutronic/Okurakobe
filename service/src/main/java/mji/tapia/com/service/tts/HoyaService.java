package mji.tapia.com.service.tts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.lang.reflect.Method;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import kr.co.voiceware.HIKARI;
import mji.tapia.com.service.R;

/**
 * Created by Sami on 9/26/2017.
 */

public class HoyaService implements TTSService {


    private Context context;

    private MediaPlayer player;

    private BehaviorSubject<TTSState> ttsStateBehaviorSubject = BehaviorSubject.create();

    private CompletableSubject completableSubject;

    public HoyaService(Context context){

        this.context = context;
        loadDB();
        player = new MediaPlayer();
        setState(TTSState.IDLE);
    }

    @Override
    public Completable say(String speech) {
        completableSubject = CompletableSubject.create();
        sayIn(speech);
        return completableSubject ;
    }

    @Override
    public Observable<TTSState> ttsStateChange() {
        return null;
    }


    private int sayIn(String speech){
        int pitch;
        int speed;
        int  volume = 140;
        int duration = 0;

        pitch = 90;
        speed = 90;

        String filename = (context.getFilesDir().getAbsolutePath()+"/play.wav");

        int file_rtn = 0;
        try
        {
            HIKARI.SetPitchSpeedVolumePause(pitch, speed, volume, 180);
            file_rtn = HIKARI.TextToFile(HIKARI.VT_FILE_API_FMT_S16PCM_WAVE, speech, filename, -1, -1, -1, -1, -1, -1);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(file_rtn == 1)
        {
            try
            {
                player.stop();
                player.reset();
                player.setDataSource(filename);
                player.prepare();
                player.start();
                duration = player.getDuration();
//                if(onSpeechLengthListener != null){
//                    onSpeechLengthListener.onSpeechLength(duration);
//                }
                setState(TTSState.SPEAKING);

                Class audioSystemClass = Class.forName("android.media.AudioSystem");

                Method setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
                // First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to the default
                // behavior, use FORCE_NONE (0).
                setForceUse.invoke(null, 1, 1);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        player.setOnCompletionListener(mp -> {
            setState(TTSState.IDLE);
            completableSubject.onComplete();
//                audioManager.setMode(AudioManager.MODE_NORMAL);


        });
        return duration;
    }

    @Override
    public void stopSpeaking(boolean isListenerCalled) {
        if(ttsStateBehaviorSubject.getValue() == TTSState.SPEAKING) {
            player.stop();
            setState(TTSState.IDLE);
            if (isListenerCalled) {
                completableSubject.onComplete();
            } else {
                completableSubject.onError(new Throwable());//TODO send a proper error
            }
        }
    }

    private void setState(TTSState newState){
        ttsStateBehaviorSubject.onNext(newState);
    }


    private void loadDB(){
        int license_rtn_jp = 0;

        final byte[] license_jp =  new byte[2048];

        try
        {
            //license_rtn =getAssets().open("verification.txt").read(license);
            license_rtn_jp = context.getResources().openRawResource(R.raw.verification_jp).read(license_jp);

        }
        catch(Exception ex)
        {
            license_rtn_jp = -1;

        }
        if( license_rtn_jp == -1 )
        {
            // debug_text.setText("verification.txt load fail");
            //アラート
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
            alertDialog.setTitle("ライセンスファイルを確認してください");
            alertDialog.setMessage("/res/raw/verification.txt");
            alertDialog.setPositiveButton("OK", (dialog, whichButton) -> ((Activity)context).setResult(Activity.RESULT_OK));
            alertDialog.create();
            alertDialog.show();
        }

        // TODO Auto-generated method stub
        int load_rtn_jp = 0;


        String engine_version_jp = "";
        String db_path = "/system/vendor/micro_h16/";
        try
        {
            String db_path1 = "/system/vendor/micro_h16";


            db_path = "/sdcard/micro_h16/";
            engine_version_jp = HIKARI.GetVersion();
            load_rtn_jp = HIKARI.LOADTTS(db_path, license_jp);


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        if(load_rtn_jp != 0)
        {
            //アラート
            Log.e("Hoya","error cannot load database");
        }
    }
}
