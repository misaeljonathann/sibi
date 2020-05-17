package com.fasilkom.sibi.tensorflow.MOBILENETV2;

import android.content.res.AssetManager;
import android.os.Trace;
import android.util.Log;

import com.fasilkom.sibi.tensorflow.FileUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageClassifier {

    // Config values.
    private String inputName;
    private String outputName;

    private int INPUT_SIZE;
    private int NUM_OF_CHANNEL;
    private int NUM_OF_CLASSES;

    // Pre-allocated buffers.
    private List<String> labels;
    private float[] outputs;
    private String[] outputNames;


    private TensorFlowInferenceInterface tensorFlowInference;

    public ImageClassifier(
            String inputName,
            String outputName,
            int inputSize,
            int numOfChannel,
            int numOfClasses,
            List<String> labels,
            TensorFlowInferenceInterface tensorFlowInference
    ) {
//        int numClasses = (int) tensorFlowInference.graph().operation(outputName).output(0).shape().size(1);
//        Log.d("flatten", outputName + " >> " + tensorFlowInference.graph(x).operation(outputName).output(0).shape());

        this.inputName = inputName;
        this.outputName = outputName;
        this.tensorFlowInference = tensorFlowInference;

        this.INPUT_SIZE = inputSize;
        this.NUM_OF_CHANNEL = numOfChannel;
        this.NUM_OF_CLASSES = numOfClasses;

        this.labels = labels;
        this.outputNames = new String[]{outputName};
    }


    private void classifyImage(float[] inputData) {

//        Iterator<Operation> operationIterator = tensorFlowInference.graph().operations();
//        while (operationIterator.hasNext()){
//            Operation operation = operationIterator.next();
//            Log.d("LayerName", operation.name());
//        }
        this.outputs = new float[1280];

        Trace.beginSection("Start Classifier");

        // Copy the input data to TensorFlow
        Trace.beginSection("Feed");
        tensorFlowInference.feed(inputName, inputData,1, INPUT_SIZE, INPUT_SIZE, NUM_OF_CHANNEL);
        Trace.endSection();

        // Run the inference call
        Trace.beginSection("Run");
        tensorFlowInference.run(outputNames);
        Trace.endSection();

        // Copy the output Tensor back into the output array
        Trace.beginSection("Fetch");
        tensorFlowInference.fetch(outputName, outputs);
        Trace.endSection();

        Trace.endSection();
    }

    public float[] predict_Real(ArrayList<float[]> inputData) {

        Log.d("MobileNetV2", "Start Predict");
        ArrayList<float[]> result = new ArrayList<>();

        for (int frameIdx = 0; frameIdx < inputData.size(); frameIdx++) {
            classifyImage(inputData.get(frameIdx));
            result.add(outputs);
        }

        /* Convert to primitive array sequence */

        return convertResultToPrimitiveArray(result, inputData.size(), NUM_OF_CLASSES);
    }

    public float[] predict(AssetManager assetManager) throws IOException {

        Log.d("MobileNetV2", "Start Predict");

        ArrayList<float[]> result = new ArrayList<>();
        int frameNumber = 0;

        final String DATA_INPUT = "Dataset_Input_MobileNetV2";

        String[] sentenceList = assetManager.list(DATA_INPUT);

        for (int sentenceIdx = 2; sentenceIdx < 3; sentenceIdx++) {

            String[] personList = assetManager.list(DATA_INPUT + "/" + sentenceList[sentenceIdx]);

            for (int personIdx = 0; personIdx < 1; personIdx++) {

                String[] datasetList = assetManager.list(DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx]);

                for (int datasetIdx = 0; datasetIdx < 1; datasetIdx++) {

//                    Log.d("filename", "================== " + datasetList[datasetIdx]);
                    String[] frameList = assetManager.list(DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx] + "/" + datasetList[datasetIdx]);

                    for (int frameIdx = 0; frameIdx < frameList.length; frameIdx++) {
//                        Log.d("filename", "================== " + frameList[frameIdx]);
                        float[] inputData = FileUtils.getInstance().getInputData(
                                assetManager,
                                DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx] + "/" + datasetList[datasetIdx] + "/" + frameList[frameIdx]
                        );

                        classifyImage(inputData);
                        result.add(outputs);

                        frameNumber++;

                    }
                }
            }
        }

        /* Convert to primitive array sequence */
        return convertResultToPrimitiveArray(result, frameNumber, NUM_OF_CLASSES);
    }

    public float[] convertResultToPrimitiveArray(
            ArrayList<float[]> result, int frameNumber, int numOfClasses) {

        float[] convertedResult = new float[frameNumber * numOfClasses];

        for (int i = 0; i < result.size(); i++) {
            System.arraycopy(result.get(i), 0, convertedResult, i * numOfClasses, numOfClasses);
        }

        return convertedResult;

    }
}
