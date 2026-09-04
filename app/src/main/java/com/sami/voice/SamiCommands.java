package com.sami.voice;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.Settings;

public class SamiCommands{

    public static String handle(Activity activity, String text){
        String command = text.toLowerCase();

        try{
            if (command.contains("آواز بڑھ") || command.contains("volume up")
                    || command.contains("volume increase")){
                AudioManager audio = (AudioManager)
                        activity.getSystemService(Activity.AUDIO_SERVICE);
                audio.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return "آواز بڑھا دی ہے۔";
            }

            if (command.contains("آواز کم") || command.contains("volume down")
                    || command.contains("volume decrease")){
                AudioManager audio = (AudioManager)
                        activity.getSystemService(Activity.AUDIO_SERVICE);
                audio.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return "آواز کم کر دی ہے۔";
            }

            if (command.contains("وائی فائی") || command.contains("wifi") || command.contains("wi-fi")){
                activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                return "Wi-Fi کی settings کھول دی ہیں۔";
            }

            if (command.contains("بلوٹوتھ") || command.contains("bluetooth") || command.contains("blue tooth")){
                activity.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                return "Bluetooth کی settings کھول دی ہیں۔";
            }

            if (command.contains("فون کی سیٹنگ") || command.contains("settings")
                    || command.contains("سیٹنگ کھولو")){
                activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                return "فون کی settings کھول دی ہیں۔";
            }

        }catch (Exception e){
            return "یہ command اس فون پر دستیاب نہیں ہے۔";
        }

        if (command.contains("گوگل") || command.contains("browser") || command.contains("براوزر")) { activity.startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com"))); return "Google کھول دیا ہے۔"; }

        if (command.contains("کیمرہ") || command.contains("camera")) { activity.startActivity(new Intent("android.media.action.IMAGE_CAPTURE")); return "Camera کھول دیا ہے۔"; }

        if (command.contains("الارم") || command.contains("alarm")) { activity.startActivity(new Intent("android.intent.action.SHOW_ALARMS")); return "Alarm کھول دیا ہے۔"; }

        return null;
    }
}
