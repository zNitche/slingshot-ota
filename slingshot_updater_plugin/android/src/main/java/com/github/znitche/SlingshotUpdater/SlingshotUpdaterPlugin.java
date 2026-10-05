package com.github.znitche.SlingshotUpdater;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.Logger;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

import java.io.InputStream;

@CapacitorPlugin(name = "SlingshotUpdater")
public class SlingshotUpdaterPlugin extends Plugin {
    private SlingshotConfig pluginConfig = null;

    private final SlingshotUpdater implementation = new SlingshotUpdater();

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
            this.pluginConfig = UpdaterHelpers.loadSlingshotConfig(getContext());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to setup SlingshotUpdater", e);
        }
    }
}
