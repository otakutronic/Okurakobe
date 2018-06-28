package mji.tapia.com.data.error_log;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Sami on 2/1/2018.
 */

@Dao
public interface ErrorLogDao {
    @Query("SELECT * FROM errorlog ORDER BY uid DESC")
    List<ErrorLog> getAll();

    @Query("SELECT * FROM errorlog WHERE type IN (:type) ORDER BY uid DESC")
    List<ErrorLog> loadAllByType(ErrorLog.Type type);


    @Insert
    void insertAll(ErrorLog... errorLogs);

    @Insert
    void insert(ErrorLog errorLog);

    @Delete
    void delete(ErrorLog errorLog);
}
