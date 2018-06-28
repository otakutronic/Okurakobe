package mji.tapia.com.okurahotel.dagger.activity;

import dagger.Component;
import mji.tapia.com.okurahotel.dagger.activity.module.ActivityModule;
import mji.tapia.com.okurahotel.dagger.application.ApplicationComponent;

@ActivityScope
@Component(
        dependencies = ApplicationComponent.class,
        modules = {
                ActivityModule.class
        }
)
public interface ActivityComponent extends ActivityComponentInjects, ActivityComponentExposes {

    final class Initializer {

        static public ActivityComponent init(final DaggerActivity daggerActivity, final ApplicationComponent applicationComponent) {
            return DaggerActivityComponent.builder()
                                          .applicationComponent(applicationComponent)
                                          .activityModule(new ActivityModule(daggerActivity))
                                          .build();
        }

        private Initializer() {
        }
    }
}
