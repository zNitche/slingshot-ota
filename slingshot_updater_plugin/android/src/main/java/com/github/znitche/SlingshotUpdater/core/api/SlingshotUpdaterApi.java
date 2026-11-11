package com.github.znitche.SlingshotUpdater.core.api;

import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface SlingshotUpdaterApi {
    @GET("/api/v1/{app_version}/current-revision")
    Call<RevisionMetadata> revisionMetadata(@Path("app_version") String appVersion);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String targetUrl);
}
