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

    public float customMSE(float[] frameFeature, int idx, int featureSize) {

        float sum = 0;

        for (int i = 0; i < featureSize; i++) {
            float valueOfArrA = frameFeature[idx * i];
            float valueOfArrB = frameFeature[(idx+1) * i];

            sum += Math.pow(valueOfArrA - valueOfArrB, 2);
        }

        return sum/featureSize;
    }
}
