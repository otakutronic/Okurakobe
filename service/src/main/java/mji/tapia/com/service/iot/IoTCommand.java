package mji.tapia.com.service.iot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sami on 2/6/2018.
 */

public class IoTCommand {

    public String name;
    public List<Object> params;
    public IoTCommand(String string){
        this.name = string;
    }


    static class RawCommand {
        String name;
        List<String> params = null;
        RawCommand(String string){
            this.name = string;
        }
    }

    static public class CommandDefinition {
        String name;
        Class[] values;

        public CommandDefinition(String name, Class... types) {
            this.name = name;

            this.values = types;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof String) {
                return name.equals(obj);
            } else if(obj instanceof CommandDefinition) {
                return name.equals(((CommandDefinition)obj).name);
            } else {
                return super.equals(obj);
            }
        }

        public boolean equals(String s) {return equals((Object)s);}

        static boolean validate(Class c) {
            return !(c != Boolean.class &&
                    c != Integer.class &&
                    c != String.class);
        }
        IoTCommand applyTo(RawCommand rawCommand) {
            if(rawCommand.params.size() > values.length) {
                throw new RuntimeException("definition doesn't cover all parameters");
            }
            IoTCommand ioTCommand = new IoTCommand(rawCommand.name);
            ioTCommand.params = new ArrayList<>();
            for (int i = 0; i < rawCommand.params.size(); i++) {
                if(values[i] == Boolean.class) {
                    ioTCommand.params.add(IoTParser.parseBoolean(rawCommand.params.get(i)));
                } else if (values[i] == Integer.class) {
                    ioTCommand.params.add(IoTParser.parseInteger(rawCommand.params.get(i)));
                } else if(values[i] == String.class) {
                    ioTCommand.params.add(rawCommand.params.get(i));
                } else {
                    ioTCommand.params.add(null);
                }
            }
            return ioTCommand;
        }
    }
}
