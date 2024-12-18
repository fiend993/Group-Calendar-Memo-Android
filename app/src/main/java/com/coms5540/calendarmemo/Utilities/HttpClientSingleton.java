package com.coms5540.calendarmemo.Utilities;

import okhttp3.OkHttpClient;

//An OkHttpClient for the application, avoid the resource waste for
//create new client for each http request
public class HttpClientSingleton {
    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static OkHttpClient getInstance(){
        return CLIENT;
    }
}
