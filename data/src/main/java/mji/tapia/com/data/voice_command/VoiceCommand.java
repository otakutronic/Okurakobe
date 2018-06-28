package mji.tapia.com.data.voice_command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Sami on 9/27/2017.
 */

public class VoiceCommand {

//    OPEN_CURTAIN,
//    CLOSE_CURTAIN,
//    OPEN_LACE,
//    CLOSE_LACE,
//
//    ENTRANCE_LIGHT_ON,
//    ENTRANCE_LIGHT_OFF,
//    ROOM_LIGHT_ON,
//    ROOM_LIGHT_OFF,
//    NIGHT_LIGHT_ON,
//    NIGHT_LIGHT_OFF,
//    MINIBAR_LIGHT_ON,
//    MINIBAR_LIGHT_OFF,
//    WASHROOM_LIGHT_ON,
//    WASHROOM_LIGHT_OFF,
//    FOOT_LIGHT_ON,
//    FOOT_LIGHT_OFF,
//    ALL_LIGHT_ON,
//    ALL_LIGHT_OFF



    private List<VoiceCommandParameter> parameterList = new ArrayList<>();
    private List<VoiceCommandItem> itemList = new ArrayList<>();

    private String name;

    VoiceCommand(String id) {
        this.name = id;
    }

    public String getId() {
        return name;
    }

    public Integer getInteger(String parameterID) {
        for (VoiceCommandParameter param: parameterList) {
            if(param.getId().equals(parameterID)) {
                return (Integer) param.getValue();
            }
        }
        return null;
    }

    public String getString(String parameterID) {
        for (VoiceCommandParameter param: parameterList) {
            if(param.getId().equals(parameterID)) {
                return (String) param.getValue();
            }
        }
        return null;
    }

    void addItem(VoiceCommandItem voiceCommandItem) {
        itemList.add(voiceCommandItem);
    }

    void addParam(VoiceCommandParameter voiceCommandParameter) {
        for (VoiceCommandParameter parameter: parameterList) {
            if(parameter.getId().equals(voiceCommandParameter.getId())) {
                throw new RuntimeException("id of parameter already used");
            }
        }
        parameterList.add(voiceCommandParameter);
    }

    Set<String> validate(String speech) {
        boolean isValid = true;
        Set<String> consumedKeyword = new HashSet<>();
        for (VoiceCommandItem vci: itemList) {
            List<String> list = vci.validate(speech, this);
            if(list == null) {
                isValid = false;
                break;
            } else {
                consumedKeyword.addAll(list);
            }
        }
        if(isValid) {
            return consumedKeyword;
        } else
            return null;
    }

    List<VoiceCommandParameter> getParameterList() {
        return parameterList;
    }

    VoiceCommandParameter getParameter(String parameterID) {
        for (VoiceCommandParameter param: parameterList) {
            if(param.getId().equals(parameterID)) {
                return param;
            }
        }
        return null;
    }

}
