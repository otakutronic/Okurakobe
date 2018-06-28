package mji.tapia.com.data.voice_command;

import java.util.Collection;

/**
 * Created by Sami on 9/27/2017.
 */

public interface VoiceCommandRepository {



    VoiceCommand getMatch(int target_list_id, String string);

    VoiceCommand getBestMatch(int target_list_id, String string);

    VoiceCommand getMatchIn(int target_list_id, Collection<String> strings);

    VoiceCommand getBestMatchIn(int target_list_id, Collection<String> strings);
}
