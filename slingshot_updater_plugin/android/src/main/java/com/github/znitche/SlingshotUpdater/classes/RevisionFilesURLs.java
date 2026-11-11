package com.github.znitche.SlingshotUpdater.classes;

import java.io.File;

public class RevisionFilesURLs {
    public File zip;
    public File signature;

    public RevisionFilesURLs(File zip, File signature) {
        this.zip = zip;
        this.signature = signature;
    }
}
