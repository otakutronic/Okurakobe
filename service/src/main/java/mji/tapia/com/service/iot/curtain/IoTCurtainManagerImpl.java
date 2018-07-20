package mji.tapia.com.service.iot.curtain;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import mji.tapia.com.service.iot.IoTCommand;
import mji.tapia.com.service.iot.IoTService;

/**
 * Created by Andy on 2/6/2018.
 * curtain manager
 */

public class IoTCurtainManagerImpl implements IoTCurtainManager {

    static final private String COMMAND_CURTAIN = "DL1";

    private IoTService.CommandEditor commandEditor;

    public IoTCurtainManagerImpl(IoTService.CommandEditor commandEditor) {
        this.commandEditor = commandEditor;
    }


    private static CurtainState getCurtainState(Integer value){
        switch (value){
            case 0:
                return CurtainState.STOP;
            case 1:
                return CurtainState.OPEN;
            case 2:
                return CurtainState.CLOSE;
            default:
                return null;
        }
    }

    private static Integer getCurtainValue(CurtainState curtainState){
        switch (curtainState){
            case OPEN:
                return 1;
            case CLOSE:
                return 2;
            case STOP:
                return 0;
            default:
                return -1;
        }
    }

    @Override
    public List<IoTCommand.CommandDefinition> loadCommands() {
        List<IoTCommand.CommandDefinition> commandDefinitionList = new ArrayList<>();
        commandDefinitionList.add(new IoTCommand.CommandDefinition(COMMAND_CURTAIN, Integer.class));
        return commandDefinitionList;
    }

    private BehaviorSubject<CurtainState> curtainStateObservable = BehaviorSubject.create();

    @Override
    public void onCommandUpdate(IoTCommand command) {
        switch (command.name) {
            case COMMAND_CURTAIN:
                curtainStateObservable.onNext(getCurtainState((Integer) command.params.get(0)));
                break;
        }
    }

    @Override
    public Observable<CurtainState> curtainStateObservable(IoTCurtain iotCurtain) {
        if(iotCurtain == IoTCurtain.CURTAIN)
            return curtainStateObservable;
        else
            return null;
    }

    @Override
    public Completable setCurtainState(IoTCurtain targetCurtain, CurtainState curtainState) {
        switch (targetCurtain) {
            case CURTAIN:
                return commandEditor.write(COMMAND_CURTAIN, getCurtainValue(curtainState));
        }
        return null;
    }
}
