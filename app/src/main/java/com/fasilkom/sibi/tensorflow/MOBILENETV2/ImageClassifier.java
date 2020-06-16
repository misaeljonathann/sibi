package com.fasilkom.sibi.tensorflow.MOBILENETV2;

import android.content.res.AssetManager;
import android.os.Trace;
import android.util.Log;

import com.fasilkom.sibi.tensorflow.FileUtils;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImageClassifier {

    // Config values.
    private String inputName;
    private String outputName;

    private int INPUT_SIZE;
    private int NUM_OF_CHANNEL;
    private int NUM_OF_CLASSES;

    // Pre-allocated buffers.
    private float[] outputs;
    private String[] outputNames;

    private ArrayList<TensorFlowInferenceInterface> tensorFlowInference;


    // Unused
    private List<String> labels;

    public ImageClassifier(
            String inputName,
            String outputName,
            int inputSize,
            int numOfChannel,
            int numOfClasses,
            List<String> labels,
            ArrayList<TensorFlowInferenceInterface> tensorFlowInference
    ) {

        this.inputName = inputName;
        this.outputName = outputName;
        this.tensorFlowInference = tensorFlowInference;

        this.INPUT_SIZE = inputSize;            // 224
        this.NUM_OF_CHANNEL = numOfChannel;     // 3
        this.NUM_OF_CLASSES = numOfClasses;     // 1280

        this.labels = labels;
        this.outputNames = new String[]{outputName};
    }

    private float[] classifyImage(float[] inputData, int inferenceIdx) {

        float[] classifierOutput = new float[1280];

        Trace.beginSection("Start Classifier");

        // Copy the input data to TensorFlow
        Trace.beginSection("Feed");
        tensorFlowInference.get(inferenceIdx).feed(inputName, inputData,1, INPUT_SIZE, INPUT_SIZE, NUM_OF_CHANNEL);
        Trace.endSection();

        // Run the inference call
        Trace.beginSection("Run");
        tensorFlowInference.get(inferenceIdx).run(outputNames);
        Trace.endSection();

        // Copy the output Tensor back into the output array
        Trace.beginSection("Fetch");
        tensorFlowInference.get(inferenceIdx).fetch(outputName, classifierOutput);
        Trace.endSection();

        Trace.endSection();

        return classifierOutput;
    }

//    private float[] classifyImage2(float[] inputData) {
//
//        float[] classifierOutput = new float[1280];
//
//        Trace.beginSection("Start Classifier");
//
//        // Copy the input data to TensorFlow
//        Trace.beginSection("Feed");
//        tensorFlowInference2.feed(inputName, inputData,1, INPUT_SIZE, INPUT_SIZE, NUM_OF_CHANNEL);
//        Trace.endSection();
//
//        // Run the inference call
//        Trace.beginSection("Run");
//        tensorFlowInference2.run(outputNames);
//        Trace.endSection();
//
//        // Copy the output Tensor back into the output array
//        Trace.beginSection("Fetch");
//        tensorFlowInference2.fetch(outputName, classifierOutput);
//        Trace.endSection();
//
//        Trace.endSection();
//
//        return classifierOutput;
//    }

    public Single predict_Real(List<float[]> inputData, int inferenceIdx) {
        Log.d("MobileNetV2-1", "Start Predict");
        Log.d("MobileNetV2-1", "" + inputData.size() + " : " + inputData.get(0).length);
        return Single.create(emitter -> {
            ArrayList<float[]> result = new ArrayList<>();

            for (int frameIdx = 0; frameIdx < inputData.size(); frameIdx++) {
//                Log.d("MobileNetV2-1", "Index : " + frameIdx);
                result.add(classifyImage(inputData.get(frameIdx), inferenceIdx));
            }

            /* Convert to primitive array sequence */
            emitter.onSuccess(result);
        })
            .subscribeOn(Schedulers.newThread());
    }

    public Single predict_Real_AllIn(float[] inputData, int inferenceIdx) {
        Log.d("MobileNetV2-1", "Start Predict");
//        Log.d("MobileNetV2-1", "" + inputData.size() + " : " + inputData.get(0).length);
        return Single.create(emitter -> {

//                result.add(classifyImage(inputData.get(frameIdx), inferenceIdx));

            /* Convert to primitive array sequence */
            emitter.onSuccess(classifyImage(inputData, inferenceIdx));
        })
            .subscribeOn(Schedulers.newThread());
    }

//    public Single predict_Real2(List<float[]> inputData) {
//        Log.d("MobileNetV2-2", "Start Predict2");
//        Log.d("MobileNetV2-2", "" + inputData.size() + " : " + inputData.get(0).length);
//        return Single.create(emitter -> {
//            ArrayList<float[]> result = new ArrayList<>();
//
//            for (int frameIdx = 0; frameIdx < inputData.size(); frameIdx++) {
////                Log.d("MobileNetV2-2", "Index : " + frameIdx);
//                result.add(classifyImage2(inputData.get(frameIdx)));
//            }
//
//            /* Convert to primitive array sequence */
//            emitter.onSuccess(result);
//            System.out.println("dikembalikan");
//        })
//            .subscribeOn(Schedulers.newThread());
//    }

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

                        classifyImage(inputData, 1);
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
