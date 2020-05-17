package com.fasilkom.sibi.tensorflow.outputs.LSTM;

public class LSTMResult {
    private String result;
    private Float confidence;

    public LSTMResult(String result, Float confidence) {
        this.result = result;
        this.confidence = confidence;
    }

    public String getResult() {
        return result;
    }

    public Float getConfidence() {
        return confidence;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setConfidence(Float confidence) {
        this.confidence = confidence;
    }
}
