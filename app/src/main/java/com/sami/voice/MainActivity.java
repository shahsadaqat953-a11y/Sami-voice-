package com.sami.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView status;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 50, 30, 30);

        TextView title = new TextView(this);
        title.setText("Sami AI 🤖");
        title.setTextSize(30);
        title.setGravity(17);

        status = new TextView(this);
        status.setText("السلام علیکم! میں سامی ہوں۔");
        status.setTextSize(20);
        status.setPadding(0, 40, 0, 40);

        Button mic = new Button(this);
        mic.setText("🎤 بولیں");

        layout.addView(title);
        layout.addView(status);
        layout.addView(mic);

        setContentView(layout);

        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("ur", "PK"));
            }
        });

        mic.setOnClickListener(v -> startListening());
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "سامی کو بولیں");

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            status.setText("Speech recognition دستیاب نہیں ہے۔");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String text = results.get(0);
                status.setText("آپ: " + text);
                tts.speak(
                    "آپ نے کہا: " + text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "sami"
                );
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
