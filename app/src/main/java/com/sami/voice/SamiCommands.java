package com.sami.voice;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;
import java.util.Locale;

public class SamiCommands {

    public static String handle(Context context, String text) {

        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        String command = text.toLowerCase(Locale.ROOT).trim();

        try {

            // Volume Up
            if (command.contains("آواز بڑھ")
                    || command.contains("آواز زیادہ")
                    || command.contains("volume up")
                    || command.contains("volume increase")) {

                AudioManager audio =
                        (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                if (audio != null) {
                    audio.adjustVolume(
                            AudioManager.ADJUST_RAISE,
                            AudioManager.FLAG_SHOW_UI
                    );
                }

                return "آواز بڑھا دی ہے۔";
            }

            // Volume Down
            if (command.contains("آواز کم")
                    || command.contains("volume down")
                    || command.contains("volume decrease")) {

                AudioManager audio =
                        (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

                if (audio != null) {
                    audio.adjustVolume(
                            AudioManager.ADJUST_LOWER,
                            AudioManager.FLAG_SHOW_UI
                    );
                }

                return "آواز کم کر دی ہے۔";
            }

            // Wi-Fi
            if (command.contains("وائی فائی")
                    || command.contains("wifi")
                    || command.contains("wi-fi")) {

                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "Wi-Fi کی settings کھول دی ہیں۔";
            }

            // Bluetooth
            if (command.contains("بلوٹوتھ")
                    || command.contains("bluetooth")
                    || command.contains("blue tooth")) {

                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "Bluetooth کی settings کھول دی ہیں۔";
            }

            // Phone Settings
            if (command.contains("فون کی سیٹنگ")
                    || command.contains("سیٹنگ کھولو")
                    || command.contains("settings")
                    || command.contains("سیٹنگز")) {

                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "فون کی settings کھول دی ہیں۔";
            }

            // Google
            if (command.equals("گوگل")
                    || command.contains("گوگل کھولو")
                    || command.contains("google")
                    || command.contains("browser")
                    || command.contains("براوزر")) {

                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com")
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "Google کھول دیا ہے۔";
            }

            // Camera
            if (command.contains("کیمرہ")
                    || command.contains("camera")) {

                Intent intent =
                        new Intent("android.media.action.IMAGE_CAPTURE");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "Camera کھول دیا ہے۔";
            }

            // Alarm
            if (command.contains("الارم")
                    || command.contains("alarm")) {

                Intent intent =
                        new Intent("android.intent.action.SHOW_ALARMS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                return "Alarm کھول دیا ہے۔";
            }

            // Open installed app by name
            String appResult = openInstalledApp(context, command);

            if (appResult != null) {
                return appResult;
            }

        } catch (Exception e) {
            return "یہ command اس فون پر دستیاب نہیں ہے۔";
        }

        return null;
    }

    private static String openInstalledApp(Context context, String command) {

        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> apps =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo app : apps) {

            String label;

            try {
                label = pm.getApplicationLabel(app)
                        .toString()
                        .toLowerCase(Locale.ROOT);
            } catch (Exception e) {
                continue;
            }

            boolean requested =
                    command.contains(label)
                    || (label.contains("youtube") &&
                        (command.contains("یوٹیوب") || command.contains("youtube")))
                    || (label.contains("whatsapp") &&
                        (command.contains("واٹس ایپ") || command.contains("whatsapp")))
                    || (label.contains("snapchat") &&
                        (command.contains("سنیپ چیٹ") || command.contains("snapchat")))
                    || (label.contains("chatgpt") &&
                        (command.contains("چیٹ جی پی ٹی") || command.contains("chatgpt")))
                    || (label.contains("play store") &&
                        (command.contains("پلے اسٹور") || command.contains("play store")));

            if (requested) {

                Intent launchIntent =
                        pm.getLaunchIntentForPackage(app.packageName);

                if (launchIntent != null) {

                    launchIntent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    );

                    context.startActivity(launchIntent);

                    return label + " کھول دیا ہے۔";
                }
            }
        }

        return null;
    }
}
