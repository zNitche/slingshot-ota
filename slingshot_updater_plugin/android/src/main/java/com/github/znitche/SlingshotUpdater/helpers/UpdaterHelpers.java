package com.github.znitche.SlingshotUpdater.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.github.znitche.SlingshotUpdater.SlingshotUpdaterPlugin;
import com.github.znitche.SlingshotUpdater.classes.RevisionDetails;
import com.github.znitche.SlingshotUpdater.classes.RevisionDirectoryType;
import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
            return null;
        }
    }

    public Boolean doesRevisionNumberFileExist() {
        return SlingshotUpdaterPlugin.filesUtils.fileExists("revision.json");
    }

    public RevisionDetails readRevisionDetailsFromFile() {
        if (!doesRevisionNumberFileExist()) {
            return null;
        }

        try {
            return SlingshotUpdaterPlugin.filesUtils.loadJson(RevisionDetails.class, "revision.json");
        } catch (IOException e) {
            Log.d("[SHT]", "readRevisionDetailsFromFile error: " + e);
            return null;
        }
    }

    public File getRevisionDir(RevisionDirectoryType dirType) {
        return new File(this.context.getFilesDir(), dirType.name());
    }

    public void removeRevisionDir(RevisionDirectoryType dirType) {
        SlingshotUpdaterPlugin.filesUtils.removeDirectory(this.getRevisionDir(dirType));
    }

    public Boolean checkIfNewRevisionShouldBeFetched(RevisionMetadata metadata) {
        if (this.doesRevisionNumberFileExist()) {
            RevisionDetails revisionDetails = this.readRevisionDetailsFromFile();

            return !Objects.equals(revisionDetails.revisionNumber, metadata.sha256sum);
        }

        return Boolean.TRUE;
    }
}
