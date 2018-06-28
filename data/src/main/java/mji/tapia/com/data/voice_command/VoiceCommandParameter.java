package mji.tapia.com.data.voice_command;

import java.util.List;

/**
 * Created by Sami on 2/7/2018.
 *
 */

public class VoiceCommandParameter {

    public enum Type {
        NUMBER,
        NAME,
        ENUM
    }

    public String getId() {
        return id;
    }

    private String id;

    public Type getType() {
        return type;
    }

    private Type type;


    public String getEnum_id() {
        if(type == Type.ENUM)
            return enum_id;
        else
            return null;
    }

    private  String enum_id = null;

    private  List<String> enumList = null;

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        switch (type) {
            case ENUM:
                if(value instanceof String) {
                    boolean isValid = false;
                    for (String enumItem: enumList) {
                        if (enumItem.equals((String)value)) {
                            isValid = true;
                            this.value = enumItem;
                            break;
                        }
                    }
                }
                break;
            case NAME:
                if(value instanceof String) {
                    this.value = value;
                }
                break;
            case NUMBER:
                if(value instanceof Integer) {
                    this.value = value;
                }
                break;
        }
        this.value = value;
    }

    VoiceCommandParameter(String id, Type type) {
        this.id = id;
        this.type= type;
        if(type == Type.ENUM) {
            throw new RuntimeException("Enum type need to have enumlist as parameter");
        }
    }

    VoiceCommandParameter(String id, Type type, String enum_id) {
        this.id = id;
        this.type= type;
        this.enum_id = enum_id;
    }
}
