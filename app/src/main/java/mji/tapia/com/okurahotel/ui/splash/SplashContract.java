package mji.tapia.com.okurahotel.ui.splash;

import mji.tapia.com.okurahotel.BaseView;
import mji.tapia.com.okurahotel.ScopedPresenter;

/**
 * Created by Sami on 9/22/2017.
 */

public interface SplashContract {
    interface View extends BaseView {

        void stopLoadingAnimation();

    }

    interface Presenter extends ScopedPresenter {
        void userStart();
    }
}
