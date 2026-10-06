package com.github.znitche.SlingshotUpdater;

import android.content.Context;

import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class UpdaterHelpers {
    public static SlingshotConfig loadSlingshotConfig(Context capContext) throws IOException {
        Gson gson = new Gson();

        InputStream pluginConfigStream = capContext.getAssets().open("slingshot.json");
        InputStreamReader fileReader =
                new InputStreamReader(pluginConfigStream, StandardCharsets.UTF_8);

        return gson.fromJson(fileReader, SlingshotConfig.class);
    }
}
