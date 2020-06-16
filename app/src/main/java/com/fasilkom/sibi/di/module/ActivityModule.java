package com.fasilkom.sibi.di.module;

import com.fasilkom.sibi.ModelProvider;
import com.fasilkom.sibi.ui.signtotext.PickerContract;
import com.fasilkom.sibi.ui.signtotext.PickerPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ActivityModule {

//    @Binds
//    abstract ModelProvider provideModelProvider(
//        ModelProvider modelProvider);

    @Provides
    static PickerContract.Presenter providePickerPresenter(ModelProvider modelProvider) {
        return new PickerPresenter(modelProvider);
    }
}
