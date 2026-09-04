package com.sami.voice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView status;
    private Button toggle;
    private boolean samiOn = false;

    private static final int MIC_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 60, 30, 30);

        TextView title = new TextView(this);
        title.setText("Sami AI");
        title.setTextSize(30);
        title.setGravity(17);

        status = new TextView(this);
        status.setText("Sami بند ہے");
        status.setTextSize(20);
        status.setGravity(17);
        status.setPadding(0, 50, 0, 50);

        toggle = new Button(this);
        toggle.setText("SAMI OFF");
        toggle.setTextSize(20);

        layout.addView(title);
        layout.addView(status);
        layout.addView(toggle);

        setContentView(layout);

        toggle.setOnClickListener(v -> toggleSami());
    }

    private void toggleSami() {
        if (!samiOn) {

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MIC_PERMISSION
                );
                return;
            }

            startSamiService();

        } else {
            stopService(new Intent(this, SamiService.class));

            samiOn = false;
            toggle.setText("SAMI OFF");
            status.setText("Sami بند ہے");
        }
    }

    private void startSamiService() {
        Intent serviceIntent = new Intent(this, SamiService.class);
        startForegroundService(serviceIntent);

        samiOn = true;
        toggle.setText("SAMI ON");
        status.setText("Sami فعال ہے\nاب Sami کو بول سکتے ہیں");
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == MIC_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startSamiService();

        } else {
            status.setText("Microphone permission ضروری ہے");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
