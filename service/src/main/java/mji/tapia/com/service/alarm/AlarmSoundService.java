package mji.tapia.com.service.alarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import mji.tapia.com.service.R;

/**
 * Created by andy on 10/04/18.
 */

public class AlarmSoundService extends Service {
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
    }
}
