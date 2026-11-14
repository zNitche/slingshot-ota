package com.github.znitche.SlingshotUpdater;

import android.content.Context;

import com.getcapacitor.BridgeActivity;
import com.getcapacitor.ServerPath;
import com.github.znitche.SlingshotUpdater.classes.RevisionDirectoryType;
import com.github.znitche.SlingshotUpdater.core.Consts;
import com.github.znitche.SlingshotUpdater.helpers.UpdaterHelpers;

import java.io.File;

public class SlingshotCustomBridgeActivity extends BridgeActivity {
    @Override
    protected void load() {
        super.load();

        Context context = bridge.getContext();
        UpdaterHelpers updaterHelpers = new UpdaterHelpers(context);

        updaterHelpers.writeToPreferencesStorage(Consts.SLINGSHOT_CAPACITOR_DEFAULT_SERVER_PATH_KEY, bridge.getServerBasePath());

        Boolean revisionExists = updaterHelpers.checkRevisionDirectory();

        if (revisionExists) {
            File revisionDir = SlingshotUpdaterPlugin.updaterHelpers.getRevisionDir(RevisionDirectoryType.CURRENT);
            ServerPath serverPath = new ServerPath(ServerPath.PathType.BASE_PATH, revisionDir.getPath());

            bridgeBuilder.setServerPath(serverPath);
            bridge.setServerBasePath(serverPath.getPath());
        }
    }
}
