package mji.tapia.com.okurahotel;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import mji.tapia.com.okurahotel.dagger.activity.ActivityScope;
import mji.tapia.com.okurahotel.ui.room_manager.air_con.AirConActivity;
import mji.tapia.com.okurahotel.ui.alarm.AlarmActivity;
import mji.tapia.com.okurahotel.ui.home.HomeActivity;
import mji.tapia.com.okurahotel.ui.language_setting.LanguageSettingActivity;
import mji.tapia.com.okurahotel.ui.room_manager.RoomManagerActivity;
import mji.tapia.com.okurahotel.ui.room_manager.curtain.CurtainActivity;
import mji.tapia.com.okurahotel.ui.room_manager.light.LightActivity;
import mji.tapia.com.okurahotel.ui.sleep.SleepActivity;
import mji.tapia.com.okurahotel.ui.speech.SpeechActivity;
import mji.tapia.com.okurahotel.ui.splash.SplashActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.StaffModeActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.lock.LockActivity;
import mji.tapia.com.okurahotel.ui.staff_mode.log.LogActivity;

@ActivityScope
public final class RouterImpl implements Router {
    final public int PICK_IMAGE = 0;

    private static final int LAST_FRAGMENT = 0;

    private final AppCompatActivity activity;
    private final FragmentManager fragmentManager;

    public RouterImpl(final AppCompatActivity activity, final FragmentManager fragmentManager) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void closeScreen() {
        if(activity instanceof RoomManagerActivity ||
                activity instanceof LightActivity ||
                activity instanceof CurtainActivity ||
                activity instanceof AirConActivity ||
                /*activity instanceof LightSettingActivity ||*/
                activity instanceof AlarmActivity ||
                activity instanceof LanguageSettingActivity){
            activity.supportFinishAfterTransition();
        } else {
            activity.finish();
        }
    }

    @Override
    public void goBack() {
        if (fragmentManager.getBackStackEntryCount() == LAST_FRAGMENT) {
            closeScreen();
        } else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void navigateToHomeScreen() {
        if(activity instanceof SplashActivity){
            Intent intent = new Intent(activity, HomeActivity.class);
            View ivLogo = activity.findViewById(R.id.logo);
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(activity, ivLogo, "okura_logo");
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, HomeActivity.class));
        }
    }

    @Override
    public void navigateToStaffLockScreen() {
        activity.startActivity(new Intent(activity, LockActivity.class));
    }

    @Override
    public void navigateToStaffModeScreen() {
        activity.startActivity(new Intent(activity, StaffModeActivity.class));
    }

    @Override
    public void navigateToRoomManagerScreen() {
        if(activity instanceof HomeActivity){
            Intent intent = new Intent(activity, RoomManagerActivity.class);
            View ivRoomManager = activity.findViewById(R.id.room_manager_icon);
            View tvRoomManager = activity.findViewById(R.id.room_manager_label);
            Pair<View, String> p1 = Pair.create(ivRoomManager, "room_manager_icon");
            Pair<View, String> p2 = Pair.create(tvRoomManager, "room_manager_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, RoomManagerActivity.class));
        }
    }

    @Override
    public void navigateToCurtainScreen() {
        if(activity instanceof RoomManagerActivity){
            Intent intent = new Intent(activity, CurtainActivity.class);
            View ivCurtain = activity.findViewById(R.id.curtains_icon);
            View tvCurtain = activity.findViewById(R.id.curtains_label);
            Pair<View, String> p1 = Pair.create(ivCurtain, "curtains_icon");
            Pair<View, String> p2 = Pair.create(tvCurtain, "curtains_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, CurtainActivity.class));
        }
    }

    @Override
    public void navigateToAirConScreen() {
        if(activity instanceof RoomManagerActivity){
            Intent intent = new Intent(activity, AirConActivity.class);
            View ivCurtain = activity.findViewById(R.id.ac_icon);
            View tvCurtain = activity.findViewById(R.id.ac_label);
            Pair<View, String> p1 = Pair.create(ivCurtain, "ac_icon");
            Pair<View, String> p2 = Pair.create(tvCurtain, "ac_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, AirConActivity.class));
        }

    }

    @Override
    public void navigateToLightScreen() {
        if(activity instanceof RoomManagerActivity){
            Intent intent = new Intent(activity, LightActivity.class);
            View ivLight = activity.findViewById(R.id.light_icon);
            View tvLight = activity.findViewById(R.id.light_label);
            Pair<View, String> p1 = Pair.create(ivLight, "light_icon");
            Pair<View, String> p2 = Pair.create(tvLight, "light_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, LightActivity.class));

        }
    }

    @Override
    public void navigateToLightSettingScreen() {
        if(activity instanceof LightActivity){

        }
        else {
            activity.startActivity(new Intent(activity, LightActivity.class));

        }
    }

    @Override
    public void navigateToSleepScreen() {
        activity.startActivity(new Intent(activity, SleepActivity.class));
    }

    @Override
    public void navigateToSpeechScreen() {
        activity.startActivity(new Intent(activity, SpeechActivity.class));
    }

    @Override
    public void navigateToLanguageScreen() {
        if(activity instanceof HomeActivity){
            Intent intent = new Intent(activity, LanguageSettingActivity.class);
            View ivLanguageSetting = activity.findViewById(R.id.settings_icon);
            View tvLanguageSetting = activity.findViewById(R.id.settings_label);
            Pair<View, String> p1 = Pair.create(ivLanguageSetting, "language_setting_icon");
            Pair<View, String> p2 = Pair.create(tvLanguageSetting, "language_setting_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, RoomManagerActivity.class));
        }
    }

    @Override
    public void navigateToAlarmScreen() {
        //activity.startActivity(new Intent(activity, AlarmActivity.class));
        if(activity instanceof HomeActivity){
            Intent intent = new Intent(activity, AlarmActivity.class);
            View ivLanguageSetting = activity.findViewById(R.id.alarm_icon);
            View tvLanguageSetting = activity.findViewById(R.id.alarm_label);
            Pair<View, String> p1 = Pair.create(ivLanguageSetting, "alarm_icon");
            Pair<View, String> p2 = Pair.create(tvLanguageSetting, "alarm_label");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
            activity.startActivity(intent, options.toBundle());
        }
        else {
            activity.startActivity(new Intent(activity, AlarmActivity.class));
        }
    }

    @Override
    public void navigateToErrorLogScreen() {
        activity.startActivity(new Intent(activity, LogActivity.class));
    }


    @Override
    public void closeApplication() {
        activity.finishAffinity();
    }

    private <T extends Fragment> void advanceToFragment(final String destinationFragmentTag, int fragment_location_id, final Function<Void,T> destinationFragmentFactory,
                                                        final Consumer<T> destinationFragmentExistsAction, boolean isRoot, boolean isOnBackStack) {

        T destinationFragment = (T) fragmentManager.findFragmentByTag(destinationFragmentTag);

        final Fragment sourceFragment= fragmentManager.findFragmentById(fragment_location_id);
//        sourceFragment = fragmentManager.findFragmentByTag(sourceFragmentTag);


        if(isRoot) {
            fragmentManager.popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        if (destinationFragment == null) {
            try {
                destinationFragment = destinationFragmentFactory.apply(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();
            if(!isRoot) {
                fragmentTransaction.setCustomAnimations(R.anim.fragment_right_enter, R.anim.fragment_left_exit, R.anim.fragment_left_enter, R.anim.fragment_right_exit);
            }
            if(isOnBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.hide(sourceFragment)
                    .add(fragment_location_id, destinationFragment, destinationFragmentTag)
                    .commit();
        } else {
            try {
                destinationFragmentExistsAction.accept(destinationFragment);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentTransaction fragmentTransaction  = fragmentManager.beginTransaction();

            if(isOnBackStack) {
                fragmentTransaction.addToBackStack(null);
            }
            fragmentTransaction.hide(sourceFragment)
                    .show(destinationFragment)
                    .commit();
        }
    }
}
