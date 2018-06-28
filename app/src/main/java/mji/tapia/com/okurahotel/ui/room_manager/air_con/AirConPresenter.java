package mji.tapia.com.okurahotel.ui.room_manager.air_con;

import javax.inject.Inject;
import io.reactivex.disposables.Disposable;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.iot.air_con.IoTAirConManager;

/**
 * Created by Andy on 06/01/2018.
 */

public class AirConPresenter extends BasePresenter<AirConContract.View> implements AirConContract.Presenter{

    final static private String TAG = "AirConPresenter";

    @Inject
    IoTAirConManager airConManager;

    private Disposable airConStateDisposable;

    public AirConPresenter(AirConContract.View view) {
        super(view);
    }

    @Override
    public void activate() {
        super.activate();

        airConStateDisposable = airConManager.getAirConState().subscribe(state -> {
            doIfViewNotNull(view -> view.setAirConSwitch(state, false));
        });

    }

    @Override
    public void deactivate() {
        super.deactivate();
        if(airConStateDisposable != null) {
            airConStateDisposable.dispose();
        }
    }

    @Override
    public void sendCommand(boolean state) {
        airConManager.setAirConState(state);
    }

    @Override
    public void back() {
        router.closeScreen();
    }

}

