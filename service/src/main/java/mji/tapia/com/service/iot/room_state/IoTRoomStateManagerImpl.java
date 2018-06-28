package mji.tapia.com.service.iot.room_state;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Sami on 2/1/2018.
 *
 */

public class IoTRoomStateManagerImpl implements IoTRoomStateManager {

    private static final String COMMAND_LAUNDRY_SERVICE = "GRS2";
    private static final String COMMAND_MAKE_UP = "MUP";
    private static final String COMMAND_DO_NOT_DISTURB = "DND";
    private static final String COMMAND_ROOM_OCCUPANCY = "STAY";

    private IoTService.CommandEditor commandEditor;

    private BehaviorSubject<Boolean> makeUpBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> doNotDisturbBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<Boolean> laundryServiceBehaviorSubject = BehaviorSubject.create();
    private BehaviorSubject<RoomOccupancyState> roomOccupancyStateBehaviorSubject = BehaviorSubject.create();

    public IoTRoomStateManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_DO_NOT_DISTURB, Boolean.class));
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_MAKE_UP, Boolean.class));
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_LAUNDRY_SERVICE, Boolean.class));
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_ROOM_OCCUPANCY, Integer.class));
        return commandDefinitionList;
    }

    @Override
    public void onCommandUpdate(IoTCommand command) {
        switch (command.name) {
            case COMMAND_DO_NOT_DISTURB:
                doNotDisturbBehaviorSubject.onNext((Boolean) command.params.get(0));
                break;
            case COMMAND_LAUNDRY_SERVICE:
                laundryServiceBehaviorSubject.onNext((Boolean) command.params.get(0));
                break;
            case COMMAND_MAKE_UP:
                makeUpBehaviorSubject.onNext((Boolean) command.params.get(0));
                break;
            case COMMAND_ROOM_OCCUPANCY:
                roomOccupancyStateBehaviorSubject.onNext(getRoomOccupancyState((Integer)command.params.get(0)));
                break;
        }
    }

    @Override
    public Completable setDoNotDisturbState(Boolean isEnable) {
        return commandEditor.write(COMMAND_DO_NOT_DISTURB, isEnable);
    }

    @Override
    public Completable setMakeUpState(Boolean isEnable) {
        return commandEditor.write(COMMAND_MAKE_UP, isEnable);
    }

    @Override
    public Completable setLaundryServiceState(Boolean isEnable) {
        return commandEditor.write(COMMAND_LAUNDRY_SERVICE, isEnable);
    }

    @Override
    public Observable<Boolean> getDoNotDisturbStateStateObservable() {
        return doNotDisturbBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getMakeUpStateObservable() {
        return makeUpBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getLaundryServiceStateObservable() {
        return laundryServiceBehaviorSubject;
    }

    @Override
    public Observable<Boolean> getRoomOccupancyStateObservable() {
        return null;
    }

    private RoomOccupancyState getRoomOccupancyState(Integer i) {
        switch (i) {
            case 0:
                return RoomOccupancyState.VACANT;
            case 1:
                return RoomOccupancyState.GUEST;
            case 2:
                return RoomOccupancyState.STAFF;
            default:
                return null;
        }
    }
}
