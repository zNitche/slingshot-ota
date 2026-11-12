package com.github.znitche.SlingshotUpdater;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.Bridge;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.github.znitche.SlingshotUpdater.classes.AppStateListener;
import com.github.znitche.SlingshotUpdater.classes.RevisionDetails;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;
import com.github.znitche.SlingshotUpdater.core.api.ApiClient;
import com.github.znitche.SlingshotUpdater.helpers.FilesUtils;
import com.github.znitche.SlingshotUpdater.helpers.UpdaterHelpers;
import com.github.znitche.SlingshotUpdater.core.AppStateManager;
import com.github.znitche.SlingshotUpdater.core.MainLoopPoolingTask;

/**
 * Please read the Capacitor Android Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/android
 */
@CapacitorPlugin(name = "SlingshotUpdater")
public class SlingshotUpdaterPlugin extends Plugin {
    private final Bridge capBridge = getBridge();
    private final SlingshotUpdater implementation = new SlingshotUpdater();
    private MainLoopPoolingTask mainLoopPoolingTask = null;
    private AppStateManager appStateManager;
    private SlingshotConfig pluginConfig = null;
    public static ApiClient apiClient;
    public static FilesUtils filesUtils;
    public static UpdaterHelpers updaterHelpers;

    @PluginMethod
    public void get_revision_number(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("value", implementation.get_revision_number());

        call.resolve(ret);
    }

    @Override
    public void load() {
        super.load();

        try {
            Context capContext = getContext();

            filesUtils = new FilesUtils(capContext);
            updaterHelpers = new UpdaterHelpers(capContext);

            this.pluginConfig = updaterHelpers.loadSlingshotConfig();
            this.implementation.setup(capContext, pluginConfig);

            apiClient = new ApiClient(capContext, this.pluginConfig.getUrl());

            this.setupAppStateNotifications();

            this.updaterCleanup();
            this.runUpdaterMainLoop();

        } catch (Exception e) {
            Log.e("[SHT]", e.toString());
            throw new RuntimeException("Failed to setup SlingshotUpdater", e);
        }
    }

    private void setupAppStateNotifications() {
        this.appStateManager = new AppStateManager(new AppStateListener() {
            @Override
            public void onForeground() {
                Log.d("[SHT]", "app enters foreground");
                runUpdaterMainLoop();
            }

            @Override
            public void onBackground() {
                Log.d("[SHT]", "app enters background");
                updaterCleanup();
            }
        });
    }

    private void updaterCleanup() {
        if (this.mainLoopPoolingTask != null) {
            this.mainLoopPoolingTask.stop();
        }

        this.mainLoopPoolingTask = null;
    }

    private void runUpdaterMainLoop() {
        if (this.appStateManager.getIsInBackground()) {
            return;
        }

        if (this.mainLoopPoolingTask != null) {
            return;
        }

        this.mainLoopPoolingTask = new MainLoopPoolingTask(
                () -> {
                    try {
                        Log.d("[SHT]", "mainloopPoolingTask start");

                        this.validateRevisionForCurrentVersion();

                        Boolean gotNewRevision = this.implementation.checkForUpdate();
                        Log.d("[SHT]", "got new revision ? : " + gotNewRevision);

                        if (gotNewRevision) {
                            Log.d("[SHT]", "setting up new revision/release");
                            this.setSlingshotRevision();
                        }
                    } catch (Exception e) {
                        Log.d("[SHT]", "mainLoopPoolingTask error: " + e);
                    } finally {
                        Log.d("[SHT]", "mainloopPoolingTask done");
                    }
                },
                this.pluginConfig.getIsPeriodicUpdater(),
                this.pluginConfig.getUpdaterTickInterval()
        );

        this.mainLoopPoolingTask.start();
    }

    private void reloadWebView() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private void validateRevisionForCurrentVersion() {
        return;
    }

    private void setSlingshotRevision() throws Exception {
        if (!updaterHelpers.doesRevisionNumberFileExist()) {
            return;
        }

        RevisionDetails revisionDetails = updaterHelpers.readRevisionDetailsFromFile();

        if (!updaterHelpers.checkRevisionDirectory()) {
            throw new Exception("index.html of current revision doesn't exist");
        }

        return;
//        capBridge.setServerBasePath();
    }
}
