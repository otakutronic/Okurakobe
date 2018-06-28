package mji.tapia.com.okurahotel.dagger.application.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import mji.tapia.com.data.AppDatabase;
import mji.tapia.com.data.error_log.ErrorLogRepository;
import mji.tapia.com.data.error_log.ErrorLogRepositoryImpl;
import mji.tapia.com.data.voice_command.VoiceCommandParser;
import mji.tapia.com.data.voice_command.VoiceCommandRepository;
import mji.tapia.com.data.voice_command.VoiceCommandRepositoryImpl;
import mji.tapia.com.okurahotel.dagger.application.ForApplication;
import mji.tapia.com.service.error_manager.ErrorManager;

@Module
public final class DataModule {

    @Provides
    @Singleton
    VoiceCommandRepository provideVoiceCommandRepository( VoiceCommandParser voiceCommandParser) {
        return new VoiceCommandRepositoryImpl(voiceCommandParser);
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ForApplication Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, "tapia-db").build();
    }

    @Provides
    @Singleton
    ErrorLogRepository provideErrorLogRepository(AppDatabase appDatabase) {
        return new ErrorLogRepositoryImpl(appDatabase);
    }

//    @Provides
//    @Singleton
//    StorageRepository provideStorageRepository(final @ForApplication Context context) {
//        return new StorageRepositoryImpl(context);
//    }
//
//    @Provides
//    @Singleton
//    CommandModelMapper provideCommandModelMapper() {
//        return new CommandModelMapper();
//    }
//
    @Provides
    @Singleton
    VoiceCommandParser provideVoiceCommandParser(@ForApplication Context context) {
        return new VoiceCommandParser(context.getResources());
    }

    public interface Exposes {

        VoiceCommandRepository voiceCommandRepository();

        AppDatabase appDatabase();

        ErrorLogRepository errorLogRepository();

        VoiceCommandParser voiceCommandParser();
//
//        StorageRepository storageRepository();

//        VoiceCommandRepository commandRepository();
//
//        CommandModelMapper commandModelMapper();
//
//        CommandParser commandParser();
    }
}
