package mji.tapia.com.data.voice_command;

import android.util.Log;

import java.util.List;

/**
 * Created by Sami on 2/8/2018.
 */

public class VoiceCommandGroupParam extends VoiceCommandGroup{

    static final private String TAG = "VoiceCommandGroupParam";

    enum Position {
        BEFORE,
        AFTER
    }

    private Position position;

    private String targetId;


    VoiceCommandGroupParam(Position position, String target) {
        this.position = position;
        this.targetId = target;
    }

    @Override
    public List<String> validate(String speech, VoiceCommand voiceCommand) {
        List<String> stringList = super.validate(speech, voiceCommand);
        if(stringList != null){
            if(stringList.size() > 1) {
                throw new RuntimeException("param group cannot have more than one keyword");
            } else {
                //find word
                Log.e(TAG, "TODO: add keyword to list");
                return stringList;
            }
        } else {
            return null;
        }
    }
}
