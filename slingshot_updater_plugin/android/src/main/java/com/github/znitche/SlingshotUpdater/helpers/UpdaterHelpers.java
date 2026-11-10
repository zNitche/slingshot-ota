package com.github.znitche.SlingshotUpdater.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

    public String getAppVersion() {
        try {
            PackageInfo info = this.context.getPackageManager()
                    .getPackageInfo(this.context.getPackageName(), 0);

            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
