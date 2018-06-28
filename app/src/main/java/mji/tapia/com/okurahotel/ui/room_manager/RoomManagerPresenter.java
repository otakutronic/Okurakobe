package mji.tapia.com.okurahotel.ui.room_manager;

import mji.tapia.com.okurahotel.BasePresenter;

/**
 * Created by Sami on 9/21/2017.
 */

public class RoomManagerPresenter extends BasePresenter<RoomManagerContract.View> implements RoomManagerContract.Presenter {

    public RoomManagerPresenter(RoomManagerContract.View view) {
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
    public void selectAirCon() {
        router.navigateToAirConScreen();
    }

    @Override
    public void selectLight() {
        router.navigateToLightScreen();
    }

    @Override
    public void selectExit() {
        router.closeScreen();
    }

    @Override
    public void selectCurtain() {
        router.navigateToCurtainScreen();
    }
}
