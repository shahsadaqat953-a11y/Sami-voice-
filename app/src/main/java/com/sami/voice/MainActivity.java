package com.sami.voice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView status;
    private Button toggle;
    private boolean samiOn = false;

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
            Intent serviceIntent = new Intent(this, SamiService.class);
            startForegroundService(serviceIntent);

            samiOn = true;
            toggle.setText("SAMI ON");
            status.setText("Sami فعال ہے\nاب Sami کو بول سکتے ہیں");
        } else {
            Intent serviceIntent = new Intent(this, SamiService.class);
            stopService(serviceIntent);

            samiOn = false;
            toggle.setText("SAMI OFF");
            status.setText("Sami بند ہے");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
