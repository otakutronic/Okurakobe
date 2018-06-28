package mji.tapia.com.okurahotel;

import android.os.Bundle;

import mji.tapia.com.okurahotel.dagger.fragment.DaggerFragment;


public abstract class BaseFragment extends DaggerFragment implements BaseView {

    private static final String KEY_VIEW_ID = "view_id";


    private String viewId;
    private boolean isViewRecreated;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().start();
    }


    @Override
    public void onResume() {
        super.onResume();
        getPresenter().activate();
    }

    @Override
    public void onPause() {
        getPresenter().deactivate();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_VIEW_ID, viewId);
    }

    public void onPreDestroy() {
        getPresenter().destroy();
    }



    public abstract ScopedPresenter getPresenter();
}
