package com.github.znitche.SlingshotUpdater;

import android.content.Context;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class FilesUtils {
    private final Context context;

    public FilesUtils(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean fileExists(String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }

    public String readFromFile(String fileName) throws IOException {
        try (FileInputStream fis = this.context.openFileInput(fileName)) {
            return new String(fis.readAllBytes(), StandardCharsets.UTF_8);
        }
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
}
