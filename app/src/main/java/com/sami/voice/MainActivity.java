package com.sami.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final int SPEECH_REQUEST = 100;
    private TextView textView;
    private TextToSpeech tts;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = new TextView(this);
        textView.setText("Sami AI 🤖\n\nMic دبائیں اور بات کریں");
        textView.setTextSize(22);
        textView.setPadding(30, 60, 30, 30);

        Button button = new Button(this);
        button.setText("🎤 Sami سے بات کریں");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 40, 20, 20);
        layout.addView(textView);
        layout.addView(button);

        setContentView(layout);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("ur", "PK"));
            }
        });

        button.setOnClickListener(v -> startListening());
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Sami ko boliye...");

        startActivityForResult(intent, SPEECH_REQUEST);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST &&
                resultCode == RESULT_OK &&
                data != null) {

            ArrayList<String> results =
                    data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String userText = results.get(0);

                textView.setText(
                        "Aap: " + userText +
                        "\n\nSami soch raha hai... 🤖"
                );

                askGemini(userText);
            }
        }
    }

    private void askGemini(String question) {

        executor.execute(() -> {

            try {
                String apiKey = BuildConfig.GEMINI_API_KEY;

                if (apiKey == null || apiKey.trim().isEmpty()) {
                    showAnswer(
                            "Gemini API key abhi configure nahi hui."
                    );
                    return;
                }

                URL url = new URL(
                        "https://sadaqat-sami-2026.kingali127890.workers.dev"
                );

                HttpURLConnection connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                connection.setRequestProperty(
                        "x-goog-api-key",
                        apiKey
                );

                JSONObject part = new JSONObject();
                part.put("text",
                        "You are Sami, a helpful voice assistant. " +
                        "Reply naturally and briefly. " +
                        "Answer in Urdu, Hindi or Sindhi when appropriate. " +
                        "User says: " + question);

                JSONArray parts = new JSONArray();
                parts.put(part);

                JSONObject content = new JSONObject();
                content.put("parts", parts);

                JSONArray contents = new JSONArray();
                contents.put(content);

                JSONObject body = new JSONObject();
                body.put("contents", contents);

                OutputStream output =
                        connection.getOutputStream();

                output.write(
                        body.toString().getBytes("UTF-8")
                );
                output.close();

                int code = connection.getResponseCode();

                InputStream stream =
                        code >= 200 && code < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(stream)
                        );

                StringBuilder response =
                        new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (code < 200 || code >= 300) {
                    showAnswer(
                            "Gemini error: " + code
                    );
                    return;
                }

                JSONObject result =
                        new JSONObject(response.toString());

                String answer =
                        result
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                showAnswer(answer);

            } catch (Exception e) {
                showAnswer(
                        "Sami connection error: " +
                        e.getMessage()
                );
            }
        });
    }

    private void showAnswer(String answer) {

        runOnUiThread(() -> {
            textView.setText(
                    "Sami: " + answer
            );

            speak(answer);
        });
    }

    private void speak(String text) {

        if (tts != null) {
            tts.speak(
                    text,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "SAMI_RESPONSE"
            );
        }
    }

    @Override
    protected void onDestroy() {

        executor.shutdownNow();

        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        super.onDestroy();
    }
}
