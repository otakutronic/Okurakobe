package mji.tapia.com.service.iot;

import android.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sami on 9/26/2017.
 */

class IoTParser {

    public final static char CR  = (char) 0x0D;
    public final static char LF  = (char) 0x0A;

    static IoTCommand.RawCommand[] parse(String string) {
        String[] strings = string.split("\\r?\\n");

        List<IoTCommand.RawCommand> commandList = new ArrayList<>();

        for (String s : strings) {
            String[] split_command = s.split("=");

            String command = split_command[0];
            IoTCommand.RawCommand cmd = new IoTCommand.RawCommand(command);
            if (split_command.length > 1) {
                String value = split_command[1];

                String[] params = value.split(",");
                cmd.params = Arrays.asList(params);
            }
            commandList.add(cmd);
        }

        IoTCommand.RawCommand[] commandArray = new IoTCommand.RawCommand[commandList.size()];
        return commandList.toArray(commandArray);
    }

    static String formatWithValues(String commandName, Object... values) {
        String formattedString = commandName;
        if(values.length > 0) {
            formattedString += "=";
            formattedString += format(values[0]);

            for (int i = 1; i < values.length; i++) {
                formattedString += "," + format(values[i]);
            }
        }
        formattedString = formattedString + CR + LF;

        return formattedString;
    }

    static Boolean parseBoolean(String s) {
        if(s.equals("0")) {
            return false;
        } else if(s.equals("1")) {
            return true;
        } else {
            return null;
        }
    }

    static Integer parseInteger(String s) {
        return Integer.parseInt(s);
    }

    static String format(Object object) {
        if(object instanceof Boolean) {
            return format((Boolean) object);
        } else if(object instanceof Integer) {
            return format((Integer) object);
        } else if(object instanceof String) {
            return (String) object;
        } else {
            return null;
        }
    }

    static String format(Boolean b) {
        if(b == null) {
            return null;
        } else {
            return b ? "1":"0";
        }
    }

    static String format(Integer i) {
        if(i == null) {
            return null;
        } else {
            return Integer.toString(i);
        }
    }
}
