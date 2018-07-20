package mji.tapia.com.service.iot.air_con;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Andy on 6/1/2018.
 */

public class IoTAirConManagerImpl implements IoTAirConManager {

    private static final int SWITCH_OFF = 0;
    private static final int SWITCH_ON = 3;

    private static final String COMMAND_AC_SWITCH = "A1VL";

    private IoTService.CommandEditor commandEditor;

    private BehaviorSubject<Boolean> stateBehaviorSubject = BehaviorSubject.create();

    public IoTAirConManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_AC_SWITCH, Integer.class));
        return commandDefinitionList;
    }

    @Override
    public void onCommandUpdate(IoTCommand command) {
        switch (command.name) {
            case COMMAND_AC_SWITCH:
                final int value = (int) command.params.get(0);
                final boolean isOn = stateIntToBoolean(value);
                stateBehaviorSubject.onNext(isOn);
                break;
        }
    }

    @Override
    public Completable setAirConState(boolean state) {
        CompletableSubject completableSubject = CompletableSubject.create();
        final int stateInt = stateBooleanToInt(state);
        commandEditor.write(COMMAND_AC_SWITCH, stateInt);
        completableSubject.onComplete();
        return completableSubject;
    }

    @Override
    public Observable<Boolean> getAirConState() {
        return stateBehaviorSubject;
    }

    private int stateBooleanToInt(Boolean state) {
        final int stateInt = (state) ? SWITCH_ON : SWITCH_OFF;
        return stateInt;
    }

    private Boolean stateIntToBoolean(int state) {
        final boolean stateBoolean = (state == SWITCH_ON);
        return stateBoolean;
    }
}
