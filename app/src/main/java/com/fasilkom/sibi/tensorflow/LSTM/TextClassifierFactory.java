package com.fasilkom.sibi.tensorflow.LSTM;


import android.content.res.AssetManager;

import com.fasilkom.sibi.tensorflow.FileUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.List;

public class TextClassifierFactory {

    private static TextClassifierFactory instance;

    public static TextClassifierFactory getInstance() {
        if (instance == null) {
            instance = new TextClassifierFactory();
        }

        return instance;
    }

    public TextClassifier create(
            AssetManager assetManager,
            String modelFileName,
            String labelFileName,
            int numOfTimeStep,
            int numOfFeatures,
            String inputName,
            String outputName
    ) throws IOException {

        // labelFileName vs labelFilePath
        List<String> labels = FileUtils.getInstance().getLabels(assetManager, labelFileName);

        return new TextClassifier(
                inputName,
                outputName,
                Long.valueOf(numOfTimeStep),
                Long.valueOf(numOfFeatures),
                labels,
                new TensorFlowInferenceInterface(assetManager, modelFileName)
            );
    }
}
