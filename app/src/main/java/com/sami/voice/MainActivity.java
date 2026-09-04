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
    private static final String SAMI_URL =
            "https://sami-ai.kingali127890.workers.dev";

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
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "ur-PK"
        );

        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "سامی کو بولیں"
        );

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            status.setText("Speech recognition دستیاب نہیں ہے۔");
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100
                && resultCode == RESULT_OK
                && data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS
                    );

            if (results != null && !results.isEmpty()) {

                String text = results.get(0);

                String commandAnswer =
                        SamiCommands.handle(this, text);

                if (commandAnswer != null) {
                    status.setText("Sami: " + commandAnswer);

                    if (tts != null) {
                        tts.speak(
                                commandAnswer,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "sami"
                        );
                    }

                    return;
                }

                status.setText("Sami سوچ رہا ہے...");
                askSami(text);
            }
        }
    }

    private void askSami(String message) {
        new Thread(() -> {
            try {
                java.net.URL url =
                        new java.net.URL(SAMI_URL);

                java.net.HttpURLConnection c =
                        (java.net.HttpURLConnection)
                                url.openConnection();

                c.setRequestMethod("POST");
                c.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );
                c.setDoOutput(true);

                String body =
                        "{\"message\":\""
                                + message
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                + "\"}";

                try (java.io.OutputStream os =
                             c.getOutputStream()) {

                    os.write(
                            body.getBytes(
                                    java.nio.charset.StandardCharsets.UTF_8
                            )
                    );
                }

                java.io.InputStream is =
                        c.getResponseCode() >= 400
                                ? c.getErrorStream()
                                : c.getInputStream();

                java.util.Scanner sc =
                        new java.util.Scanner(
                                is,
                                "UTF-8"
                        ).useDelimiter("\\A");

                String response =
                        sc.hasNext() ? sc.next() : "";

                sc.close();

                String answer =
                        response.contains("\"error\"") ? "ابھی AI کی حد پوری ہو گئی ہے، تھوڑی دیر بعد دوبارہ کوشش کریں۔" :
                        response.replaceAll(
                                ".*\"answer\"\\s*:\\s*\"([^\"]*)\".*",
                                "$1"
                        )
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");

                final String finalAnswer = (answer.contains("quota") || answer.contains("Quota") || answer.contains("exceeded")) ? "ابھی AI کی حد پوری ہو گئی ہے، تھوڑی دیر بعد دوبارہ کوشش کریں۔" : answer;

                runOnUiThread(() -> {
                    status.setText("Sami: " + finalAnswer);

                    if (tts != null) {
                        tts.speak(
                                finalAnswer,
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                "sami"
                        );
                    }
                });

                c.disconnect();

            } catch (Exception e) {
                runOnUiThread(() ->
                        status.setText(
                                "Sami server سے connect نہیں ہو سکا۔"
                        )
                );
            }
        }).start();
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
