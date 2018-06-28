package mji.tapia.com.data.voice_command;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 2/8/2018.
 */

public class VoiceCommandKeyword implements VoiceCommandItem {
    private String keyword;

    VoiceCommandKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public List<String> validate(String speech, VoiceCommand voiceCommand) {

        if(speech.toLowerCase().contains(keyword.toLowerCase())) {
            List<String> list = new ArrayList<>();
            list.add(keyword);
            return list;
        } else {
            return null;
        }
    }
}
