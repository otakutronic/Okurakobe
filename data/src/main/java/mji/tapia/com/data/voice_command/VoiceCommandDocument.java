package mji.tapia.com.data.voice_command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Sami on 2/9/2018.
 *
 */

public class VoiceCommandDocument {

    public List<VoiceCommand> getVoiceCommandList() {
        return voiceCommandList;
    }

    public List<VoiceCommandEnum> getVoiceCommandEnums() {
        return voiceCommandEnums;
    }

    private List<VoiceCommand> voiceCommandList = new ArrayList<>();

    private List<VoiceCommandEnum> voiceCommandEnums = new ArrayList<>();


    Set<String> getEnumList(String id) {
        for (VoiceCommandEnum v: voiceCommandEnums) {
            if(v.getEnumId().equals(id)) {
                return v.getEnumList();
            }
        }
        return null;
    }

    void addCommand(VoiceCommand voiceCommand) {
        for (VoiceCommand v: voiceCommandList) {
            if(v.getId().equals(voiceCommand.getId())){
                throw new RuntimeException("enum with this id has already been added");
            }
        }
        voiceCommandList.add(voiceCommand);
    }

    void addEnum(VoiceCommandEnum voiceCommandEnum) {
        for (VoiceCommandEnum v: voiceCommandEnums) {
            if(v.getEnumId().equals(voiceCommandEnum.getEnumId())){
                throw new RuntimeException("enum with this id has already been added");
            }
        }
        voiceCommandEnums.add(voiceCommandEnum);
    }

    boolean check() {
        boolean isCorrect = true;

        //check the enum
        for (VoiceCommand vc: voiceCommandList) {
            for (VoiceCommandParameter vcp: vc.getParameterList()) {
                if(vcp.getType() == VoiceCommandParameter.Type.ENUM) {
                    boolean enumExist = false;
                    for (VoiceCommandEnum vce: voiceCommandEnums) {
                        if(vce.getEnumId().equals(vcp.getEnum_id())) {
                            enumExist = true;
                        }
                    }
                    if(!enumExist) {
                        throw  new RuntimeException("Error enum not found");
                    }
                }
            }
        }
        return isCorrect;
    }
}
