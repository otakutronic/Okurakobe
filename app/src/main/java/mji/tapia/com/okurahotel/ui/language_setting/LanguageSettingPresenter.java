package mji.tapia.com.okurahotel.ui.language_setting;

import javax.inject.Inject;

import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.language.LanguageManager;

/**
 * Created by Sami on 1/25/2018.
 */

public class LanguageSettingPresenter extends BasePresenter<LanguageSettingContract.View> implements LanguageSettingContract.Presenter{

    @Inject
    LanguageManager languageManager;

    public LanguageSettingPresenter(LanguageSettingContract.View view) {
        super(view);
    }

    @Override
    public void back() {
        router.closeScreen();
    }

    @Override
    public void onLanguageChange(LanguageManager.Language language) {
        languageManager.setCurrentLanguage(language);
    }
}
