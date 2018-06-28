package mji.tapia.com.okurahotel.ui.room_manager.light;

import java.util.HashMap;

import javax.inject.Inject;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.iot.light.IoTLightManager;

/**
 * Created by Andy on 6/21/2018.
 */

public class LightPresenter extends BasePresenter<LightContract.View> implements LightContract.Presenter {

    private static final String TAG = "LightPresenter";

    @Inject
    IoTLightManager lightManager;

    private Disposable roomDisposable;
    private Disposable spotDisposable;
    private Disposable footDisposable;
    private LightContract.View view;

    private HashMap<CommandType, Boolean> commandStates = new HashMap<>();

    public LightPresenter(LightContract.View view) {
        super(view);
        this.view = view;
        commandStates.put(CommandType.ROOM,false);
        commandStates.put(CommandType.SPOT,false);
        commandStates.put(CommandType.FOOT,false);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void activate() {
        super.activate();

        roomDisposable = lightManager.getLightStateObserver().subscribe(state -> {
            setCommand(CommandType.ROOM, state);
        });

        spotDisposable = lightManager.getSpotStateObserver().subscribe(state -> {
            setCommand(CommandType.SPOT, state);
        });

        footDisposable = lightManager.getFootStateObserver().subscribe(state -> {
            setCommand(CommandType.FOOT, state);
        });
    }

    private synchronized void setCommand(CommandType commandType, boolean state) {
        commandStates.put(commandType, state);
        doIfViewNotNull(view -> view.setLightButton(commandType, state));
        updateSwitch();
    }

    private void updateSwitch() {
        boolean isLightOn = false;

        for (CommandType key : commandStates.keySet()) {
            final boolean isActive = commandStates.get(key);
            isLightOn = (isActive) ? true : isLightOn;
        }

        view.setLightSwitch(isLightOn, false);
    }

    @Override
    public void sendCommand(CommandType commandType, boolean state) {
        final int commandIndex = CommandType.valueOf(commandType.name()).ordinal();

        if(commandType.equals(CommandType.ALL)) {
            lightManager.setLightStateAll(state);
        } else {
            lightManager.setLightState(commandIndex, state);
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        roomDisposable.dispose();
        spotDisposable.dispose();
        footDisposable.dispose();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {
        router.closeScreen();
    }
}