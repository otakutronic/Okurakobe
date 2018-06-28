package mji.tapia.com.service.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtilsImpl implements PreferenceUtils {

    private static final String USER_PREFERENCES = "user_preferences";
    private static final String KEY_ROOM_MAC_ADDRESS = "key_room_mac_address";

    private final SharedPreferences preferences;
    static private PreferenceUtils instance;

    private Context context;

    public PreferenceUtilsImpl(final Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public String getRoomHubMacAddress() {
        return preferences.getString(KEY_ROOM_MAC_ADDRESS,null);
    }

    static public PreferenceUtils getInstance(Context context) {
        if(instance == null) {
            instance = new PreferenceUtilsImpl(context);
        }
        return instance;
    }

    @Override
    public SharedPreferences getSharedPreference() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public SharedPreferences getSharedPreference(String id) {
        return context.getSharedPreferences(id, Context.MODE_PRIVATE);
    }
}
