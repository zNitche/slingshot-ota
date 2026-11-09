package com.github.znitche.SlingshotUpdater;

import android.content.Context;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.github.znitche.SlingshotUpdater.classes.AppStateListener;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

/**
 * Please read the Capacitor Android Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/android
 */
@CapacitorPlugin(name = "SlingshotUpdater")
public class SlingshotUpdaterPlugin extends Plugin {
    private final SlingshotUpdater implementation = new SlingshotUpdater();
    private AppStateManager appStateManager;
    private SlingshotConfig pluginConfig = null;
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

            this.setupAppStateNotifications();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup SlingshotUpdater", e);
        }
    }

    private void setupAppStateNotifications() {
        this.appStateManager = new AppStateManager(new AppStateListener() {
            @Override
            public void onForeground() {
                Log.d("SlingshotUpdater", "app enters foreground");
                runUpdaterMainloop();
            }

            @Override
            public void onBackground() {
                Log.d("SlingshotUpdater", "app enters background");
                updaterCleanup();
            }
        });
    }

    private void updaterCleanup() {

    }

    private void runUpdaterMainloop() {

    }

    private void reloadWebView() {

    }

    private void validateRevisionForCurrentVersion() {

    }

    private void setSlingshotRevision() {

    }
}
