package mji.tapia.com.data.voice_command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 2/8/2018.
 */

public class VoiceCommandGroupDependent extends VoiceCommandGroup{


    @Override
    public List<String> validate(String speech, VoiceCommand voiceCommand) {
        boolean isValid = true;
        List<String> list = new ArrayList<>();
        for (VoiceCommandItem voiceCommandItem: groupItems) {
            List<String> vcList =  voiceCommandItem.validate(speech, voiceCommand);
            if(vcList.size() == 0) {
                isValid = false;
                break;
            } else {
                list.addAll(vcList);
            }
        }
        if(isValid)
            return list;
        else
            return null;
    }
}
