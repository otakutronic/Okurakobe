package mji.tapia.com.data.voice_command;

import java.util.List;

/**
 * Created by Sami on 2/8/2018.
 */

public interface VoiceCommandItem {

    List<String> validate(String speech, VoiceCommand voiceCommand);
}
