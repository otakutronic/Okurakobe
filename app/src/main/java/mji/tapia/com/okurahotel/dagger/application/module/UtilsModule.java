package mji.tapia.com.okurahotel.dagger.application.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mji.tapia.com.service.util.PreferenceUtils;
import mji.tapia.com.service.util.PreferenceUtilsImpl;
import mji.tapia.com.okurahotel.dagger.application.ForApplication;
import mji.tapia.com.service.connectivity.ConnectivityManagerWrapper;
import mji.tapia.com.service.connectivity.NetworkUtils;
import mji.tapia.com.service.connectivity.NetworkUtilsImpl;

@Module
public final class UtilsModule {

    @Provides
    @Singleton
    NetworkUtils provideNetworkUtils(final ConnectivityManagerWrapper connectivityManagerWrapper) {
        return new NetworkUtilsImpl(connectivityManagerWrapper);
    }

    @Provides
    @Singleton
    PreferenceUtils providePreferenceUtils(final @ForApplication Context context) {
        return new PreferenceUtilsImpl(context);
    }

    public interface Exposes {

        NetworkUtils networkUtils();

        PreferenceUtils preferenceUtils();

    }
}
