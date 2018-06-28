package mji.tapia.com.okurahotel.dagger;


import mji.tapia.com.okurahotel.OkuraHotelApplication;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.dagger.activity.DaggerActivity;
import mji.tapia.com.okurahotel.dagger.application.ApplicationComponent;
import mji.tapia.com.okurahotel.dagger.fragment.DaggerFragment;
import mji.tapia.com.okurahotel.dagger.fragment.FragmentComponent;

public final class ComponentFactory {

    private ComponentFactory() {
    }

    public static ApplicationComponent createApplicationComponent(final OkuraHotelApplication okuraHotelApplication) {
        return ApplicationComponent.Initializer.init(okuraHotelApplication);
    }

    public static ActivityComponent createActivityComponent(final DaggerActivity daggerActivity, final OkuraHotelApplication okuraHotelApplication) {
        return ActivityComponent.Initializer.init(daggerActivity, okuraHotelApplication.getApplicationComponent());
    }

    public static FragmentComponent createFragmentComponent(final DaggerFragment daggerFragment, final ActivityComponent activityComponent) {
        return FragmentComponent.Initializer.init(daggerFragment, activityComponent);
    }
}
