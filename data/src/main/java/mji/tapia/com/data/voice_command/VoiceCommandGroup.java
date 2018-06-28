package mji.tapia.com.data.voice_command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 2/8/2018.
 */

public class VoiceCommandGroup implements VoiceCommandItem {

    List<VoiceCommandItem> groupItems = new ArrayList<>();

    @Override
    public List<String> validate(String speech, VoiceCommand voiceCommand) {
        List<String> list = null;
        for (VoiceCommandItem voiceCommandItem: groupItems) {
            list =  voiceCommandItem.validate(speech, voiceCommand);
            if(list != null && list.size() > 0) {
                break;
            }
        }
        return list;
    }
}
