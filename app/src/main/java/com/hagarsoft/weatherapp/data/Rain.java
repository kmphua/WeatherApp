package com.hagarsoft.weatherapp.data;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("1h")
    public float onehour;
    @SerializedName("3h")
    public float threehour;
}