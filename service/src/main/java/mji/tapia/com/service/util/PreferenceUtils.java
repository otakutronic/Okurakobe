package mji.tapia.com.service.util;

import android.content.SharedPreferences;

public interface PreferenceUtils {

    String getRoomHubMacAddress();

    SharedPreferences getSharedPreference();

    SharedPreferences getSharedPreference(String id);

}
