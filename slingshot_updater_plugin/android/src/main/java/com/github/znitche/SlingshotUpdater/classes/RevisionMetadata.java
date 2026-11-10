package com.github.znitche.SlingshotUpdater.classes;

public class RevisionMetadata {
    public Boolean exists;
    public String sha256sum;
    public String releaseUrl;
    public String sigUrl;

    public RevisionMetadata(Boolean exists, String sha256sum, String releaseUrl, String sigUrl) {
        this.exists = exists;
        this.sha256sum = sha256sum;
        this.releaseUrl = releaseUrl;
        this.sigUrl = sigUrl;
    }
}
