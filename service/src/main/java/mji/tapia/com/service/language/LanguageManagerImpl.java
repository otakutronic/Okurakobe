package mji.tapia.com.service.language;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import io.reactivex.Observable;
import mji.tapia.com.service.util.LocaleManager;
import mji.tapia.com.service.util.PreferenceUtils;
import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.widget.Toast;

/**
 * Created by Sami on 1/26/2018.
 */

public class LanguageManagerImpl implements LanguageManager {

    private Language currentLanguage;

    private Context context;

    private PreferenceUtils preferenceUtils;

    public LanguageManagerImpl(Context applicationContext, PreferenceUtils preferenceUtils) {
        this.preferenceUtils = preferenceUtils;
        context = applicationContext;
    }

    @Override
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void setCurrentLanguage(Language language) {
        currentLanguage = language;
        Configuration configuration = context.getResources().getConfiguration();
        switch (language) {
            case KOREAN:
                Locale.setDefault(Locale.KOREAN);
                configuration.setLocale(Locale.KOREAN);
                break;
            case ENGLISH:
                Locale.setDefault(Locale.ENGLISH);
                configuration.setLocale(Locale.ENGLISH);
                break;
            case JAPANESE:
                Locale.setDefault(Locale.JAPANESE);
                configuration.setLocale(Locale.JAPANESE);
                break;
            case FRENCH:
                Locale.setDefault(Locale.FRENCH);
                configuration.setLocale(Locale.FRENCH);
                break;
            case GERMAN:
                Locale.setDefault(Locale.GERMAN);
                configuration.setLocale(Locale.GERMAN);
                break;
            case CHINESE_TW:
                Locale.setDefault(Locale.TRADITIONAL_CHINESE);
                configuration.setLocale(Locale.TRADITIONAL_CHINESE);
                break;
            case CHINESE_ZH:
                Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
        }
        context.getResources().updateConfiguration(configuration, null);
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Configuration configuration = context.getResources().getConfiguration();
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        //Intent refresh = new Intent(this, AndroidLocalize.class);
        //startActivity(refresh);
        //finish();

    }


    @Override
    public Observable<Language> getLanguageChangeObservable() {
        return null;
    }

    @Override
    public Language getDefaultLanguage() {
        return Language.JAPANESE;
    }

    @Override
    public void setDefaultLanguage(Language language) {

    }
}
