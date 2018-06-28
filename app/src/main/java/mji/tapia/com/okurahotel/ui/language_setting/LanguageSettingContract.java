package mji.tapia.com.okurahotel.ui.language_setting;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;
import mji.tapia.com.service.language.LanguageManager;

/**
 * Created by Sami on 1/25/2018.
 */

public interface LanguageSettingContract {

    interface View extends BaseView {

    }

    interface Presenter extends ScopedPresenter {
        void onLanguageChange(LanguageManager.Language language);
    }
}
