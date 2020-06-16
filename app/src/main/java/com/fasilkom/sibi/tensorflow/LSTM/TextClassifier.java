package com.fasilkom.sibi.tensorflow.LSTM;

import android.content.res.AssetManager;
import android.os.Trace;
import android.util.Log;

import com.fasilkom.sibi.tensorflow.FileUtils;
import com.fasilkom.sibi.tensorflow.outputs.LSTM.LSTMResult;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class TextClassifier {

    // Config values.
    private String inputName;
    private String outputName;
    private int inputSize;

    private long numOfTimeStep;
    private long numOfFeatures;

    // Pre-allocated buffers.
    private List<String> labels;
    private float[] outputs;
    private String[] outputNames;


    private TensorFlowInferenceInterface tensorFlowInference;

    public TextClassifier(
        String inputName,
        String outputName,
        long numOfTimeStep,
        long numOfFeatures,
        List<String> labels,
        TensorFlowInferenceInterface tensorFlowInference
    ) {
        int numClasses = (int) tensorFlowInference.graph().operation(outputName).output(0).shape().size(1);
//        Log.d("NumClass", " >> " + tensorFlowInference.graph().operation(outputName).output(0).shape());

        this.inputName = inputName;
        this.outputName = outputName;
        this.tensorFlowInference = tensorFlowInference;
        this.outputs = new float[numClasses];
        this.numOfTimeStep = numOfTimeStep;
        this.numOfFeatures = numOfFeatures;

        this.labels = labels;
        this.outputNames = new String[]{outputName};
    }


    private void classifyTextToOutputs(float[] inputData) {

//        Iterator<Operation> operationIterator = tensorFlowInference.graph().operations();
//        while (operationIterator.hasNext()){
//            Operation operation = operationIterator.next();
//            Log.d("LayerName", operation.name());
//        }

        Trace.beginSection("Start Classifier");

        // Copy the input data to TensorFlow
        Trace.beginSection("Feed");
        tensorFlowInference.feed(inputName, inputData, 1, numOfTimeStep, numOfFeatures);
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

    public LSTMResult[] predictWord_Real(ArrayList<float[]> inputData) {

        int numOfWord = inputData.size();
        Log.d("OutputLSTM", "numOfWord: " + numOfWord);

        LSTMResult[] output = new LSTMResult[numOfWord];

        for (int wordIdx = 0; wordIdx < inputData.size(); wordIdx++) {
            Log.d("OutputLSTM", "Length: " + inputData.get(wordIdx).length);

            classifyTextToOutputs(inputData.get(wordIdx));
            PriorityQueue<LSTMResult> outputQueue = getResults();
            output[wordIdx] = outputQueue.poll();

            Log.d("OutputLSTM", output[wordIdx].getResult());

        }

        return output;
    }

    public ArrayList<LSTMResult[]> predictWord(AssetManager assetManager) throws IOException {
        int inputSize = (int) this.numOfFeatures * (int) this.numOfTimeStep;

        final String DATA_INPUT = "Dataset_Input_LSTM";

        ArrayList<LSTMResult[]> output = new ArrayList<>();

        int actualDataIdx = 0;
        int totalWordToPredict = 0;
        int predictedCorrect = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("Dataset_Video_K_1.txt")));
        String line;

        while ((line = br.readLine()) != null) {

            float[] inputData = FileUtils.getInstance().getInputData(assetManager, DATA_INPUT + line + "_data_anotasi.txt");
            int numOfWord = inputData.length / inputSize;

            output.add(actualDataIdx, new LSTMResult[numOfWord]); // CHECK

            for (int wordIdx = 0; wordIdx < numOfWord; wordIdx++) {
                float[] input = Arrays.copyOfRange(inputData, (inputData.length * wordIdx / numOfWord), (inputData.length * (wordIdx + 1) / numOfWord));

                classifyTextToOutputs(input);
                PriorityQueue<LSTMResult> outputQueue = getResults();
                output.get(actualDataIdx)[wordIdx] = outputQueue.poll();

                // Check to Label
                float[] labelData = FileUtils.getInstance().getInputData(assetManager, DATA_INPUT + line + "_label_anotasi.txt");

                Log.d("filename", output.get(actualDataIdx)[wordIdx].getResult());
                if (("" + ((int) labelData[wordIdx] + 1)).equals(output.get(actualDataIdx)[wordIdx].getResult())) {
                    Log.d("filename", " == predicted");
                    predictedCorrect += 1;
                } else {
                    Log.d("fail", line + " --- " + wordIdx);
                    Log.d("fail", "Actual Label : " + ("" + ((int) labelData[wordIdx] + 1)));
                    Log.d("fail", "Prediction Label : " + output.get(actualDataIdx)[wordIdx].getResult());
                }

                totalWordToPredict += 1;
            }

            // CHECK
            actualDataIdx += 1;
        }

// =============================== VERSI TEST SEMUA FOLDER (INCLUDE TRAINING DATA)


//        // Iterate Each Sentence Folder
//        Log.d("Start", "-- sentence --" + sentences[14]);
//        for (int sentenceIdx = 14; sentenceIdx < 15; sentenceIdx++) {
//
//            String[] personList = assetManager.list(dataInput + "/" + sentences[sentenceIdx]);
//
//            // Iterate Each Person Folder
//            for (int personIdx = 0; personIdx < personList.length; personIdx++) {
//                Log.d("Start", "-- person --" + personList[personIdx]);
//
//                String[] datasetName = assetManager.list(dataInput + "/" + sentences[sentenceIdx] + "/" + personList[personIdx]);
//
//                // Iterate Each Person Dataset
//                for (int fileIdx = 0; fileIdx < datasetName.length; fileIdx++) {
//
//                    if ((datasetName[fileIdx].split("\\.")[1].equals("txt")) && (datasetName[fileIdx].split("_")[2].equals("data"))) {
//
//                        float[] inputData = FileUtils.getInstance().getInputData(assetManager, "Dataset_Input_LSTM/" + sentences[sentenceIdx] + "/" + personList[personIdx] + "/" + datasetName[fileIdx]);
//                        int numOfWord = inputData.length / inputSize;
//
//                        output.add(actualDataIdx, new CRFResult[numOfWord]); // CHECK
//
//                        for (int wordIdx = 0; wordIdx < numOfWord; wordIdx++) {
//                            float[] input = Arrays.copyOfRange(inputData, (inputData.length * wordIdx / numOfWord), (inputData.length * (wordIdx+1) / numOfWord));
//
//                            classifyTextToOutputs(input);
//                            PriorityQueue<CRFResult> outputQueue = getResults();
//                            output.get(actualDataIdx)[wordIdx] = outputQueue.poll();
//
//                            // Check to Label
//
//                            String labelFile = assetManager.list(labelInput + "/" + sentences[sentenceIdx])[0];
//                            float[] labelData = FileUtils.getInstance().getInputData(assetManager, labelInput + "/" + sentences[sentenceIdx] + "/" + labelFile);
//
////                            Log.d("banding", (""+ ((int) labelData[wordIdx]+1)).equals(output.get(actualDataIdx)[wordIdx].getResult()) + " : " + (""+ ((int) labelData[wordIdx]+1)) + " ---- " + output.get(actualDataIdx)[wordIdx].getResult());
//
//                            Log.d("filename", output.get(actualDataIdx)[wordIdx].getResult());
//                            if ((""+ ((int) labelData[wordIdx]+1)).equals(output.get(actualDataIdx)[wordIdx].getResult())) {
//                                Log.d("filename", " == predicted");
//                                predictedCorrect += 1;
//                            } else {
//                                Log.d("fail", sentences[sentenceIdx] + " --- " + wordIdx);
//                                Log.d("fail", "Actual Label : " + (""+ ((int) labelData[wordIdx]+1)));
//                                Log.d("fail", "Prediction Label : " + output.get(actualDataIdx)[wordIdx].getResult());
//                            }
//
//                            totalWordToPredict += 1;
//                        }
//
//                        actualDataIdx += 1;
//                    }
//                }
//            }
//        }
        Log.d("Prediction", "Correct : " + predictedCorrect + " from " + totalWordToPredict);
        Log.d("Prediction", "Correct : " + predictedCorrect + " from " + totalWordToPredict);

        return output;
    }

    private PriorityQueue<LSTMResult> getResults() {
        PriorityQueue<LSTMResult> outputQueue = createOutputQueue();
        for (int i = 0; i < outputs.length; i++) {
            outputQueue.add(new LSTMResult(""+(i+1)+" : "+ labels.get(i+1), outputs[i]));
        }
        return outputQueue;
    }

    private PriorityQueue<LSTMResult> createOutputQueue() {
        // Find the best classifications.
        return new PriorityQueue<LSTMResult>(
                labels.size(),
                new Comparator<LSTMResult>() {
                    @Override
                    public int compare(LSTMResult lhs, LSTMResult rhs) {
                        // Intentionally reversed to put high confidence at the head of the queue.
                        return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                    }
                });
    }
}
