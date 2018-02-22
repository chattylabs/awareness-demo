package com.chattylabs.module.base;


import android.Manifest;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CsvWriter implements RequiredPermissions {

    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String DATETIME = "DATETIME";

    private Context context;
    private String filename;
    private String delimiter;
    private File directory;
    private BufferedWriter file;
    private String empty;
    private String[] headers;
    private ExecutorService executorService;

    public CsvWriter(Context context) {
        this.context = context;
    }

    @Override
    public String[] requiredPermissions() {
        return new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
    }

    private File getLogFile() {
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String name = filename + ".csv";
        return new File(directory, name);
    }

    public void init(String filename, String delimiter, String empty, String... headers) {
        this.filename = filename;
        this.delimiter = delimiter;
        this.empty = empty;
        this.headers = headers;
        this.executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> init(headers));
    }

    private void init(String... headers) {
        File theFile = getLogFile();
        if (!theFile.exists()) {
            StringBuilder line = new StringBuilder();
            for (String value : headers) {
                line.append(delimiter).append(value);
            }
            line = new StringBuilder(TIMESTAMP + delimiter + DATETIME + line);

            try {
                // Make sure the Pictures directory exists.
                //noinspection ResultOfMethodCallIgnored
                directory.mkdirs();
                file = new BufferedWriter(new FileWriter(theFile));
                file.write(line.toString());
                file.newLine();
                file.flush();
                makeAvailable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (file == null) file = new BufferedWriter(new FileWriter(theFile, true));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SafeVarargs
    public final void write(Pair<String, String>... keyValue) {
        executorService.submit(() -> writeLocal(keyValue));
    }

    @SafeVarargs
    private final void writeLocal(Pair<String, String>... keyValue) {
        if (file == null) {
            return;
        }

        init(filename, delimiter, empty, headers);

        StringBuilder line = new StringBuilder();

        head:
        for (String header : headers) {
            for (Pair<String, String> aKeyValue : keyValue) {
                if (header.equals(aKeyValue.first)) {
                    line.append(delimiter).append(aKeyValue.second);
                    continue head;
                }
            }
            line.append(delimiter).append(empty);
        }

        String timestamp = Long.toString(System.currentTimeMillis());
        String datetime = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        line = new StringBuilder(timestamp + delimiter + datetime + line);

        try {
            file.write(line.toString());
            file.newLine();
            file.flush();
            makeAvailable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (file == null) {
            return;
        }

        try {
            file.close();
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
            makeAvailable();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void makeAvailable() {
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[] { file.toString() }, null, null);
    }
}
