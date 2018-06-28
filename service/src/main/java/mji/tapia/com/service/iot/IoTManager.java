package mji.tapia.com.service.iot;


import java.util.List;

/**
 * Created by Sami on 2/5/2018.
 * Interface for base manager type
 */

public interface IoTManager {

    List<IoTCommand.CommandDefinition> loadCommands();

    void onCommandUpdate(IoTCommand command);
}
