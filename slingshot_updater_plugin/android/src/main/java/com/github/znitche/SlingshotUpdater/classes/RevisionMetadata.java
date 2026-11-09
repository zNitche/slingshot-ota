package com.github.znitche.SlingshotUpdater.classes;

public class RevisionMetadata {
    String url;
    String releaseUrl;
    String sigUrl;

    public RevisionMetadata(String url, String releaseUrl, String sigUrl) {
        this.url = url;
        this.releaseUrl = releaseUrl;
        this.sigUrl = sigUrl;
    }
}
