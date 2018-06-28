package mji.tapia.com.service.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.CancellationException;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.SingleSubject;
import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 3/30/2018.
 *
 */

public class TapiaAudioManagerImpl implements TapiaAudioManager {

    static private final String AUDIO_PREFERENCE = "AUDIO_PREFERENCE";
    static private final String ID = "AUDIO_REF";
    static private final int DEFAULT_VOLUME = 10;

    static private TapiaAudioManager instance;

    enum SourceType {
        ASSET,
        STORAGE
    }

    private Context context;

    private MediaPlayer seMediaPlayer;
    private MediaPlayer voiceMediaPlayer;
    private MediaPlayer bgmMediaPlayer;

    private WeakReference<AudioSession> curSESession;
    private WeakReference<AudioSession> curVoiceSession;
    private WeakReference<AudioSession> curBGMSession;

    private SharedPreferences audioPreferences;
    private AudioManager audioManager;

    public TapiaAudioManagerImpl(Context context, PreferenceUtils sharedPreferenceManager) {
        seMediaPlayer = new MediaPlayer();
        bgmMediaPlayer = new MediaPlayer();
        bgmMediaPlayer.setVolume(0.3f,0.3f);
        voiceMediaPlayer = new MediaPlayer();
        this.audioPreferences = sharedPreferenceManager.getSharedPreference(AUDIO_PREFERENCE);
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void init() {
        int volume = audioPreferences.getInt(ID, DEFAULT_VOLUME);
        setInternalVolume(volume);
    }

    @Override
    public int getVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public int getDefaultVolume() {
        return audioPreferences.getInt(ID, DEFAULT_VOLUME);
    }

    @Override
    public int getMaxVolume() {
        return MAX_VOLUME;
    }

    @Override
    public void setVolume(int volume, boolean save) {
        volume = clampVolume(volume, 0, MAX_VOLUME);
        setInternalVolume(volume);

        if (save) {
            audioPreferences.edit().putInt(ID, volume).apply();
        }
    }

    public static int percentToVolume(int percentage) {
        return percentage = (int) ((TapiaAudioManager.MAX_VOLUME * .01) * percentage);
    }

    public static int volumeToPercent(int volume) {
        float percentage = (float) volume / TapiaAudioManager.MAX_VOLUME;
        percentage *= 100;
        return (int) percentage;
    }

    public static int clampVolume(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    private void setInternalVolume(int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    @Override
    public AudioSession createAudioSessionFromAsset(String filePath, AudioType audioType) {
        MediaPlayer mediaPlayer = getMediaPlayer(audioType);
        return new AudioSessionImpl(filePath, SourceType.ASSET, mediaPlayer, audioType);
    }

    @Override
    public AudioSession createAudioSessionFromStorage(String filePath, AudioType audioType) {
        MediaPlayer mediaPlayer = getMediaPlayer(audioType);
        return new AudioSessionImpl(filePath, SourceType.STORAGE, mediaPlayer,audioType);
    }

    private MediaPlayer getMediaPlayer(@NonNull AudioType audioType) {
        switch (audioType) {
            case BGM:
                return bgmMediaPlayer;
            case VOICE:
                return voiceMediaPlayer;

            case SOUND_EFFECT:
                return seMediaPlayer;
        }
        return bgmMediaPlayer;
    }

    private void loadFile(AudioSessionImpl audioSession, String file, SourceType sourceType) {
        try {

            AudioSessionImpl previousSession = null;
            switch (audioSession.audioType) {
                case SOUND_EFFECT:
                    previousSession = (AudioSessionImpl) getCurSession(AudioType.SOUND_EFFECT);
                    curSESession = new WeakReference<>(audioSession);
                    break;
                case VOICE:
                    previousSession = (AudioSessionImpl) getCurSession(AudioType.VOICE);
                    curVoiceSession = new WeakReference<>(audioSession);
                    break;
                case BGM:
                    previousSession = (AudioSessionImpl) getCurSession(AudioType.BGM);
                    curBGMSession = new WeakReference<>(audioSession);
                    break;
            }

            if (previousSession != null && !previousSession.finishSubject.hasComplete()) {
                previousSession.finishSubject.onError(new CancellationException());
            }

            switch (sourceType) {
                case ASSET:
                    AssetFileDescriptor afd = context.getAssets().openFd(file);
                    audioSession.mediaPlayer.stop();
                    audioSession.mediaPlayer.reset();
                    audioSession.mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    audioSession.mediaPlayer.prepareAsync();
                    afd.close();
                    break;
                case STORAGE:
                    audioSession.mediaPlayer.stop();
                    audioSession.mediaPlayer.reset();
                    audioSession.mediaPlayer.setDataSource(file);
                    audioSession.mediaPlayer.prepareAsync();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class AudioSessionImpl implements TapiaAudioManager.AudioSession{

        MediaPlayer mediaPlayer;

        CompletableSubject preparedSubject = CompletableSubject.create();

        private CompletableSubject startSubject = CompletableSubject.create();

        CompletableSubject finishSubject = CompletableSubject.create();

        SingleSubject<Integer> durationSubject = SingleSubject.create();

        BehaviorSubject<Boolean> focusSubject = BehaviorSubject.create();

        AudioType audioType;

        String file;

        SourceType sourceType;

        AudioSessionImpl(String file, SourceType sourceType,MediaPlayer mediaPlayer, AudioType audioType) {
            this.file = file;
            this.sourceType = sourceType;
            this.audioType = audioType;
            this.mediaPlayer = mediaPlayer;

            init();
        }

        private void init() {
            loadFile(this, file, sourceType);
            preparedSubject = CompletableSubject.create();
            startSubject = CompletableSubject.create();
            finishSubject = CompletableSubject.create();

            this.mediaPlayer.setOnCompletionListener(mp ->  {
                finishSubject.onComplete();
                Log.e("mp", "complete");
            });
            this.mediaPlayer.setOnPreparedListener(mp -> {
                Log.e("mp", "prepared");
                preparedSubject.onComplete();
                durationSubject.onSuccess(mediaPlayer.getDuration());
            });
            Completable.mergeArray(preparedSubject, startSubject)
                    .subscribe(() -> {
                        Log.e("mp", "started");
                        mediaPlayer.start();
                    });
        }

        @Override
        public AudioType getAudioType() {
            return audioType;
        }

        @Override
        public boolean isPrepared() {
            return preparedSubject.hasComplete();
        }

        @Override
        public Single<Integer> getDuration() {
            return durationSubject;
        }

        @Override
        public Completable play() {
            if (getCurSession(audioType) != null && getCurSession(audioType) == this) {
                if(startSubject.hasComplete()) {
                    init();
                    startSubject.onComplete();
                } else {
                    startSubject.onComplete();
                }
            } else {
                init();
                startSubject.onComplete();
            }

            return finishSubject;
        }

        @Override
        public void stop() {
            mediaPlayer.stop();
            Log.e("mp", "stoped");
        }


        int pausedPosition;
        @Override
        public void pause() {
            Log.e("mp", "paused");
            mediaPlayer.pause();
            pausedPosition = mediaPlayer.getCurrentPosition();
        }

        @Override
        public void resume() {
            Log.e("mp", "resume");
            mediaPlayer.seekTo(pausedPosition);
            mediaPlayer.start();
        }

        @Override
        public Completable restart() {
            init();
            startSubject.onComplete();
            return finishSubject;
        }
    }

    private AudioSession getCurSession(AudioType audioType) {
        AudioSession curAudioSession = null;
        switch (audioType) {
            case BGM:
                curAudioSession = curBGMSession == null?null:curBGMSession.get();
                break;
            case VOICE:
                curAudioSession = curVoiceSession == null?null:curVoiceSession.get();
                break;
            case SOUND_EFFECT:
                curAudioSession = curSESession == null?null:curSESession.get();
                break;
        }
        return curAudioSession;
    }
}
