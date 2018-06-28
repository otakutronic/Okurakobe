package mji.tapia.com.okurahotel.ui.room_manager.curtain;

import android.util.Log;
import javax.inject.Inject;
import mji.tapia.com.okurahotel.BasePresenter;
import mji.tapia.com.service.iot.curtain.IoTCurtainManager;

/**
 * Created by Sami on 9/21/2017.
 *
 */

public class CurtainPresenter extends BasePresenter<CurtainContract.View> implements CurtainContract.Presenter {


    @Inject
    IoTCurtainManager curtainManager;

    public CurtainPresenter(CurtainContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
    }
    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    @Override
    public void back() {
        router.closeScreen();
    }

    @Override
    public void raiseCurtains() {
        Log.e("TAG", "raiseCurtains");
        curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.OPEN);
    }

    @Override
    public void stopCurtains() {
        Log.e("TAG", "stopCurtains");
        curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.STOP);
    }

    @Override
    public void closeCurtains() {
        Log.e("TAG", "closeCurtains");
        curtainManager.setCurtainState(IoTCurtainManager.IoTCurtain.CURTAIN, IoTCurtainManager.CurtainState.CLOSE);
    }
}
