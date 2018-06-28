package mji.tapia.com.data.voice_command;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;


import java.util.Collection;
import java.util.Set;

/**
 * Created by Sami on 9/27/2017.
 *
 */

public class VoiceCommandRepositoryImpl implements VoiceCommandRepository{


    private VoiceCommandParser voiceCommandParser;

    public VoiceCommandRepositoryImpl(VoiceCommandParser voiceCommandParser) {
        this.voiceCommandParser = voiceCommandParser;
    }

    @Override
    public VoiceCommand getMatch(int target_list_id, String string) {
        VoiceCommandDocument voiceCommandDocument = voiceCommandParser.parseXML(target_list_id);

        for (VoiceCommand vc: voiceCommandDocument.getVoiceCommandList()) {
            Set<String> consumedKeywords = vc.validate(string);
            if(consumedKeywords != null) {
                return vc;
            }
        }
        return null;
    }

    @Override
    public VoiceCommand getBestMatch(int target_list_id, String string) {
        VoiceCommandDocument voiceCommandDocument = voiceCommandParser.parseXML(target_list_id);

        Pair<VoiceCommand, Integer> voiceCommandScorePair = new Pair<>(null,0);
        for (VoiceCommand vc: voiceCommandDocument.getVoiceCommandList()) {
            Set<String> consumedKeywords = vc.validate(string);
            if(consumedKeywords != null) {
                if(consumedKeywords.size() > voiceCommandScorePair.second) {
                    voiceCommandScorePair = new Pair<>(vc,consumedKeywords.size());
                }
            }
        }
        return voiceCommandScorePair.first;
    }

    @Override
    public VoiceCommand getMatchIn(int target_list_id, Collection<String> strings) {
        for (String s: strings) {
            VoiceCommand voiceCommand = getMatch(target_list_id,s);
            if(voiceCommand != null) {
                return voiceCommand;
            }
        }
        return null;
    }

    @Override
    public VoiceCommand getBestMatchIn(int target_list_id, Collection<String> strings) {
        for (String s: strings) {
            VoiceCommand voiceCommand = getBestMatch(target_list_id,s);
            if(voiceCommand != null) {
                return voiceCommand;
            }
        }
        return null;
    }
}
