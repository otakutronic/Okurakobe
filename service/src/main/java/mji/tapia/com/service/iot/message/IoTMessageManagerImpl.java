package mji.tapia.com.service.iot.message;

import java.util.ArrayList;
import java.util.List;

import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Sami on 2/1/2018.
 */

public class IoTMessageManagerImpl implements IoTMessageManager {

    private IoTService.CommandEditor commandEditor;

    public IoTMessageManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        return commandDefinitionList;
    }

    @Override
    public void onCommandUpdate(IoTCommand command) {

    }
}
