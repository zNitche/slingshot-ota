package com.github.znitche.SlingshotUpdater;

import android.content.Context;
import android.util.Log;

import com.github.znitche.SlingshotUpdater.classes.RevisionDetails;
import com.github.znitche.SlingshotUpdater.classes.RevisionDirectoryType;
import com.github.znitche.SlingshotUpdater.classes.RevisionFilesURLs;
import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.github.znitche.SlingshotUpdater.core.Crypto;
import com.github.znitche.SlingshotUpdater.helpers.FilesUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import retrofit2.Call;

public class SlingshotUpdater {
    private Context capContext;
    private SlingshotConfig pluginConfig;

    public void setup(Context capContext, SlingshotConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.capContext = capContext;
    }

    public String get_revision_number() {
        RevisionDetails revisionDetails = SlingshotUpdaterPlugin.updaterHelpers.readRevisionDetailsFromFile();
        return revisionDetails.revisionNumber;
    }

    private Boolean fetchNewRevision(RevisionMetadata metadata) {
        RevisionFilesURLs tmpFilesURLs = SlingshotUpdaterPlugin.updaterHelpers.getRevisionTmpFilesURLs();

        try {
            File tmpZip = SlingshotUpdaterPlugin.apiClient.downloadRevisionFile(metadata.release_url);
            File tmpSig = SlingshotUpdaterPlugin.apiClient.downloadRevisionFile(metadata.sig_url);

            SlingshotUpdaterPlugin.updaterHelpers.createTmpDir();

            Files.move(tmpZip.toPath(), tmpFilesURLs.zip.toPath());
            Files.move(tmpSig.toPath(), tmpFilesURLs.signature.toPath());

            return true;
        } catch (IOException e) {
            Log.d("[SHT]", "fetchNewRevision error " + e);
            return false;
        }
    }

    private Boolean validateNewRevision(RevisionMetadata metadata) {
        RevisionFilesURLs tmpFilesURLs = SlingshotUpdaterPlugin.updaterHelpers.getRevisionTmpFilesURLs();

        InputStream pubKeyInputStream;

        try {
            pubKeyInputStream = this.capContext.getAssets().open("slingshot.pem");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Boolean signatureValidationResult = Crypto.validateFile(tmpFilesURLs.zip, pubKeyInputStream, tmpFilesURLs.signature);
        Boolean hashValidationResult = Crypto.validateFileSHA256(tmpFilesURLs.zip, metadata.sha256sum);

        return signatureValidationResult && hashValidationResult;
    }

    private void extractReleaseZip() throws IOException {
        RevisionFilesURLs filesPaths = SlingshotUpdaterPlugin.updaterHelpers.getRevisionTmpFilesURLs();
        File targetDir = SlingshotUpdaterPlugin.updaterHelpers.getRevisionDir(RevisionDirectoryType.CURRENT);

        FilesUtils.unzipArchive(filesPaths.zip, targetDir);
    }

    private RevisionMetadata getRevisionMetadata() {
        String appVersion = SlingshotUpdaterPlugin.updaterHelpers.getAppVersion();
        Call<RevisionMetadata> getFetchRevisionMetadataCall = SlingshotUpdaterPlugin.apiClient.getFetchRevisionMetadataCall(appVersion);

        try {
            return getFetchRevisionMetadataCall.execute().body();
        } catch (Exception e) {
            Log.d("[SHT]", "failed to fetch revision metadata " + e);
        }

        return null;
    }

    public Boolean checkForUpdate() throws Exception {
        RevisionMetadata metadata = this.getRevisionMetadata();

        if (metadata == null) {
            return  false;
        }

        Log.d("[SHT]", "got revision metadata");

        Boolean shouldNewRevisionBeFetched = SlingshotUpdaterPlugin.updaterHelpers.checkIfNewRevisionShouldBeFetched(metadata);

        if (!shouldNewRevisionBeFetched) {
            return false;
        }

        Log.d("[SHT]", "fetching new revision");

        SlingshotUpdaterPlugin.updaterHelpers.removeRevisionDir(RevisionDirectoryType.TMP);
        Boolean fetchNewRevisionStatus = this.fetchNewRevision(metadata);

        if (!fetchNewRevisionStatus) {
            Log.d("[SHT]", "revision fetch has failed");
            return false;
        }

        if (!this.validateNewRevision(metadata)) {
            Log.d("[SHT]", "revision rsa validation has failed");
            return false;
        }

        Log.d("[SHT]", "processing revision files");

        SlingshotUpdaterPlugin.updaterHelpers.removeRevisionDir(RevisionDirectoryType.CURRENT);
        this.extractReleaseZip();
        SlingshotUpdaterPlugin.updaterHelpers.removeRevisionDir(RevisionDirectoryType.TMP);

        Log.d("[SHT]", "saved new revision, removed tmp dir");
        SlingshotUpdaterPlugin.updaterHelpers.writeRevisionNumberToFile(metadata.sha256sum);

        return true;
    }
}
