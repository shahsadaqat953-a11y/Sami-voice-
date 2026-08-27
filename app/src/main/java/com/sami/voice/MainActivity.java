package com.sami.voice;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int SPEECH_REQUEST = 100;
    private TextView textView;
    private TextToSpeech tts;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST &&
            resultCode == RESULT_OK &&
            data != null) {

            ArrayList<String> results =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                String userText = results.get(0);

                textView.setText("Aap: " + userText + "\n\nSami soch raha hai... 🤖");

                String answer = getAnswer(userText);
                textView.setText("Aap: " + userText + "\n\nSami: " + answer);
                speak(answer);
            }
        }
    }


    private String getAnswer(String question) {
        String q = question.toLowerCase(Locale.ROOT);

        if (q.contains("hello") || q.contains("salam") || q.contains("اسلام")) {
            return "وعلیکم السلام! میں Sami ہوں۔ آپ کیا پوچھنا چاہتے ہیں؟";
        }

        if (q.contains("tum kon") || q.contains("who are you")) {
            return "میں Sami ہوں، آپ کا voice assistant۔";
        }

        if (q.contains("time") || q.contains("وقت")) {
            return "میں ابھی وقت کی بنیادی معلومات دے سکتا ہوں، لیکن درست موجودہ وقت کے لیے clock app استعمال کریں۔";
        }

        if (q.contains("thank") || q.contains("شکریہ")) {
            return "خوش رہیں! میں آپ کی مدد کے لیے حاضر ہوں۔";
        }

        return "میں نے آپ کی بات سنی: " + question +
               ". اس سوال کا intelligent جواب دینے کے لیے مجھے AI model سے connect کرنا ہوگا۔";
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SAMI_RESPONSE");
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
