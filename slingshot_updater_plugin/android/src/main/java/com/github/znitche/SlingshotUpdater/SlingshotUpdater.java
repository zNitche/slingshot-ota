package com.github.znitche.SlingshotUpdater;

import android.content.Context;
import android.util.Log;

import com.github.znitche.SlingshotUpdater.classes.RevisionDirectoryType;
import com.github.znitche.SlingshotUpdater.classes.RevisionMetadata;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

import retrofit2.Call;

public class SlingshotUpdater {
    private Context capContext;
    private SlingshotConfig pluginConfig;

    public void setup(Context capContext, SlingshotConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.capContext = capContext;
    }

    public String get_revision_number() {
        return this.pluginConfig.getUrl();
    }

    private void fetchNewRevision(RevisionMetadata metadata) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Boolean validateNewRevision(RevisionMetadata metadata) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void extractReleaseZip() {
        throw new UnsupportedOperationException("Not implemented yet");
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

    public Boolean checkForUpdate() {
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

        this.fetchNewRevision(metadata);

        if (!this.validateNewRevision(metadata)) {
            Log.d("[SHT]", "revision rsa validation has failed");
            return false;
        }

        Log.d("[SHT]", "processing revision files");

        SlingshotUpdaterPlugin.updaterHelpers.removeRevisionDir(RevisionDirectoryType.CURRENT);
        this.extractReleaseZip();
        SlingshotUpdaterPlugin.updaterHelpers.removeRevisionDir(RevisionDirectoryType.TMP);

        Log.d("[SHT]", "saved new revision, removed tmp dir");

        return false;
    }
}
