package com.github.znitche.SlingshotUpdater.core.api;

import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private final Retrofit retrofit;
    private final SlingshotUpdaterApi apiService;

    public ApiClient(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .build();

        apiService = retrofit.create(SlingshotUpdaterApi.class);
    }

    public Call<RevisionMetadata> getFetchRevisionMetadataCall(String appVersion) {
        return this.apiService.revisionMetadata(appVersion);
    }
}
