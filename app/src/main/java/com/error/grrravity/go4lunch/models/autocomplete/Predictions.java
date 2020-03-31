package com.error.grrravity.go4lunch.models.autocomplete;

import com.google.gson.annotations.SerializedName;
import java.util.List;
@SuppressWarnings({"unused"})
public class Predictions {

    @SerializedName("status")
    private String status;

    @SerializedName("predictions")
    private List<Prediction> predictions;

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Prediction> getPredictions() {
        return this.predictions;
    }

    public void setPredictions(List<Prediction> predictions) {
        this.predictions = predictions;
    }
}