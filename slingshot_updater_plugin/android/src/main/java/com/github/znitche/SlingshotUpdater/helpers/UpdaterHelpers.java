package com.github.znitche.SlingshotUpdater.helpers;

import android.content.Context;

import com.github.znitche.SlingshotUpdater.SlingshotUpdaterPlugin;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

import java.io.IOException;

public class UpdaterHelpers {
    private final Context context;

    public UpdaterHelpers(Context context) {
        this.context = context.getApplicationContext();
    }

    public SlingshotConfig loadSlingshotConfig() throws IOException {
        return SlingshotUpdaterPlugin.filesUtils.loadJson(SlingshotConfig.class, "slingshot.json");
    }
}
