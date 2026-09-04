package com.sami.voice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import java.util.List;
import java.util.Locale;

public class SamiService extends Service {

    private static final String CHANNEL_ID = "sami_service";
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private Handler handler;
    private boolean ttsReady = false;
    private boolean waitingForCommand = false;
    private boolean destroyed = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Sami AI", NotificationManager.IMPORTANCE_LOW);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if (manager != null) manager.createNotificationChannel(channel);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true;
                int result = textToSpeech.setLanguage(new Locale("ur", "PK"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
                if (!destroyed) handler.postDelayed(this::startListening, 700);
            }
        });

        setupSpeechRecognizer();
    }

    private void setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {
                if (!destroyed) handler.postDelayed(SamiService.this::startListening, 800);
            }

            @Override
            public void onResults(Bundle results) {
                List<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches == null || matches.isEmpty()) {
                    restartListening();
                    return;
                }

                String text = matches.get(0).toLowerCase(Locale.ROOT);
                if (!waitingForCommand && isWakeWord(text)) {
                    waitingForCommand = true;
                    speak("جی بولیں صداقت علی شاہ");
                } else if (waitingForCommand) {
                    waitingForCommand = false;
                    speak("جی، آپ نے کہا: " + matches.get(0));
                } else {
                    restartListening();
                }
            }

            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }
    private boolean isWakeWord(String text) {
        return text.contains("sami")
                || text.contains("samee")
                || text.contains("samy")
                || text.contains("semi")
                || text.contains("سامی");
    }

    private void startListening() {
        if (destroyed || speechRecognizer == null || !ttsReady) return;

        try {
            speechRecognizer.cancel();
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            restartListening();
        }
    }

    private void restartListening() {
        if (!destroyed) handler.postDelayed(this::startListening, 800);
    }

    private void speak(String text) {
        if (destroyed || textToSpeech == null || !ttsReady) return;
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "SAMI_RESPONSE");
        handler.postDelayed(this::startListening, 2200);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Sami AI")
                .setContentText("Sami آواز سننے کے لیے تیار ہے")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();

        startForeground(1001, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyed = true;
        if (handler != null) handler.removeCallbacksAndMessages(null);
        if (speechRecognizer != null) {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
