package com.error.grrravity.go4lunch.models.autocomplete;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
@SuppressWarnings({"unused"})
class MatchedSubstring implements Serializable {

    @SerializedName("length")
    private int length;

    @SerializedName("offset")
    private int offset;

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}