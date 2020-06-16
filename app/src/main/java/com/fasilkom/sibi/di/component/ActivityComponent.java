package com.fasilkom.sibi.di.component;

import android.app.Application;

import com.fasilkom.sibi.ModelProvider;
import com.fasilkom.sibi.activities.HomePageActivity;
import com.fasilkom.sibi.di.module.ActivityModule;
import com.fasilkom.sibi.ui.camera.CameraActivity;
import com.fasilkom.sibi.ui.signtotext.PickerActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ActivityModule.class })
public interface ActivityComponent {

    void inject(PickerActivity pickerActivity);
    void inject(HomePageActivity homePageActivity);
}
