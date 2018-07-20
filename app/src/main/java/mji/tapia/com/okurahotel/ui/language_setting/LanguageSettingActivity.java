package mji.tapia.com.okurahotel.ui.language_setting;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import mji.tapia.com.okurahotel.BaseActivity;
import mji.tapia.com.okurahotel.R;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.ui.splash.SplashActivity;
import mji.tapia.com.okurahotel.widget.PickerView;
import mji.tapia.com.service.language.LanguageManager;
import mji.tapia.com.service.util.LocaleManager;

import static mji.tapia.com.service.util.LocaleManager.LANGUAGE_JAPANESE;

/**
 * Created by Sami on 1/25/2018.
 */

public class LanguageSettingActivity extends BaseActivity implements LanguageSettingContract.View {

    @Inject
    LanguageSettingPresenter presenter;

    @BindView(R.id.ok)
    TextView ok_tv;

    @BindView(R.id.picker_view)
    PickerView pickerView;

    @BindView(R.id.settings_label)
    TextView title_tv;

    HashMap<Integer, LanguageManager.Language> languageIntegerHashMap = new HashMap<>();

    private int langIndex = 0;

    @Override
    protected void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected ScopedPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        ButterKnife.bind(this);
        languageIntegerHashMap.put(0, LanguageManager.Language.ENGLISH);
        languageIntegerHashMap.put(1, LanguageManager.Language.JAPANESE);
        languageIntegerHashMap.put(2, LanguageManager.Language.CHINESE_TW);
        languageIntegerHashMap.put(3, LanguageManager.Language.CHINESE_ZH);
        languageIntegerHashMap.put(4, LanguageManager.Language.KOREAN);
        languageIntegerHashMap.put(5, LanguageManager.Language.FRENCH);

        ok_tv.setOnClickListener(v -> onDoneSelected());
        pickerView.setSelectedItem(1,false);
        pickerView.setOnSelectedItemChangeListener(new PickerView.OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChange(int itemIndex, boolean fromUser) {
                if(itemIndex == 1) {
                    ok_tv.setEnabled(true);
                    ok_tv.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    ok_tv.setEnabled(false);
                    ok_tv.setTextColor(Color.parseColor("#aaFFFFFF"));
                }

                langIndex = itemIndex;

                //presenter.onLanguageChange(languageIntegerHashMap.get(itemIndex));
                title_tv.setText(R.string.home_language_setting_label);
            }

            @Override
            public void onSelectedItemFinalChange(int itemIndex, boolean fromUser) {

            }
        });
    }

    private void onDoneSelected() {
        String lang = languageIntegerHashMap.get(langIndex).toString();
        Log.e("TAG", "lang: " + LANGUAGE_JAPANESE);
        //setLocale(lang);
        setNewLocale(LANGUAGE_JAPANESE, false);
        //setNewLocale(LANGUAGE_JAPANESE, true);
        presenter.back();
    }

    private boolean setNewLocale(String language, boolean restartProcess) {
        LocaleManager.setNewLocale(this, language);

        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

        if (restartProcess) {
            System.exit(0);
        } else {
            Toast.makeText(this, "Activity restarted", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void setLocale(String lang) {
        lang = "ja";
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //Intent refresh = new Intent(this, LanguageSettingActivity.class);
        //startActivity(refresh);
        //finish();
    }
}
