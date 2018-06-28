package mji.tapia.com.data.error_log;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by Sami on 2/1/2018.
 */

public class ErrorLogConverters {
    @TypeConverter
    public static ErrorLog.Type fromTimestamp(String value) {
        for (ErrorLog.Type type: ErrorLog.Type.values()) {
            if(type.name().equals(value))
                return type;
        }
        return null;
    }

    @TypeConverter
    public static String dateToTimestamp(ErrorLog.Type value) {
        if (value == null) {
            return null;
        } else {
            return value.name();
        }
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
