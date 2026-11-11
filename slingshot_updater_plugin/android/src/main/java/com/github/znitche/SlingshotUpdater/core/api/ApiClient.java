package com.github.znitche.SlingshotUpdater.core.api;

import android.content.Context;

import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private Context capContext;
    private final Retrofit retrofit;
    private final SlingshotUpdaterApi apiService;

    public ApiClient(Context capContext, String baseUrl) {
        this.capContext = capContext;

        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(
                        GsonConverterFactory.create()
                )
                .build();

        this.apiService = retrofit.create(SlingshotUpdaterApi.class);
    }

    public Call<RevisionMetadata> getFetchRevisionMetadataCall(String appVersion) {
        return this.apiService.revisionMetadata(appVersion);
    }

    public File downloadRevisionFile(String resourceUrl) throws IOException {
        Response<ResponseBody> response = this.apiService.downloadFile(resourceUrl).execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("downloadRevisionFile has failed: " + response.code());
        }

        File tmpFile = File.createTempFile(
                "slingshot-",
                ".zip",
                this.capContext.getCacheDir()
        );

        try (InputStream input = response.body().byteStream();
             OutputStream output = new FileOutputStream(tmpFile)) {

            input.transferTo(output);
        } catch (IOException e) {
            tmpFile.delete();
            throw e;
        }

        return tmpFile;
    }
}
