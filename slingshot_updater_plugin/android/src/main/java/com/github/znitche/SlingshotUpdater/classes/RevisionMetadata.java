package com.github.znitche.SlingshotUpdater.classes;

public class RevisionMetadata {
    public Boolean exists;
    public String sha256sum;
    public String release_url;
    public String sig_url;

    public RevisionMetadata(Boolean exists, String sha256sum, String releaseUrl, String sigUrl) {
        this.exists = exists;
        this.sha256sum = sha256sum;
        this.release_url = releaseUrl;
        this.sig_url = sigUrl;
    }
}
