package mji.tapia.com.okurahotel.dagger.application;

import javax.inject.Singleton;

import dagger.Component;

import mji.tapia.com.okurahotel.OkuraHotelApplication;
import mji.tapia.com.okurahotel.dagger.application.module.ApplicationModule;
import mji.tapia.com.okurahotel.dagger.application.module.DataModule;
import mji.tapia.com.okurahotel.dagger.application.module.MappersModule;
import mji.tapia.com.okurahotel.dagger.application.module.ServiceModule;
import mji.tapia.com.okurahotel.dagger.application.module.ThreadingModule;
import mji.tapia.com.okurahotel.dagger.application.module.UtilsModule;


@Singleton
@Component(
        modules = {
                ApplicationModule.class,
                ThreadingModule.class,
                UtilsModule.class,
                DataModule.class,
                MappersModule.class,
                ServiceModule.class
        }
)

public interface ApplicationComponent extends ApplicationComponentInjects, ApplicationComponentExposes {

    final class Initializer {

        static public ApplicationComponent init(final OkuraHotelApplication okuraHotelApplication) {
            return DaggerApplicationComponent.builder()
                                             .applicationModule(new ApplicationModule(okuraHotelApplication))
                                             .threadingModule(new ThreadingModule())
                                             .utilsModule(new UtilsModule())
                                             .dataModule(new DataModule())
                                             .mappersModule(new MappersModule())
                                             .serviceModule(new ServiceModule())
                                             .build();
        }

        private Initializer() {
        }
    }
}
