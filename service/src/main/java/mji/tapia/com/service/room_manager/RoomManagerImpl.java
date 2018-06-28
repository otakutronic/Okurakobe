package mji.tapia.com.service.room_manager;

import android.content.SharedPreferences;
import android.util.Log;

import mji.tapia.com.service.util.PreferenceUtils;

/**
 * Created by andy on 3/30/2018.
 *
 */

public class RoomManagerImpl implements RoomManager {

    static private final String ROOM_REFERENCE = "ROOM_REF";
    static private final String DEFAULT_ROOM_NUMBER = "0101";

    private String roomID;
    private SharedPreferences sharedPreferences;

    public RoomManagerImpl(PreferenceUtils sharedPreferenceManager) {
        this.sharedPreferences = sharedPreferenceManager.getSharedPreference(ROOM_REFERENCE);
        this.roomID = sharedPreferences.getString(ROOM_REFERENCE, DEFAULT_ROOM_NUMBER);
    }

    @Override
    public String getRoomID() {
        return roomID;
    }

    @Override
    public void setRoomID(String roomID) {
        this.roomID = roomID;
        sharedPreferences.edit().putString(ROOM_REFERENCE, roomID).apply();
    }
}
