package com.fasilkom.sibi;

import android.app.Application;

import com.fasilkom.sibi.di.component.ActivityComponent;
import com.fasilkom.sibi.di.component.DaggerActivityComponent;

public class BaseApplication extends Application {

    private ActivityComponent activityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        activityComponent = DaggerActivityComponent.builder()
            .build();
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }
}
