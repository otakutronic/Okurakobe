package mji.tapia.com.okurahotel.dagger.application.module;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;
import mji.tapia.com.okurahotel.dagger.application.ForApplication;
import mji.tapia.com.service.battery.BatteryManager;
import mji.tapia.com.service.battery.BatteryManagerImpl;
import mji.tapia.com.service.connectivity.ConnectivityManagerWrapper;
import mji.tapia.com.service.connectivity.ConnectivityManagerWrapperImpl;
import mji.tapia.com.service.connectivity.ConnectivityReceiver;
import mji.tapia.com.service.connectivity.ConnectivityReceiverImpl;
import mji.tapia.com.service.connectivity.NetworkUtils;
import mji.tapia.com.service.error_manager.ErrorManager;
import mji.tapia.com.service.error_manager.ErrorManagerImpl;
import mji.tapia.com.service.iot.IoTService;
import mji.tapia.com.service.iot.IoTServiceImpl;
import mji.tapia.com.service.iot.air_con.IoTAirConManager;
import mji.tapia.com.service.iot.alarm.IoTAlarmManager;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryServiceImpl;
import mji.tapia.com.service.iot.bluetooth.BluetoothSerialService;
import mji.tapia.com.service.iot.bluetooth.BluetoothSerialServiceImpl;
import mji.tapia.com.service.iot.curtain.IoTCurtainManager;
import mji.tapia.com.service.iot.light.IoTLightManager;
import mji.tapia.com.service.iot.room_state.IoTRoomStateManager;
import mji.tapia.com.service.language.LanguageManager;
import mji.tapia.com.service.language.LanguageManagerImpl;
import mji.tapia.com.service.stt.NuanceService;
import mji.tapia.com.service.stt.STTService;
import mji.tapia.com.service.tts.HoyaService;
import mji.tapia.com.service.tts.TTSService;
import mji.tapia.com.service.util.PreferenceUtils;
import mji.tapia.com.service.wake_up.FuetrekLocalService;
import mji.tapia.com.service.wake_up.WakeUpService;
import mji.tapia.com.service.audio.TapiaAudioManager;
import mji.tapia.com.service.audio.TapiaAudioManagerImpl;
import mji.tapia.com.service.alarm.AlarmCallManager;
import mji.tapia.com.service.alarm.AlarmCallManagerImpl;
import mji.tapia.com.service.room_manager.RoomManager;
import mji.tapia.com.service.room_manager.RoomManagerImpl;

@Module
public final class ServiceModule {

    @Provides
    @Singleton
    TTSService provideTTSService(final @ForApplication Context context) {
        return  new HoyaService(context);
    }

    @Provides
    @Singleton
    STTService provideSTTService(final @ForApplication Context context, LanguageManager languageManager) {
        return  new NuanceService(context, languageManager);
    }

    @Provides
    @Singleton
    WakeUpService provideWakeUpService(final @ForApplication Context context) {
        return  new FuetrekLocalService(context);
    }

    @Provides
    @Singleton
    BluetoothSerialService provideBluetoothSerialService() {
        return  new BluetoothSerialServiceImpl();
    }

    @Provides
    @Singleton
    BluetoothDiscoveryService provideBluetoothDiscoveryService(final @ForApplication Context context, final PreferenceUtils preferenceUtils) {
        return  new BluetoothDiscoveryServiceImpl(context, preferenceUtils, BluetoothAdapter.getDefaultAdapter());
    }

    @Provides
    @Singleton
    IoTService provideIoTService(final @ForApplication Context context, final PreferenceUtils preferenceUtils, final BluetoothDiscoveryService bluetoothDiscoveryService, final BluetoothSerialService bluetoothSerialService) {
        return  new IoTServiceImpl(bluetoothDiscoveryService, bluetoothSerialService);
    }

    @Provides
    @Singleton
    LanguageManager provideLanguageManager(final  @ForApplication Context context,  final PreferenceUtils preferenceUtils) {
        return new LanguageManagerImpl(context, preferenceUtils);
    }

    @Provides
    @Singleton
    ConnectivityManagerWrapper provideConnectivityManagerWrapper(final @ForApplication Context context) {
        return new ConnectivityManagerWrapperImpl(context);
    }

    @Provides
    @Singleton
    ConnectivityReceiver provideConnectivityReceiver(final @ForApplication Context context, final NetworkUtils networkUtils) {
        return new ConnectivityReceiverImpl(context, networkUtils, Schedulers.io());
    }

    @Provides
    @Singleton
    ErrorManager provideErrorManager() {
        return new ErrorManagerImpl();
    }

    @Provides
    @Singleton
    BatteryManager provideBatteryManager(@ForApplication Context context) {
        return new BatteryManagerImpl(context);
    }

    @Provides
    @Singleton
    IoTAlarmManager provideAlarmManager(IoTService ioTService) {
        return ioTService.getAlarmManager();
    }

    @Provides
    @Singleton
    IoTAirConManager provideAirConManager(IoTService ioTService) {
        return ioTService.getAirConManager();
    }

    @Provides
    @Singleton
    IoTCurtainManager provideCurtainManager(IoTService ioTService) {
        return ioTService.getCurtainManager();
    }

    @Provides
    @Singleton
    IoTLightManager provideLightManager(IoTService ioTService) {
        return ioTService.getLightManager();
    }

    @Provides
    @Singleton
    IoTRoomStateManager provideRoomStateManager(IoTService ioTService) {
        return ioTService.getRoomManager();
    }

    @Provides
    @Singleton
    TapiaAudioManager provideAudioManager(final @ForApplication Context context, final PreferenceUtils preferenceUtils) {
        return new TapiaAudioManagerImpl(context, preferenceUtils);
    }

    @Provides
    @Singleton
    AlarmCallManager provideAlarmCallManager(final @ForApplication Context context, final PreferenceUtils preferenceUtils) {
        return new AlarmCallManagerImpl(context, preferenceUtils);
    }

    @Provides
    @Singleton
    RoomManager provideRoomManager(final PreferenceUtils preferenceUtils) {
        return new RoomManagerImpl(preferenceUtils);
    }

    public interface Exposes {

        ConnectivityManagerWrapper connectivityManagerWrapper();

        ConnectivityReceiver connectivityReceiver();

        IoTService iotService();

        AlarmCallManager alarmCallManager();

        TapiaAudioManager tapiaAudioManager();

        RoomManager roomManager();

        IoTAirConManager iotAirConManager();

        IoTRoomStateManager iotRoomStateManager();

        IoTLightManager iotLightManager();

        IoTAlarmManager iotAlarmManager();

        IoTCurtainManager iotCurtainManager();

        BluetoothSerialService bluetoothSerialService();

        BluetoothDiscoveryService bluetoothDiscoveryService();

        TTSService ttsService();

        STTService sttService();

        WakeUpService wakeUpService();

        LanguageManager languageManager();

    }
}
