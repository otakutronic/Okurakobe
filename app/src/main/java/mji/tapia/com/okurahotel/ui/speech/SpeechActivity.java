package mji.tapia.com.okurahotel.ui.speech;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Completable;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.service.stt.STTService;

/**
 * Created by Sami on 9/21/2017.
 */

public class SpeechActivity extends BaseActivity implements SpeechContract.View{

    @Inject
    SpeechPresenter presenter;

    @BindView(R.id.example)
    TextView example_tv;

    @BindView(R.id.listening_icon)
    ImageView listeningIcon;

    @BindView(R.id.speech_text)
    TextView speech_tv;

    @BindView(R.id.speech_screen)
    View speechScreen;

    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;

    private Unbinder unbinder;

    private float lastIconScale = 0.6f;

    private boolean isProcessing = true;

    private JSONObject jsonProcessing;
    private JSONObject jsonListening;
    private JSONObject jsonIdle;

    private JSONObject curAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);
        unbinder = ButterKnife.bind(this);

        speechScreen.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                presenter.back();
                return true;
            }
            return false;
        });


        String jsonProcessingString = loadJSONFromAsset("lottie/processing.json");
        String jsonListeningString = loadJSONFromAsset("lottie/voice_recon.json");
        String jsonIdleString = loadJSONFromAsset("lottie/standby.json");

        try {
            jsonProcessing = new JSONObject(jsonProcessingString);
            jsonListening = new JSONObject(jsonListeningString);
            jsonIdle = new JSONObject(jsonIdleString);

            lottieAnimationView.setAnimation(jsonIdle);

            lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

//                    if(isProcessing) {
//                        isProcessing = false;
//                            lottieAnimationView.setAnimation(jsonVoiceReconObject);
//                            lottieAnimationView.playAnimation();
//
//                    }
//                    else {
//                        isProcessing = true;
//                            lottieAnimationView.setAnimation(jsonProcessingObject);
//                            lottieAnimationView.playAnimation();
//
//                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    lottieAnimationView.setAnimation(curAnimation);
                }
            });
            lottieAnimationView.loop(true);
            lottieAnimationView.playAnimation();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lottieAnimationView.cancelAnimation();
        unbinder.unbind();
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    public void updateAnimation(STTService.STTState sttState) {
        switch (sttState) {
            case IDLE:
                curAnimation = jsonIdle;
                break;
            case LISTENING:
                curAnimation = jsonListening;
                break;
            case PROCESSING:
                curAnimation = jsonProcessing;
                break;
        }
    }

    @Override
    public void updateIconSize(Integer level) {
        runOnUiThread(() -> {
            float initFloat = listeningIcon.getScaleX();
            float endFLoat = initFloat + ((float)level)/100.f;
            Animation anim = new ScaleAnimation(
                    lastIconScale, endFLoat, // Start and end values for the X axis scaling
                    lastIconScale, endFLoat, // Start and end values for the Y axis scaling
                    Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                    Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
            anim.setFillAfter(true); // Needed to keep the result of the animation
            anim.setDuration(200);
            lastIconScale = endFLoat;
            listeningIcon.startAnimation(anim);
        });
    }

    @Override
    public void updateExampleText() {
        runOnUiThread(() -> {
            String[] commands = getResources().getStringArray(R.array.command_example);
            Random random = new Random();
            String newCommand = commands[random.nextInt(commands.length)];


            showText(example_tv,newCommand,500,false);
        });
    }

    @Override
    public void updateSpeechText(String string) {
        runOnUiThread(() -> {
            showText(speech_tv,string,300,true);
        });
    }

    void showText(TextView textView, String text, final int time, boolean fast){
        final Animation fadeout;
        final Animation fadein;
        if(fast){
            fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in_fast);
            fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_out_fast);
        }
        else {
            fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
            fadeout = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        }


        if(textView.getVisibility() == View.VISIBLE) {
            textView.startAnimation(fadeout);
            fadeout.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    textView.setText(text);
                    Completable.timer(time, TimeUnit.MILLISECONDS).subscribe(() -> {
                        runOnUiThread(() -> {
                            textView.setVisibility(View.VISIBLE);
                            textView.startAnimation(fadein);
                        });
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        else {
            textView.setText(text);
            Completable.timer(time, TimeUnit.MILLISECONDS).subscribe(() -> {
                runOnUiThread(() -> {
                    textView.setVisibility(View.VISIBLE);
                    textView.startAnimation(fadein);
                });
            });
        }
    }


    private String loadJSONFromAsset(String path) {
        String json = null;
        try {
            InputStream is = getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

