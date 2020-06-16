package com.fasilkom.sibi.tensorflow.CRF;

import android.content.res.AssetManager;
import android.os.Trace;
import android.util.Log;

import com.fasilkom.sibi.tensorflow.FileUtils;
import com.fasilkom.sibi.tensorflow.outputs.CRF.CRFResult;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrameClassifier {

    // Inner Class
    public class FrameSequence{

        private float mse;
        private float[] sequence;

        public FrameSequence(float mse, float[] seq) {
            this.mse = mse;
            this.sequence = seq;
        }

        public float getMse() {
            return mse;
        }

        public void setMse(float mse) {
            this.mse = mse;
        }

        public float[] getSequence() {
            return sequence;
        }

        public void setSequence(float[] sequence) {
            this.sequence = sequence;
        }
    }

    // Config values.
    private String inputName;
    private String outputName;

    private final int NUM_OF_FRAMES;
    private final int NUM_OF_MOBNET_FEATURES;

//    private int NUM_OF_CRF_FEATURE = 2;
    private int NUM_OF_CRF_FEATURE = 4;

    // Pre-allocated buffers.
    private List<String> labels;
    private float[] outputs;
    private String[] outputNames;

    private TensorFlowInferenceInterface tensorFlowInference;

    public FrameClassifier(
            String inputName,
            String outputName,
            int numOfFrames,
            int numOfMobnetFeatures,
            TensorFlowInferenceInterface tensorFlowInference
    ) {
        Log.d("Ukuran", " : " + tensorFlowInference.graph().operation(outputName).output(0).shape());
        this.inputName = inputName;
        this.outputName = outputName;
        this.NUM_OF_FRAMES = numOfFrames;
        this.NUM_OF_MOBNET_FEATURES = numOfMobnetFeatures;
        this.tensorFlowInference = tensorFlowInference;
        this.outputNames = new String[]{outputName};
    }

    public void classify(float[] inputData) {

        // CHECK
        outputs = new float[NUM_OF_FRAMES * NUM_OF_CRF_FEATURE];

        Trace.beginSection("Start Classifier");

        // Copy the input data to TensorFlow
        Trace.beginSection("Feed");
        tensorFlowInference.feed(inputName, inputData, 1, inputData.length/ NUM_OF_MOBNET_FEATURES, NUM_OF_MOBNET_FEATURES);
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

    public ArrayList<ArrayList<float[]>> predictFrame_Real(float[] inputData) throws IOException {

        ArrayList<ArrayList<float[]>> output = new ArrayList<>();

        classify(inputData);

        CRFResult res = new CRFResult(outputs);

        ArrayList<CRFResult.Package> packages = res.getPackagesOfNonTransition();
        ArrayList<float[]> eachSentencePackage = new ArrayList<>();

        for (CRFResult.Package pack: packages) {

            float[] frameFeature = new float [13 * NUM_OF_MOBNET_FEATURES];
            float[] flattenFrameData = Arrays.copyOfRange(
                    inputData,
                    pack.startIdx * NUM_OF_MOBNET_FEATURES,
                    (pack.startIdx + pack.length) * NUM_OF_MOBNET_FEATURES
            );

            frameFeature = equalizeFrameLength(inputData, flattenFrameData, pack);

            eachSentencePackage.add(frameFeature);

            Log.d("Length-nya", "" + frameFeature.length);
        }

//        Log.d("Length-nya", "Final 2");
        output.add(eachSentencePackage);

        return output;
    }

    // ToDo:
    // 1. Ganti return typenya jadi CRFResult

    public ArrayList<ArrayList<float[]>> predictFrame(AssetManager assetManager) throws IOException {

        ArrayList<ArrayList<float[]>> output = new ArrayList<>();

        final String DATA_INPUT = "Dataset_Input_CRF";

        String[] sentenceList = assetManager.list(DATA_INPUT);

        for (int sentenceIdx = 0; sentenceIdx < 1; sentenceIdx++) {

            String[] personList = assetManager.list(DATA_INPUT + "/" + sentenceList[sentenceIdx]);

            for (int personIdx = 0; personIdx < 1; personIdx++) {

                String[] datasetList = assetManager.list(DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx]);

                for (int datasetIdx = 0; datasetIdx < 1; datasetIdx++) {

                    if (datasetList[datasetIdx].split("_")[2].equals("data.txt")) {

//                        Log.d("HasilAkhir", "Reading : " + DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx] + "/" + datasetList[datasetIdx]);

                        float[] inputData = FileUtils.getInstance().getInputData(
                                assetManager,
                                DATA_INPUT + "/" + sentenceList[sentenceIdx] + "/" + personList[personIdx] + "/" + datasetList[datasetIdx]);

                        classify(inputData);

                        CRFResult res = new CRFResult(outputs);

                        ArrayList<CRFResult.Package> packages = res.getPackagesOfNonTransition();
                        ArrayList<float[]> eachSentencePackage = new ArrayList<>();

//                        Log.d("Length-nya", "Final 1 :" + res.getSequence().length);
                        for (CRFResult.Package pack: packages) {
//                            Log.d("Length-nya", "Final 33");

                            float[] frameFeature = new float [13 * NUM_OF_MOBNET_FEATURES];
                            float[] flattenFrameData = Arrays.copyOfRange(
                                    inputData,
                                    pack.startIdx * NUM_OF_MOBNET_FEATURES,
                                    (pack.startIdx + pack.length) * NUM_OF_MOBNET_FEATURES
                            );

                            frameFeature = equalizeFrameLength(inputData, flattenFrameData, pack);

                            eachSentencePackage.add(frameFeature);

                            Log.d("Length-nya", "" + frameFeature.length);
                        }

//                        Log.d("Length-nya", "Final 2");
                        output.add(eachSentencePackage);
                    }
                }
            }
        }

        return output;
    }

    public float[] equalizeFrameLength(float[] inputData, float[] flattenFrameData, CRFResult.Package pack) {

        float[] frameFeature = new float [13 * NUM_OF_MOBNET_FEATURES];

        /* Expand frame length */
        if (pack.length <= 13) {
//            Log.d("Equalizing", "less");

            float[] lastFrameData = Arrays.copyOfRange(
                    inputData,
                    (pack.startIdx + pack.length - 1) * NUM_OF_MOBNET_FEATURES,
                    (pack.startIdx + pack.length) * NUM_OF_MOBNET_FEATURES
            );

            System.arraycopy(flattenFrameData, 0, frameFeature, 0, flattenFrameData.length);

            for (int frameIdx = pack.length; frameIdx < 13; frameIdx++) {

                for (int featureIdx = 0; featureIdx < NUM_OF_MOBNET_FEATURES; featureIdx++) {

                    frameFeature[(frameIdx * NUM_OF_MOBNET_FEATURES) + featureIdx] = lastFrameData[featureIdx];
                }

            }
//            Log.d("Equalizing", "frame feature: " + frameFeature.length);

        /* Cut frame length */
        } else {

            // todo : Improve

//            Log.d("Equalizing", "more " + pack.length);

            FrameSequence[] frameSequences = new FrameSequence[pack.length];

            /* Init first frame */
            float[] firstFrameData = Arrays.copyOfRange(
                    flattenFrameData,
                    0 * NUM_OF_MOBNET_FEATURES,
                    1 * NUM_OF_MOBNET_FEATURES
            );
            frameSequences[0] = new FrameSequence(Float.MAX_VALUE, firstFrameData);


            /* The rest of the frames */
            for (int frameIdx = 1; frameIdx < pack.length; frameIdx++) {

                float mse = CRFUtils.getInstance().customMSE(flattenFrameData, frameIdx, NUM_OF_MOBNET_FEATURES);
                float[] iterFrameData = Arrays.copyOfRange(
                        flattenFrameData,
                        frameIdx * NUM_OF_MOBNET_FEATURES,
                        (frameIdx + 1) * NUM_OF_MOBNET_FEATURES
                );
                frameSequences[frameIdx] = new FrameSequence(mse, iterFrameData);
            }

            /* remove lowest value until frame equalized */
            int removeCount = 0;

            while (removeCount < (pack.length - 13)) {

                int lowestValIdx = 0;
                for (int frameIdx = 1; frameIdx < frameSequences.length; frameIdx++) {

                    if (frameSequences[frameIdx].getMse() < frameSequences[lowestValIdx].getMse()) {

                        lowestValIdx = frameIdx;
                    }
                }

                frameSequences = removeFromArray(frameSequences, lowestValIdx);

                removeCount++;
            }

            frameFeature = concatFrameSequence(frameSequences);
//            Log.d("Equalizing", "frame feature: " + frameFeature.length);

        }

//        Log.d("Length-nya", "Final: " + frameFeature.length);

        return frameFeature;
    }

    public float[] concatFrameSequence(FrameSequence[] frameSequences) {

        float[] concatSequence = new float[13 * NUM_OF_MOBNET_FEATURES];

        for (int frameIdx = 0; frameIdx < frameSequences.length; frameIdx++) {

            System.arraycopy(
                    frameSequences[frameIdx].sequence,
                    0,
                    concatSequence,
                    frameIdx * NUM_OF_MOBNET_FEATURES,
                    NUM_OF_MOBNET_FEATURES);
        }

        return concatSequence;
    }

    public <T> T[] removeFromArray(T[] arr, int idx) {

        // shifting elements
        for (int j = idx; j < arr.length - 1; j++) {
            arr[j] = arr[j + 1];
        }

        return Arrays.copyOfRange(arr, 0, arr.length-1);
    }
}
