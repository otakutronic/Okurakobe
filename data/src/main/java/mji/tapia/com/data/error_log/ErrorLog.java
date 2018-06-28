package mji.tapia.com.data.error_log;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.nfc.Tag;

import java.util.Date;

/**
 * Created by Sami on 2/1/2018.
 */

@Entity
public class ErrorLog {

    public enum Type {
        RUNTIME,
        WARNING
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    @PrimaryKey(autoGenerate = true)
    private int uid;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @ColumnInfo(name = "time")
    private Date time;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "stack")
    private String stack;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @ColumnInfo(name = "type")
    private Type type;
}
