package mji.tapia.com.okurahotel.dagger.activity;

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

public interface ActivityComponentInjects {

    void inject(SplashActivity splashActivity);
    void inject(SplashPresenter splashPresenter);

    void inject(HomeActivity homeActivity);
    void inject(HomePresenter homePresenter);

    void inject(SleepActivity sleepActivity);
    void inject(SleepPresenter sleepPresenter);

    void inject(RoomManagerActivity roomManagerActivity);
    void inject(RoomManagerPresenter roomManagerPresenter);

    void inject(CurtainActivity curtainActivity);
    void inject(CurtainPresenter curtainPresenter);

    void inject(LightActivity lightActivity);
    void inject(LightPresenter lightPresenter);

    void inject(StaffModeActivity staffModeActivity);
    void inject(StaffModePresenter staffModePresenter);

    void inject(SpeechActivity speechActivity);
    void inject(SpeechPresenter speechPresenter);

    void inject(LanguageSettingActivity languageSettingActivity);
    void inject(LanguageSettingPresenter languageSettingPresenter);

    void inject(AlarmActivity alarmActivity);
    void inject(AlarmPresenter alarmPresenter);

    void inject(LockActivity lockActivity);
    void inject(LockPresenter lockPresenter);

    void inject(AirConActivity airConActivity);
    void inject(AirConPresenter airConPresenter);

    void inject(LogActivity logActivity);
    void inject(LogPresenter logPresenter);

}
