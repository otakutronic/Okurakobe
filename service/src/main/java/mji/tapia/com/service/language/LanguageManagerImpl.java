package mji.tapia.com.service.language;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

import io.reactivex.Observable;
import mji.tapia.com.service.util.PreferenceUtils;

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

    @Override
    public Observable<Language> getLanguageChangeObservable() {
        return null;
    }

    @Override
    public Language getDefaultLanguage() {
        return null;
    }

    @Override
    public void setDefaultLanguage(Language language) {

    }
}
