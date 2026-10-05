package com.github.znitche.SlingshotUpdater;

import android.content.Context;

import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class UpdaterHelpers {
    public static SlingshotConfig loadSlingshotConfig(Context capContext) {
        Gson gson = new Gson();

        try {
            InputStream pluginConfigStream = capContext.getAssets().open("slingshot.json");
            InputStreamReader fileReader =
                    new InputStreamReader(pluginConfigStream, StandardCharsets.UTF_8);

            return gson.fromJson(fileReader, SlingshotConfig.class);
        } catch (Exception e) {
            return null;
        }
    }
}
