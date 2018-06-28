package mji.tapia.com.okurahotel.dagger.activity.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;

import dagger.Module;
import dagger.Provides;
import mji.tapia.com.okurahotel.Router;
import mji.tapia.com.okurahotel.RouterImpl;
import mji.tapia.com.okurahotel.dagger.activity.ActivityScope;
import mji.tapia.com.okurahotel.dagger.activity.DaggerActivity;
import mji.tapia.com.okurahotel.dagger.activity.ForActivity;
import mji.tapia.com.okurahotel.ui.room_manager.air_con.AirConActivity;
import mji.tapia.com.okurahotel.ui.room_manager.air_con.AirConPresenter;
import mji.tapia.com.okurahotel.ui.alarm.AlarmActivity;
import mji.tapia.com.okurahotel.ui.alarm.AlarmPresenter;
import mji.tapia.com.okurahotel.ui.home.HomeActivity;
import mji.tapia.com.okurahotel.ui.home.HomePresenter;
import mji.tapia.com.okurahotel.ui.language_setting.LanguageSettingActivity;
import mji.tapia.com.okurahotel.ui.language_setting.LanguageSettingPresenter;
import mji.tapia.com.okurahotel.ui.room_manager.RoomManagerActivity;
import mji.tapia.com.okurahotel.ui.room_manager.RoomManagerPresenter;
import mji.tapia.com.okurahotel.ui.room_manager.curtain.CurtainActivity;
import mji.tapia.com.okurahotel.ui.room_manager.curtain.CurtainPresenter;
import mji.tapia.com.okurahotel.ui.room_manager.light.LightActivity;
import mji.tapia.com.okurahotel.ui.room_manager.light.LightPresenter;
import mji.tapia.com.okurahotel.ui.sleep.SleepActivity;
import mji.tapia.com.okurahotel.ui.sleep.SleepPresenter;
import mji.tapia.com.okurahotel.ui.speech.SpeechActivity;
import mji.tapia.com.okurahotel.ui.speech.SpeechPresenter;
import mji.tapia.com.okurahotel.ui.splash.SplashActivity;
import mji.tapia.com.okurahotel.ui.splash.SplashPresenter;
import mji.tapia.com.okurahotel.ui.staff_mode.StaffModeActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.StaffModePresenter;
import mji.tapia.com.okurahotel.ui.staff_mode.lock.LockActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.lock.LockPresenter;
import mji.tapia.com.okurahotel.ui.staff_mode.log.LogActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.log.LogPresenter;


@Module
public class ActivityModule {

    private final DaggerActivity daggerActivity;

    public ActivityModule(final DaggerActivity daggerActivity) {
        this.daggerActivity = daggerActivity;
    }

    @Provides
    @ActivityScope
    @ForActivity
    Context provideActivityContext() {
        return daggerActivity;
    }

    @Provides
    @ActivityScope
    Activity provideActivity() {
        return daggerActivity;
    }

    @Provides
    @ActivityScope
    FragmentManager provideFragmentManager() {
        return daggerActivity.getSupportFragmentManager();
    }

    @Provides
    @ActivityScope
    Router provideRouter(final FragmentManager fragmentManager) {
        return new RouterImpl(daggerActivity, fragmentManager);
    }

    @Provides
    @ActivityScope
    SplashPresenter provideSplashPresenter() {
        final SplashPresenter splashPresenter = new SplashPresenter((SplashActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(splashPresenter);
        return splashPresenter;
    }

    @Provides
    @ActivityScope
    HomePresenter provideHomePresenter() {
        final HomePresenter homePresenter = new HomePresenter((HomeActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(homePresenter);
        return homePresenter;
    }

    @Provides
    @ActivityScope
    SleepPresenter provideSleepPresenter() {
        final SleepPresenter sleepPresenter = new SleepPresenter((SleepActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(sleepPresenter);
        return sleepPresenter;
    }

    @Provides
    @ActivityScope
    RoomManagerPresenter provideRoomManagerPresenter() {
        final RoomManagerPresenter roomManagerPresenter = new RoomManagerPresenter((RoomManagerActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(roomManagerPresenter);
        return roomManagerPresenter;
    }

    @Provides
    @ActivityScope
    LightPresenter provideLightPresenter() {
        final LightPresenter lightPresenter = new LightPresenter((LightActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(lightPresenter);
        return lightPresenter;
    }

    /*@Provides
    @ActivityScope
    LightSettingPresenter provideLightSettingPresenter() {
        final LightSettingPresenter lightSettingPresenter = new LightSettingPresenter((LightSettingActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(lightSettingPresenter);
        return lightSettingPresenter;
    }*/

    @Provides
    @ActivityScope
    CurtainPresenter provideCurtainPresenter() {
        final CurtainPresenter curtainPresenter = new CurtainPresenter((CurtainActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(curtainPresenter);
        return curtainPresenter;
    }

    @Provides
    @ActivityScope
    StaffModePresenter provideStaffModePresenter() {
        final StaffModePresenter staffModePresenter = new StaffModePresenter((StaffModeActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(staffModePresenter);
        return staffModePresenter;
    }

    @Provides
    @ActivityScope
    SpeechPresenter provideSpeechPresenter() {
        final SpeechPresenter speechPresenter = new SpeechPresenter((SpeechActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(speechPresenter);
        return speechPresenter;
    }

    @Provides
    @ActivityScope
    LanguageSettingPresenter provideLanguageSettingPresenter() {
        final LanguageSettingPresenter languageSettingPresenter = new LanguageSettingPresenter((LanguageSettingActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(languageSettingPresenter);
        return languageSettingPresenter;
    }

    @Provides
    @ActivityScope
    AlarmPresenter provideAlarmPresenter() {
        final AlarmPresenter alarmPresenter = new AlarmPresenter((AlarmActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(alarmPresenter);
        return alarmPresenter;
    }

    @Provides
    @ActivityScope
    LockPresenter provideLockPresenter() {
        final LockPresenter lockPresenter = new LockPresenter((LockActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(lockPresenter);
        return lockPresenter;
    }

    @Provides
    @ActivityScope
    AirConPresenter provideAirConPresenter() {
        final AirConPresenter airConPresenter = new AirConPresenter((AirConActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(airConPresenter);
        return airConPresenter;
    }

    @Provides
    @ActivityScope
    LogPresenter provideLogPresenter() {
        final LogPresenter logPresenter = new LogPresenter((LogActivity) daggerActivity);
        daggerActivity.getActivityComponent().inject(logPresenter);
        return logPresenter;
    }

    public interface Exposes {

        Activity activity();

        @ForActivity
        Context context();

        FragmentManager fragmentManager();

        Router router();
    }
}
