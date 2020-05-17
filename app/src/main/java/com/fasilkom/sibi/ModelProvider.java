package com.fasilkom.sibi;

import android.content.Context;
import android.util.Log;
import android.util.TimingLogger;

import com.fasilkom.sibi.interfaces.Classifier;
import com.fasilkom.sibi.services.OpenCVService;
import com.fasilkom.sibi.tensorflow.CRF.FrameClassifier;
import com.fasilkom.sibi.tensorflow.CRF.FrameClassifierFactory;
import com.fasilkom.sibi.tensorflow.LSTM.TextClassifier;
import com.fasilkom.sibi.tensorflow.LSTM.TextClassifierFactory;
import com.fasilkom.sibi.tensorflow.MOBILENETV2.ImageClassifier;
import com.fasilkom.sibi.tensorflow.MOBILENETV2.ImageClassifierFactory;
import com.fasilkom.sibi.tensorflow.ModelNullException;
import com.fasilkom.sibi.tensorflow.outputs.LSTM.LSTMResult;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ModelProvider {

    final String TAG = "ModelProvider";

    final static String LSTM = "lstm";
    final static String CRF = "crf";
    final static String MOBILENETV2 = "mobilenetv2";
    final static String ALL = "all";

    private Classifier classifier = null;
    private FrameClassifier factory = null;
    private ImageClassifier imageClassifier = null;
    private TextClassifier factory_lstm = null;

    private static ModelProvider instance;

    public static ModelProvider getInstance() {
        if (instance == null) {
            instance = new ModelProvider();
        }

        return instance;
    }

    public void loadClassifier(
            Context context,
            String modelName) throws IOException {

        switch (modelName) {
            case ALL:
                Log.d("RxJava", "All Model : " + Thread.currentThread().getName());
                try {
                    imageClassifier = ImageClassifierFactory.getInstance().create(
                            context.getAssets(),
                            "Widhi/Weight_MobileNet_V2_20191020_054829_K_1-5_Epoch_50.pb",
                            "labels.txt",
                            224,
                            3,
                            1280,
                            "input_1",
                            "flatten_1/Reshape"
                    );
                    factory = FrameClassifierFactory.getInstance().create(
                            context.getAssets(),
                            "Widhi/Weight_CRF_20191218_181608_K_5-5_Epoch_45.pb",
                            "",
                            480,
                            1280,
                            "input_1",
                            "crf_1/truediv"
                    );
                    factory_lstm = TextClassifierFactory.getInstance().create(
                            context.getAssets(),
                            "Widhi/Weight_LSTM_P1_20191104_210250_K_2-5_Epoch_150.pb",
                            "labels.txt",
                            13,
                            1280,
                            "input_1",
                            "dense_1/Softmax"
                    );

                } catch (IOException e) {
                    Log.d("MobileNetV2", "All Failed Init");
                    e.printStackTrace();
                }
        }
    }


    public void checkIfModelLoaded() throws ModelNullException {
        if (this.imageClassifier == null) {
            throw new ModelNullException("Null model: MobileNetV2, model is not loaded successfully");
        }
        if (this.factory == null) {
            throw new ModelNullException("Null model: CRF, model is not loaded successfully");
        }
        if (this.factory_lstm == null) {
            throw new ModelNullException("Null model: LSTM, model is not loaded successfully");
        }
    }

    public void runClassifier() {
        Disposable disposable = Observable.create(emitter -> {
            System.out.println("processing item on thread " + Thread.currentThread().getName());
            emitter.onNext(ModelProvider.getInstance().runClassifier(
                    getApplicationContext(),
                    modelType,
                    videoFileName,
                    cameraPreview.getMeasuredHeight(),
                    cameraPreview.getMeasuredWidth()));
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> Log.d("CameraActivity", "Rx Schedulers on error : " + error))
                .subscribe(result -> System.out.println("consuming item on thread " + Thread.currentThread().getName()));
    }

    public ArrayList<LSTMResult[]> runClassifier(
            Context context,
            String modelName,
            String videoFileName,
            int videoFrameHeight,
            int videoFrameWidth
    ) throws ModelNullException {
        TimingLogger timingLogger = new TimingLogger(TAG, "Start");

        try {
            checkIfModelLoaded();
        } catch (ModelNullException e) {
            throw e;
        }

        ArrayList<float[]> inputData = null;

        try {
            timingLogger.addSplit("(1) Pre-Flow");

            /* Case 1 : Input From Actual Recorded Video */
            inputData = OpenCVService.getInstance().javacvConvert(
                    context,
                    videoFileName
            );

            timingLogger.addSplit("(2) JavaCV Extract Data");

            float[] mobileNetV2Result = imageClassifier.predict_Real(inputData);

            timingLogger.addSplit("(3) MobileNetV2");

            ArrayList<ArrayList<float[]>> crfResults = factory.predictFrame_Real(
                    mobileNetV2Result);

            timingLogger.addSplit("(4) CRF");
//            for (ArrayList<float[]> sentence: crfResults) {
//
//                Log.d("OutputCRF", " Sentence Size : " + sentence.size());
//                for (float[] frameFeature: sentence) {
//
//                    Log.d("OutputCRF", " Feature Length : " + frameFeature.length);
//                }
//            }

            ArrayList<LSTMResult[]> lstmFinalResult = new ArrayList<>();

            for (int sentenceIdx = 0; sentenceIdx < crfResults.size(); sentenceIdx++) {

                Log.d("OutputLSTM", "===================");
                Log.d("OutputLSTM", "" + crfResults.size());
                Log.d("OutputLSTM", "" + crfResults.get(sentenceIdx).size());
                LSTMResult[] lstmResults = factory_lstm.predictWord_Real(crfResults.get(sentenceIdx));

                for (int q = 0; q < lstmResults.length; q++) {
                    Log.d("Hasil " + (q+1), ">>" + lstmResults[q].getResult() + " - " + lstmResults[q].getConfidence());
                }

                lstmFinalResult.add(lstmResults);
            }

            timingLogger.addSplit("(5) LSTM");
            timingLogger.dumpToLog();
            return lstmFinalResult;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
