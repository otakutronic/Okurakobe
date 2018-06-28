package mji.tapia.com.okurahotel.ui.home;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.alarm.AlarmCallManager;
import mji.tapia.com.service.iot.alarm.IoTAlarmManager;
import mji.tapia.com.service.iot.room_state.IoTRoomStateManager;
import mji.tapia.com.service.wake_up.WakeUpService;

/**
 * Created by Sami on 9/21/2017.
 * Home presenter
 */

public class HomePresenter extends BasePresenter<HomeContract.View> implements HomeContract.Presenter {

    @Inject
    AlarmCallManager alarmManager;

    @Inject
    IoTRoomStateManager roomStateManager;

    @Inject
    WakeUpService wakeUpService;

    private boolean doLaundry = false;

    private boolean doCleaning = false;

    private boolean doNotDisturb = false;

    public HomePresenter(HomeContract.View view) {
        super(view);
    }

    private Disposable doLaundryDisposable;

    private Disposable doNotDisturbDisposable;

    private Disposable doCleaningDisposable;

    private Disposable wakeWordDisposable;

    private Disposable alarmStateDisposable;

    private Completable completableTimer = Completable.timer(15000, TimeUnit.MILLISECONDS);

    private Disposable disposableTimer;

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void activate() {
        super.activate();
        doNotDisturbDisposable = roomStateManager.getDoNotDisturbStateStateObservable().subscribe(aBoolean -> {
            doIfViewNotNull(view -> view.setDoNotDisturbTextEnable(aBoolean));
            doNotDisturb = aBoolean;
        });

        doLaundryDisposable = roomStateManager.getLaundryServiceStateObservable().subscribe(aBoolean -> {
            doIfViewNotNull(view -> view.setDoLaundryTextEnable(aBoolean));
            doLaundry = aBoolean;
        });

        doCleaningDisposable= roomStateManager.getMakeUpStateObservable().subscribe(aBoolean -> {
            doIfViewNotNull(view -> view.setCleanRoomTextEnable(aBoolean));
            doCleaning = aBoolean;
        });

        alarmStateDisposable = alarmManager.getAlarmStateObservable().subscribe(alarmState ->
                doIfViewNotNull(view -> view.setAlarmInfo(alarmState.enabled, alarmState.hours, alarmState.minutes)));

        wakeUpService.startService();
        wakeWordDisposable = wakeUpService.wakeWordDetected().subscribe(wakeWord -> router.navigateToSpeechScreen());

        disposableTimer = completableTimer.subscribe(() -> router.navigateToSleepScreen());
    }

    @Override
    public void deactivate() {
        super.deactivate();
        if(doLaundryDisposable != null) {
            doLaundryDisposable.dispose();
        }
        if(doCleaningDisposable != null) {
            doCleaningDisposable.dispose();
        }
        if(doNotDisturbDisposable != null) {
            doNotDisturbDisposable.dispose();
        }

        if(wakeWordDisposable != null) {
            wakeWordDisposable.dispose();
        }

        if(disposableTimer != null) {
            disposableTimer.dispose();
        }

        if(alarmStateDisposable != null) {
            alarmStateDisposable.dispose();
        }

        wakeUpService.stopService();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {

    }

    @Override
    public void selectAlarm() {
        router.navigateToAlarmScreen();
    }

    @Override
    public void selectRoomManager() {
        router.navigateToRoomManagerScreen();
    }

    @Override
    public void selectLanguageSetting() {
        router.navigateToLanguageScreen();
    }

    @Override
    public void selectSleepMode() {
        router.navigateToSleepScreen();
    }

    @Override
    public void selectStaffMode() {
        router.navigateToStaffLockScreen();
    }

    @Override
    public void selectAlarmState() {
        alarmManager.silenceAlarm();
    }

    @Override
    public void toggleCleanRoom() {
        roomStateManager.setMakeUpState(!doCleaning);
        doIfViewNotNull(view -> view.setCleanRoomTextEnable(!doCleaning));
    }

    @Override
    public void toggleDoNotDisturb() {
        roomStateManager.setDoNotDisturbState(!doNotDisturb);
        doIfViewNotNull(view -> view.setDoNotDisturbTextEnable(!doNotDisturb));
    }

    @Override
    public void toggleDoLaundry() {
        roomStateManager.setLaundryServiceState(!doLaundry);
        doIfViewNotNull(view -> view.setDoLaundryTextEnable(!doLaundry));
    }

    @Override
    public void onUserActivity() {
        disposableTimer.dispose();
        disposableTimer = completableTimer.retry().subscribe(() -> router.navigateToSleepScreen());
    }
}
