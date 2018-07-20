package mji.tapia.com.service.iot.light;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Andy on 2/1/2018.
 */

public class IoTLightManagerImpl implements IoTLightManager {

    public static final int SWITCH_OFF = 0;
    public static final int SWITCH_ON = 1;

    public static final String COMMAND_LIGHT = "LT3";
    public static final String COMMAND_SPOT = "LT1";
    public static final String COMMAND_FOOT = "LT2";

    public static final String[] COMMANDS_ALL = new String[]{COMMAND_LIGHT, COMMAND_SPOT, COMMAND_FOOT};

    private IoTService.CommandEditor commandEditor;

    private BehaviorSubject<Boolean> lightBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> spotBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> footBehaviorSubject = BehaviorSubject.create();

    public IoTLightManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_LIGHT, Integer.class));
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_SPOT, Integer.class));
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_FOOT, Integer.class));
        return commandDefinitionList;
    }

    @Override
    public void onCommandUpdate(IoTCommand command) {
        switch (command.name) {
            case COMMAND_LIGHT:
                int value = (int) command.params.get(0);
                boolean isOn = (value == SWITCH_ON);
                lightBehaviorSubject.onNext(isOn);
                break;
            case COMMAND_SPOT:
                value = (int) command.params.get(0);
                isOn = (value == SWITCH_ON);
                spotBehaviorSubject.onNext(isOn);
                break;
            case COMMAND_FOOT:
                value = (int) command.params.get(0);
                isOn = (value == SWITCH_ON);
                footBehaviorSubject.onNext(isOn);
                break;
        }
    }

    @Override
    public Completable setLightState(int lightIndex, boolean state) {
        CompletableSubject completableSubject = CompletableSubject.create();
        final int stateInt = (state) ? SWITCH_ON : SWITCH_OFF;
        final String command = COMMANDS_ALL[lightIndex];
        commandEditor.write(command, stateInt);
        completableSubject.onComplete();
        return completableSubject;
    }

    @Override
    public Completable setLightStateAll(boolean state) {
        CompletableSubject completableSubject = CompletableSubject.create();
        for(int i = 0; i < COMMANDS_ALL.length; i++) {
            setLightState(i, state);
        }
        completableSubject.onComplete();
        return completableSubject;
    }

    @Override
    public Observable<Boolean> getLightStateObserver() {
        return lightBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getSpotStateObserver() {
        return spotBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getFootStateObserver() {
        return footBehaviorSubject;
    }
}


