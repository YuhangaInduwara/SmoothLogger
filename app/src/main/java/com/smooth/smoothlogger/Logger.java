package com.smooth.smoothlogger;

import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private static final String DIRECTORY_NAME = "SmoothLogger";
    private static final StringBuilder logBuilder = new StringBuilder();

    private Logger() {
    }

    public static void appendLog(String text) {
        String currentTime = getCurrentTime();
        String logMessage = "───────────────────────────────\n" + String.format("Date: %-20s \n", currentTime) + String.format("Text: %-20s \n", text) + "───────────────────────────────\n";
        logBuilder.append(logMessage);
        appendToFile(logMessage);
    }

    public static String getLog() {
        return logBuilder.toString();
    }

    private static void appendToFile(String logMessage) {
        try {
            File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File logDirectory = new File(documentsDirectory, DIRECTORY_NAME);
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }

            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            File logFile = new File(logDirectory, "log_" + currentDate + ".txt");

            FileWriter writer = new FileWriter(logFile, true);
            writer.append(logMessage);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
