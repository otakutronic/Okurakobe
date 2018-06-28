package mji.tapia.com.okurahotel.dagger.fragment;

import dagger.Component;
import mji.tapia.com.okurahotel.dagger.activity.ActivityComponent;
import mji.tapia.com.okurahotel.dagger.fragment.module.FragmentModule;
import mji.tapia.com.okurahotel.dagger.fragment.module.FragmentPresenterModule;


@FragmentScope
@Component(
        dependencies = ActivityComponent.class,
        modules = {
                FragmentModule.class,
                FragmentPresenterModule.class
        }
)
public interface FragmentComponent extends FragmentComponentInjects, FragmentComponentExposes {

    final class Initializer {

        static public FragmentComponent init(final DaggerFragment fragment, final ActivityComponent activityComponent) {
            return DaggerFragmentComponent.builder()
                                          .activityComponent(activityComponent)
                                          .fragmentModule(new FragmentModule(fragment))
                                          .fragmentPresenterModule(new FragmentPresenterModule(fragment))
                                          .build();
        }

        private Initializer() {
        }
    }
}
