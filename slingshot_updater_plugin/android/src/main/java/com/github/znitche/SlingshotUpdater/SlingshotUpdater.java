package com.github.znitche.SlingshotUpdater;

import android.content.Context;

import com.getcapacitor.Logger;
import com.github.znitche.SlingshotUpdater.classes.SlingshotConfig;

public class SlingshotUpdater {
    private Context capContext;
    private SlingshotConfig pluginConfig;

    public void setup(Context capContext, SlingshotConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
        this.capContext = capContext;
    }

    public String get_revision_number() {
        return "123";
    }
}
