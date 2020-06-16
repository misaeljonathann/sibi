package com.fasilkom.sibi;

import android.content.Context;
import android.util.Log;
import android.util.TimingLogger;

import com.fasilkom.sibi.services.OpenCVService;
import com.fasilkom.sibi.tensorflow.CRF.FrameClassifier;
import com.fasilkom.sibi.tensorflow.CRF.FrameClassifierFactory;
import com.fasilkom.sibi.tensorflow.LSTM.TextClassifier;
import com.fasilkom.sibi.tensorflow.LSTM.TextClassifierFactory;
import com.fasilkom.sibi.tensorflow.MOBILENETV2.ImageClassifier;
import com.fasilkom.sibi.tensorflow.MOBILENETV2.ImageClassifierFactory;
import com.fasilkom.sibi.tensorflow.outputs.LSTM.LSTMResult;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class ModelProvider {

    final String TAG = "ModelProvider";

    private FrameClassifier frameClassifier = null;
    private ImageClassifier imageClassifier = null;
    private TextClassifier textClassifier = null;

    private TimingLogger timingLogger = null;

    @Inject
    public ModelProvider() {
        Log.d("ModelProvider", "======= Instance Created");
    }

    public void loadClassifier(
            Context context,
            String modelName) throws IOException {
        imageClassifier = ImageClassifierFactory.getInstance().create(
                context.getAssets(),
                "Widhi/Weight_MobileNet_V2_20191020_054829_K_1-5_Epoch_50.pb",
                "labels.txt",
                224,
                3,
                1280,
                4,
                "input_1",
                "flatten_1/Reshape"
        );
        frameClassifier = FrameClassifierFactory.getInstance().create(
                context.getAssets(),
                "Widhi/Weight_CRF_20191218_181608_K_5-5_Epoch_45.pb",
                "",
                480,
                1280,
                "input_1",
                "crf_1/truediv"
        );
        textClassifier = TextClassifierFactory.getInstance().create(
                context.getAssets(),
                "Widhi/Weight_LSTM_P1_20191104_210250_K_2-5_Epoch_150.pb",
                "labels.txt",
                13,
                1280,
                "input_1",
                "dense_1/Softmax"
        );
        Log.d("ModelProvider", "Model Loaded");
    }

    public void runClassifierV2(
            String videoFileName
    ) throws IOException {

        timingLogger = new TimingLogger("ALL", "Start");

        timingLogger = new TimingLogger("ALL", "Start");
        Single.just(OpenCVService.getInstance().javacvConvert(videoFileName))
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> runMobileNetV2(convertByteToFloat(result)));

        timingLogger.addSplit("All Done");
        timingLogger.dumpToLog();
    }

    public ArrayList<float[]> convertByteToFloat(ArrayList<byte[]> arr) {

        ArrayList<float[]> result = new ArrayList<>();
        for (byte[] layer: arr) {

            float[] temp = new float[224*224*3];

            for (int i = 0; i < layer.length; i++) {
                temp[i] = (layer[i] & 0xFF);
            }
            result.add(temp);
        }

        return result;
    }

    private void runMobileNetV2(ArrayList<float[]> inputData) {
        /*CHECK semua data masuk atau ngga*/
        timingLogger.addSplit("End: Preprocess");
        Single.zip(
            imageClassifier.predict_Real(inputData.subList(0, (inputData.size() / 2)), 0),
            imageClassifier.predict_Real(inputData.subList(inputData.size() / 2, inputData.size()), 1),

            new BiFunction<ArrayList<float[]>, ArrayList<float[]>, float[]>() {
                @Override
                public float[] apply(ArrayList<float[]> floats, ArrayList<float[]> floats2) throws Throwable {
                    floats.addAll(floats2);
                    return imageClassifier.convertResultToPrimitiveArray(floats, floats.size(), 1280);
                }
            }
        )
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(
                new DisposableSingleObserver<float[]>() {
                    @Override
                    public void onSuccess(float[] dataOutput) {
                        try {
                            System.out.println("HIYAHIYAHIYAHIYA");
                            runRestClassifier(dataOutput);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }
                }
            );
    }

    private void runRestClassifier(float[] mobileNetV2Result) throws IOException {

        timingLogger.addSplit("End: MobileNetV2");
        ArrayList<ArrayList<float[]>> crfResults = frameClassifier.predictFrame_Real(
            mobileNetV2Result);

        ArrayList<LSTMResult[]> lstmFinalResult = new ArrayList<>();
        timingLogger.addSplit("End: CRF");

        for (int sentenceIdx = 0; sentenceIdx < crfResults.size(); sentenceIdx++) {

            Log.d("OutputLSTM", "===================");
            Log.d("OutputLSTM", "" + crfResults.size());
            Log.d("OutputLSTM", "" + crfResults.get(sentenceIdx).size());
            LSTMResult[] lstmResults = textClassifier.predictWord_Real(crfResults.get(sentenceIdx));

            for (int q = 0; q < lstmResults.length; q++) {
                Log.d("Hasil " + (q+1), ">>" + lstmResults[q].getResult() + " - " + lstmResults[q].getConfidence());
            }

            lstmFinalResult.add(lstmResults);
        }
        timingLogger.addSplit("End: LSTM");
        timingLogger.dumpToLog();

        Runtime.getRuntime().gc();
    }
}
