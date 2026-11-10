package com.github.znitche.SlingshotUpdater.classes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SlingshotUpdaterApi {
    @GET("/api/v1/{app_version}/current-revision")
    Call<RevisionMetadata> revisionMetadata(@Path("app_version") String appVersion);
}
