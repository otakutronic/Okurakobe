package mji.tapia.com.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import mji.tapia.com.data.error_log.ErrorLog;
import mji.tapia.com.data.error_log.ErrorLogConverters;
import mji.tapia.com.data.error_log.ErrorLogDao;

/**
 * Created by Sami on 2/1/2018.
 */

@Database(entities = {ErrorLog.class}, version = 1)
@TypeConverters({ErrorLogConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ErrorLogDao errorLogDao();
}