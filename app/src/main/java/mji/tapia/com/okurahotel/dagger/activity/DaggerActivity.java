package mji.tapia.com.okurahotel.dagger.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import mji.tapia.com.okurahotel.OkuraHotelApplication;
import mji.tapia.com.okurahotel.dagger.ComponentFactory;


public abstract class DaggerActivity extends AppCompatActivity {

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inject(getActivityComponent());
    }

    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = ComponentFactory.createActivityComponent(this, getOkuraHotelApplication());
        }
        return activityComponent;
    }

    private OkuraHotelApplication getOkuraHotelApplication() {
        return ((OkuraHotelApplication) getApplication());
    }

    protected abstract void inject(final ActivityComponent activityComponent);
}
