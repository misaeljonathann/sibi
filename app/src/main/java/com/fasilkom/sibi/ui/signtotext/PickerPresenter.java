package com.fasilkom.sibi.ui.signtotext;

import com.fasilkom.sibi.ModelProvider;

import java.io.IOException;

import javax.inject.Inject;

public class PickerPresenter implements PickerContract.Presenter {

    private PickerContract.View mView;

    private ModelProvider modelProvider;

    @Inject
    public PickerPresenter(ModelProvider modelProvider) {
        this.modelProvider = modelProvider;
    }

    @Override
    public void executeClassifier(String videoFileName) {
        try {
            modelProvider.runClassifierV2(videoFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attach(PickerContract.View view) {
        this.mView = view;
    }

    @Override
    public void printString() {
        System.out.println("HELLOOOOOOOOOOOOOOOOOOOOOOO");
    }
}
