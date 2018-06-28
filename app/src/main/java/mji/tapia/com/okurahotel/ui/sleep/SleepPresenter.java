package mji.tapia.com.okurahotel.ui.sleep;

import java.util.Date;
import java.util.concurrent.TimeUnit;


import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.wake_up.WakeUpService;

/**
 * Created by Sami on 9/21/2017.
 */

public class SleepPresenter extends BasePresenter<SleepContract.View> implements SleepContract.Presenter {


    @Inject
    WakeUpService wakeUpService;

    private Disposable wakeWordDisposable;


    public SleepPresenter(SleepContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();

    }

    @Override
    public void activate() {
        super.activate();

        viewActionQueue.subscribeTo(Observable.interval(0,1000,TimeUnit.MILLISECONDS)
                .map(aLong -> view -> {
                    view.updateClock(new Date());
                }), view -> {}, Throwable::printStackTrace);

        wakeUpService.startService();
        wakeWordDisposable = wakeUpService.wakeWordDetected().subscribe(wakeWord -> {
            router.navigateToSpeechScreen();
            router.closeScreen();
        });
    }

    @Override
    public void deactivate() {
        super.deactivate();

        wakeWordDisposable.dispose();
        wakeUpService.stopService();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {
        router.closeScreen();
    }

}
