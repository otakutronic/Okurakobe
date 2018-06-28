package mji.tapia.com.okurahotel.dagger.application.module;

import android.content.Context;
import android.content.res.Resources;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import mji.tapia.com.okurahotel.OkuraHotelApplication;
import mji.tapia.com.okurahotel.configuration.ViewActionQueueProvider;
import mji.tapia.com.okurahotel.configuration.ViewIdGenerator;
import mji.tapia.com.okurahotel.dagger.application.ForApplication;


@Module
public final class ApplicationModule {

    private final OkuraHotelApplication okuraHotelApplication;

    public ApplicationModule(final OkuraHotelApplication okuraHotelApplication) {
        this.okuraHotelApplication = okuraHotelApplication;
    }

    @Provides
    @Singleton
    OkuraHotelApplication provideOkuraHotelApplication() {
        return okuraHotelApplication;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideContext() {
        return okuraHotelApplication;
    }

    @Provides
    @Singleton
    ViewIdGenerator provideViewIdGenerator() {
        return new ViewIdGenerator();
    }

    @Provides
    @Singleton
    ViewActionQueueProvider provideViewActionQueueProvider(final @Named(ThreadingModule.MAIN_SCHEDULER) Scheduler mainScheduler) {
        return new ViewActionQueueProvider(mainScheduler);
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return okuraHotelApplication.getResources();
    }

    public interface Exposes {

        OkuraHotelApplication okuraHotelApplication();

        @ForApplication
        Context context();

        ViewIdGenerator viewIdGenerator();

        ViewActionQueueProvider viewActionQueueProvider();

        Resources resources();
    }
}
