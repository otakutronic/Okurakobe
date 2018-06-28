package mji.tapia.com.okurahotel.dagger.application;


import mji.tapia.com.okurahotel.dagger.application.module.ApplicationModule;
import mji.tapia.com.okurahotel.dagger.application.module.DataModule;
import mji.tapia.com.okurahotel.dagger.application.module.MappersModule;
import mji.tapia.com.okurahotel.dagger.application.module.ServiceModule;
import mji.tapia.com.okurahotel.dagger.application.module.ThreadingModule;
import mji.tapia.com.okurahotel.dagger.application.module.UtilsModule;

public interface ApplicationComponentExposes extends ApplicationModule.Exposes,
                                                     ThreadingModule.Exposes,
                                                     UtilsModule.Exposes,
                                                     DataModule.Exposes,
                                                     MappersModule.Exposes,
                                                     ServiceModule.Exposes {

}
