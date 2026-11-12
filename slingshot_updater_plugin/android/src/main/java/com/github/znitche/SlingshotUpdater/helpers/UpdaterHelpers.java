package com.github.znitche.SlingshotUpdater.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.github.znitche.SlingshotUpdater.SlingshotUpdaterPlugin;
import com.github.znitche.SlingshotUpdater.classes.RevisionDetails;
import com.github.znitche.SlingshotUpdater.classes.RevisionDirectoryType;
import com.github.znitche.SlingshotUpdater.classes.RevisionFilesURLs;
import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.github.znitche.SlingshotUpdater.core.Consts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;

public class UpdaterHelpers {
    private final Context context;

    public UpdaterHelpers(Context context) {
        this.context = context.getApplicationContext();
    }

    public SlingshotConfig loadSlingshotConfig() throws IOException {
        InputStream fileStream = this.context.getAssets().open("slingshot.json");
        return SlingshotUpdaterPlugin.filesUtils.loadJson(SlingshotConfig.class, fileStream);
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

    public void writeRevisionDetailsToFile(RevisionDetails details) throws IOException {
        SlingshotUpdaterPlugin.filesUtils.dumpJson(details, "revision.json");
    }

    public RevisionDetails readRevisionDetailsFromFile() {
        if (!doesRevisionNumberFileExist()) {
            return null;
        }

        try {
            File file = new File(context.getFilesDir(), "revision.json");
            InputStream fileStream = new FileInputStream(file);

            return SlingshotUpdaterPlugin.filesUtils.loadJson(RevisionDetails.class, fileStream);
        } catch (IOException e) {
            Log.d("[SHT]", "readRevisionDetailsFromFile error: " + e);
            return null;
        }
    }

    public RevisionFilesURLs getRevisionTmpFilesURLs() {
        File tmpDir = this.getRevisionDir(RevisionDirectoryType.TMP);

        return new RevisionFilesURLs(
                new File(tmpDir, "release.zip"),
                new File(tmpDir, "release.zip.sig")
        );
    }

    public void createTmpDir() throws IOException {
        Files.createDirectories(this.getRevisionDir(RevisionDirectoryType.TMP).toPath());
    }

    public File getRevisionDir(RevisionDirectoryType dirType) {
        return new File(this.context.getFilesDir(), dirType.name());
    }

    public void removeRevisionDir(RevisionDirectoryType dirType) {
        FilesUtils.removeDirectory(this.getRevisionDir(dirType));
    }

    public Boolean checkIfNewRevisionShouldBeFetched(RevisionMetadata metadata) {
        if (this.doesRevisionNumberFileExist()) {
            RevisionDetails revisionDetails = this.readRevisionDetailsFromFile();

            return !Objects.equals(revisionDetails.revisionNumber, metadata.sha256sum);
        }

        return Boolean.TRUE;
    }

    public Boolean checkRevisionDirectory() {
        File revisionDirFile = getRevisionDir(RevisionDirectoryType.CURRENT);
        File indexHtmlFile = new File(revisionDirFile.getPath(), "index.html");

        return indexHtmlFile.exists();
    }

    public void writeRevisionNumberToFile(String num) throws Exception {
        String appVersion = this.getAppVersion();

        if (appVersion == null) {
            throw new Exception("couldn't get app version");
        }

        RevisionDetails details = new RevisionDetails(num, appVersion);
        this.writeRevisionDetailsToFile(details);
    }

    public void writeToPreferencesStorage(String key, String details) {
        SharedPreferences prefs = this.context.getSharedPreferences(
                Consts.SLINGSHOT_PREFERENCES_KEY,
                Context.MODE_PRIVATE
        );

        prefs.edit().putString(key, details).apply();
    }

    public String readFromPreferencesStorage(String key) {
        SharedPreferences prefs = this.context.getSharedPreferences(
                Consts.SLINGSHOT_PREFERENCES_KEY,
                Context.MODE_PRIVATE
        );

        return prefs.getString(key, null);
    }
}
