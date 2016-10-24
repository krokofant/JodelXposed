package com.jodelXposed.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Admin on 09.10.2016.
 */

public class RetrofitProvider {

    private static JodelXposedService jodelXposedService;

    public static JodelXposedService getJodelXposedService() {
        return jodelXposedService;
    }

    static {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/krokofant/JodelXposed/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        jodelXposedService = retrofit.create(JodelXposedService.class);
    }
}
