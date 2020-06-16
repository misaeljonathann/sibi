package com.fasilkom.sibi.tensorflow.MOBILENETV2;

import android.content.res.AssetManager;

import com.fasilkom.sibi.tensorflow.FileUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageClassifierFactory {

    private static ImageClassifierFactory instance;

    public static ImageClassifierFactory getInstance() {
        if (instance == null) {
            instance = new ImageClassifierFactory();
        }

        return instance;
    }

    public ImageClassifier create(
            AssetManager assetManager,
            String modelFileName,
            String labelFileName,
            int inputSize,
            int numOfChannel,
            int numOfClasses,
            int numOfInferences,
            String inputName,
            String outputName
    ) throws IOException {

        List<String> labels = FileUtils.getInstance().getLabels(assetManager, labelFileName);
        ArrayList<TensorFlowInferenceInterface> tensorFlowInferenceInterfaces = new ArrayList<>();

        for (int i = 0; i < numOfInferences; i++) {
            tensorFlowInferenceInterfaces.add(new TensorFlowInferenceInterface(assetManager, modelFileName));
        }

        return new ImageClassifier(
                inputName,
                outputName,
                inputSize,
                numOfChannel,
                numOfClasses,
                labels,
                tensorFlowInferenceInterfaces
        );
    }
}
