package com.jodelXposed.retrofit;

import com.jodelXposed.retrofit.Response.HooksResponse;
import com.jodelXposed.retrofit.Response.VersionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

@SuppressWarnings("WeakerAccess")
public interface JodelXposedService {
    @GET("master/hooks/{version}/hooks.json")
    Call<HooksResponse> getHooks(@Path("version") int version);

    @GET("master/hooks/current.json")
    Call<VersionResponse> latestVersion();
}
