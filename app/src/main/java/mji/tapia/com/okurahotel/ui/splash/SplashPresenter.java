package mji.tapia.com.okurahotel.ui.splash;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import mji.tapia.com.okurahotel.BasePresenter;

/**
 * Created by Sami on 9/21/2017.
 */

public class SplashPresenter extends BasePresenter<SplashContract.View> implements SplashContract.Presenter {

    private boolean initFinish = false;


    public SplashPresenter(SplashContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        initFinish = false;
        viewActionQueue.subscribeTo(Completable.timer(2000, TimeUnit.MILLISECONDS),view -> {
            initFinish = true;
            view.stopLoadingAnimation();
        }, Throwable::printStackTrace);
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {

    }

    @Override
    public void userStart() {
        if(initFinish) router.navigateToHomeScreen();
    }
}
