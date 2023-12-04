package com.smooth.smoothlogger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements TextChangeListener{
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 100;
    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    String fileName = "log_" + currentDate + ".txt";
    File logDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SmoothLogger");
    File file = new File(logDirectory, fileName);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTransparentNotificationBar();

        if (!isAccessibilityServiceEnabled()) {
            showEnableAccessibilityDialog();
        }
        if (!isStoragePermissionGranted(this)) {
            requestStoragePermission();
        }

        LoggerAccessibilityService accessibilityService = LoggerAccessibilityService.getInstance();
        if (accessibilityService != null) {
            accessibilityService.setTextChangeListener(this);
            Log.d("HomeActivity", "onCreate: Accessibility service instance set");
        } else {
            Log.e("HomeActivity", "onCreate: Accessibility service instance is null");
        }

        String fileContent = readTextFromFile(file);
        if(fileContent != ""){
            TextView loggedTextView = findViewById(R.id.test_text);
            loggedTextView.setText(fileContent);
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        String serviceName = "com.smooth.smoothlogger/com.smooth.smoothlogger.LoggerAccessibilityService";
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );

            if (settingValue != null) {
                splitter.setString(settingValue.toLowerCase());
                while (splitter.hasNext()) {
                    String accessibilityService = splitter.next().toLowerCase();
                    if (accessibilityService.equals(serviceName.toLowerCase())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void showEnableAccessibilityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enable Accessibility Service");
        builder.setMessage("Please enable the accessibility service in Settings");
        builder.setPositiveButton("Enable", (dialog, which) -> openAccessibilitySettings());
        builder.setNegativeButton("Cancel", (dialog, which) -> finishAffinity());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onTextChanged(String newText) {
        String fileContent = readTextFromFile(file);
        TextView loggedTextView = findViewById(R.id.test_text);
        loggedTextView.setText(fileContent);
    }

    private String readTextFromFile(File file) {
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    protected void setTransparentNotificationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Add this flag
        }
    }

}