package mji.tapia.com.data.voice_command;

import android.util.ArraySet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sami on 2/9/2018.
 */

public class VoiceCommandEnum {
    public Set<String> getEnumList() {
        return enumList;
    }

    private Set<String> enumList = new HashSet<>();

    public String getEnumId() {
        return enumId;
    }

    private String enumId;

    VoiceCommandEnum(String id) {
        enumId =id;
    }

    void addEnumItem(String enumItem) {
        enumList.add(enumItem);
    }
}
