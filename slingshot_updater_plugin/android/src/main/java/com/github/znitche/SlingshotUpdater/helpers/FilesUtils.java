package com.github.znitche.SlingshotUpdater.helpers;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FilesUtils {
    private final Context context;

    public FilesUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void removeDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    removeDirectory(file);
                } else {
                    file.delete();
                }
            }
        }

        directory.delete();
    }

    public static String readFromFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return new String(fis.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public boolean fileExists(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public void writeToFile(String fileName, String data) throws IOException {
        try (FileOutputStream fos = this.context.openFileOutput(
                fileName, Context.MODE_PRIVATE)) {

            fos.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    public <T> void dumpJson(T obj, String fileName) throws IOException {
        Gson gson = new Gson();
        String dump = gson.toJson(obj);

        writeToFile(dump, fileName);
    }

    public <T> T loadJson(Class<T> targetClass, String fileName) throws IOException {
        Gson gson = new Gson();

        InputStream fileStream = this.context.getAssets().open(fileName);
        InputStreamReader fileReader =
                new InputStreamReader(fileStream, StandardCharsets.UTF_8);

        return gson.fromJson(fileReader, targetClass);
    }

    public static String calculateSha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream input = new DigestInputStream(
                new FileInputStream(file), digest)) {

            byte[] buffer = new byte[8192];

            while (input.read(buffer) != -1) {
                // digest updated after read(buffer) is called
            }
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : digest.digest()) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    public static void unzipArchive(File zipFile, File target) throws IOException {
        try (ZipFile zip = new ZipFile(zipFile)) {
            var entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File file = new File(target, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();

                    try (InputStream in = zip.getInputStream(entry);
                         OutputStream out = new FileOutputStream(file)) {
                        in.transferTo(out);
                    }
                }
            }
        }
    }
}
