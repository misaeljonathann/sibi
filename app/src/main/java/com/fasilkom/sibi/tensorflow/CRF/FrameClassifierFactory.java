package com.fasilkom.sibi.tensorflow.CRF;

import android.content.res.AssetManager;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;

public class FrameClassifierFactory {

    private static FrameClassifierFactory instance;

    public static FrameClassifierFactory getInstance() {
        if (instance == null) {
            instance = new FrameClassifierFactory();
        }

        return instance;
    }

    public FrameClassifier create(
            AssetManager assetManager,
            String modelFileName,
            String labelFileName,
            int numOfFrames,
            int numOfFeatures,
            String inputName,
            String outputName
    ) throws IOException {

        Log.d("LayerName", "Start Frame Classifier");
        return new FrameClassifier(
                inputName,
                outputName,
                numOfFrames,
                numOfFeatures,
                new TensorFlowInferenceInterface(assetManager, modelFileName)
        );
    }
}
