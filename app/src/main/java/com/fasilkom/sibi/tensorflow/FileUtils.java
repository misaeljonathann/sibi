package com.fasilkom.sibi.tensorflow;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileUtils {

    private static FileUtils instance;

    public static FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }

        return instance;
    }


    public List<String> getLabels(AssetManager assetManager, String fileName) throws IOException {

        ArrayList<String> labels = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));

        String line;

        while ((line = br.readLine()) != null) {
            labels.add(line.split(",")[0]); // CHECK
        }

        br.close();

        return labels;
    }

    public float[] getInputData(AssetManager assetManager, String fileName) throws IOException {

        // Sub
        BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));

        // Original
//        InputStream inputStream = contentResolver.openInputStream(uri);
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line;

        ArrayList<Float> token = new ArrayList<>();

        while ((line = br.readLine()) != null) {

            token.add(Float.valueOf(line));
        }

        return convertFloats(token);
    }

    public static float[] convertFloats(List<Float> floats) //CHECK
    {
        float[] ret = new float[floats.size()];
        Iterator<Float> iterator = floats.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().floatValue();
        }
        return ret;
    }
}
