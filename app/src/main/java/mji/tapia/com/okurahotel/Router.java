package mji.tapia.com.okurahotel;

import android.net.Uri;

import io.reactivex.Single;

public interface Router {


    void closeScreen();

    void goBack();

    void navigateToHomeScreen();

    void navigateToStaffLockScreen();

    void navigateToStaffModeScreen();

    void navigateToRoomManagerScreen();

    void navigateToCurtainScreen();

    void navigateToAirConScreen();

    void navigateToLightScreen();

    void navigateToLightSettingScreen();

    void navigateToSleepScreen();

    void navigateToSpeechScreen();

    void navigateToLanguageScreen();

    void navigateToAlarmScreen();

    void navigateToErrorLogScreen();

    void closeApplication();

}
