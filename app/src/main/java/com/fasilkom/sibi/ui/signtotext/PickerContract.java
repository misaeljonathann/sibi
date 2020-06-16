package com.fasilkom.sibi.ui.signtotext;

public interface PickerContract {

    interface View {

    }

    interface Presenter {
        void attach(View view);
        void executeClassifier(String videoFileName);
        void printString();
    }
}
