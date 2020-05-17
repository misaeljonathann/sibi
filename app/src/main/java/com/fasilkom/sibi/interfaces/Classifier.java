package com.fasilkom.sibi.interfaces;

import android.content.res.AssetManager;

import com.fasilkom.sibi.tensorflow.outputs.LSTM.LSTMResult;

import java.io.IOException;
import java.util.ArrayList;

public interface Classifier {

    ArrayList<LSTMResult[]> predictWord(AssetManager assetManager) throws IOException;
}
