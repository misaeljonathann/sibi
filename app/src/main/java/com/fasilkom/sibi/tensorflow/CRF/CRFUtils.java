package com.fasilkom.sibi.tensorflow.CRF;

import com.fasilkom.sibi.tensorflow.outputs.CRF.CRFResult;

public class CRFUtils {
    private static CRFUtils instance;

    public static CRFUtils getInstance() {
        if (instance == null) {
            instance = new CRFUtils();
        }

        return instance;
    }
    public void filter(CRFResult res) {

//        ArrayList<CRFResult.Package> packages = res.getPackagesOfNonTransition();
//
//        for (CRFResult.Package pack: packages) {
//
//            float[] frameFeature = new float [13 * 1280];
//
//            if (pack.length < 13) {
//
//                // CHECK ANJER
//
//                float[] flattenFrameData = Arrays.copyOfRange(inputData, pack.startIdx * 1280, (pack.startIdx + pack.length) * 1280);
//                float[] lastFrameData = Arrays.copyOfRange(inputData, (pack.startIdx + pack.length - 1) * 1280, (pack.startIdx + pack.length) * 1280);
//
//                System.arraycopy(flattenFrameData, 0, frameFeature, 0, flattenFrameData.length);
//
////                                Log.d("FrameData", "-=-=-=-=- " + flattenFrameData.length);
////                                Log.d("FrameData", "========= " + lastFrameData.length);
////                                Log.d("FrameData", "========= " + lastFrameData[0] + " : " + lastFrameData[1279] + " : "  + flattenFrameData[(pack.startIdx + pack.length) * 1280] + " : "  + flattenFrameData[((pack.startIdx + pack.length) * 1280) + 1]);
//
//
//                for (int frameIdx = pack.length; frameIdx < 13; frameIdx++) {
//
//                    for (int featureIdx = 0; featureIdx < 1280; featureIdx++) {
//
//                        frameFeature[(frameIdx * 1280) + featureIdx] = lastFrameData[featureIdx];
//                    }
//                }
//
//            } else {
//
//                // If Frame > 13
//
//            }
//        }
    }

    public float customMSE(float[] frameFeature, int idx, int featureSize) {

        float sum = 0;

        for (int i = 0; i < featureSize; i++) {
            float valueOfArrA = frameFeature[idx * i];
            float valueOfArrB = frameFeature[(idx+1) * i];

//            Log.d("mse", "diff : " + (valueOfArrA - valueOfArrB));
//            Log.d("mse", "value : " + Math.pow(valueOfArrA - valueOfArrB, 2));
            sum += Math.pow(valueOfArrA - valueOfArrB, 2);
        }

        return sum/featureSize;
    }
}
