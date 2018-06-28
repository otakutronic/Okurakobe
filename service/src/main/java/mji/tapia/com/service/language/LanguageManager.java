package mji.tapia.com.service.language;


import io.reactivex.Observable;

/**
 * Created by Sami on 1/26/2018.
 */

public interface LanguageManager {

    enum Language {
        JAPANESE,
        ENGLISH,
        CHINESE_TW,
        CHINESE_ZH,
        KOREAN,
        FRENCH,
        GERMAN
    }

    Language getCurrentLanguage();

    void setCurrentLanguage(Language language);

    Observable<Language> getLanguageChangeObservable();

    Language getDefaultLanguage();

    void setDefaultLanguage(Language language);

}
