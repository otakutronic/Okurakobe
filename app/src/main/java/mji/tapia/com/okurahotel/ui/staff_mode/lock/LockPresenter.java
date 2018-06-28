package mji.tapia.com.okurahotel.ui.staff_mode.lock;

import mji.tapia.com.okurahotel.BasePresenter;

/**
 * Created by Sami on 1/29/2018.
 */

public class LockPresenter extends BasePresenter<LockContract.View> implements LockContract.Presenter {
    public LockPresenter(LockContract.View view) {
        super(view);
    }

    @Override
    public void onPin(String pinCode) {
        if(pinCode.equals("9191")) {
            router.navigateToStaffModeScreen();
        }
    }

    @Override
    public void back() {
        router.closeScreen();
    }
}
