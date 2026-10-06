package com.github.znitche.SlingshotUpdater.classes;

public class SlingshotConfig {
    private String url;
    private Boolean periodicUpdater;
    private Integer updaterTickInterval;
    private Boolean reloadWebviewOnNewRelease;

    public String getUrl() {
        return url;
    }

    public Boolean getIsPeriodicUpdater() {
        return periodicUpdater;
    }

    public Integer getUpdaterTickInterval() {
        return updaterTickInterval;
    }

    public Boolean getReloadWebviewOnNewRelease() {
        return reloadWebviewOnNewRelease;
    }
}
