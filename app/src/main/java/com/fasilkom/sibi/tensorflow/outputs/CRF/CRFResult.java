package com.fasilkom.sibi.tensorflow.outputs.CRF;

import android.util.Log;

import java.util.ArrayList;

// INI BELOM MODULAR BELOM MVVM

public class CRFResult {

    public class Package {
        public final int startIdx;
        public final int length;

        public Package(int startIdx, int length) {
            this.startIdx = startIdx;
            this.length = length;
        }
    }
    private float[] sequence;

    public float[] getSequence() {
        return sequence;
    }

    public void setSequence(float[] sequence) {
        this.sequence = sequence;
    }

    public CRFResult(float[] sequence) {
        this.sequence = sequence;
    }

    public float[] getRoundedSequence() {

        float[] res = new float[480];

        int idx = 0;

        String logOutput = "";

        for (int i = 0; i < (sequence.length - 1); i += 4) {
//            Log.d("InputData", "Seq Idx: " + idx);
//            Log.d("Length-nya", i + " == " + sequence[i] + " : " + sequence[i+1]);
//            Log.d("Length-nya", i + " == " + sequence[i] + " : " + sequence[i+1] + " : " + sequence[i+2] + " : " + sequence[i+3]);

//            res[idx++] = (sequence[i] < sequence[i+1]) ? 1 : 0;
            res[idx++] = (sequence[i] < sequence[i+3]) ? 3 : 0;

            logOutput += (int) res[idx-1] + " ";

        }

        for (int i = 0; i < res.length; i++) {

//            Log.d("CurrVal", "" + i + " : " + res[i]);
        }

//        Log.d("OutputCRF", logOutput);
        return res;
    }

    public ArrayList<Package> getPackagesOfNonTransition() {

        ArrayList<Package> packages = new ArrayList<>();
        final int THRESHOLD = 5;

        float targetVal = 3; // 1 or 3
        int streak = 0;

        float[] roundedSequence = getRoundedSequence();

        for (int i = 0; i < roundedSequence.length; i++) {

            if (roundedSequence[i] == targetVal) {
                streak += 1;

            } else {

                if (streak > THRESHOLD) {

                    packages.add(new Package(i - streak + 1, streak));
                }

                streak = 1;
            }
        }
        Log.d("PackSize", "Package Size: " + packages.size());

        for (Package item: packages) {
            Log.d("CurrVal", "" + item.startIdx);
        }

        return packages;
    }
}
