package com.github.znitche.SlingshotUpdater;

import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class UpdaterHelpers {
    public static SlingshotConfig loadSlingshotConfig(InputStream fileStream) {
        Gson gson = new Gson();

        try (InputStreamReader fileReader =
                     new InputStreamReader(fileStream, StandardCharsets.UTF_8)) {
            return gson.fromJson(fileReader, SlingshotConfig.class);
        } catch (Exception e) {
            return null;
        }
    }
}
