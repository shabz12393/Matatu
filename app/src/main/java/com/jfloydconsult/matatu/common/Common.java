package com.jfloydconsult.matatu.common;

import com.jfloydconsult.matatu.retrofit.IGoogleAPI;
import com.jfloydconsult.matatu.retrofit.RetrofitClient;

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
