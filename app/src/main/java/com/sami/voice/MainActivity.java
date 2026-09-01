package com.sami.voice;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private TextView historyView;
    private TextView pointsView;
    private int points = 1000;
    private final ArrayList<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 40, 30, 30);

        TextView title = new TextView(this);
        title.setText("Sami Analyzer 🤖");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        pointsView = new TextView(this);
        pointsView.setText("Virtual Points: 1000");
        pointsView.setTextSize(20);
        pointsView.setGravity(Gravity.CENTER);
        pointsView.setPadding(0, 25, 0, 25);

        Button dragonButton = new Button(this);
        dragonButton.setText("Dragon");

        Button tigerButton = new Button(this);
        tigerButton.setText("Tiger");

        Button tieButton = new Button(this);
        tieButton.setText("Tie");

        Button clearButton = new Button(this);
        clearButton.setText("Clear History");

        historyView = new TextView(this);
        historyView.setText("History:\\n\\nAbhi koi result nahi.");
        historyView.setTextSize(18);
        historyView.setPadding(0, 25, 0, 0);

        layout.addView(title);
        layout.addView(pointsView);
        layout.addView(dragonButton);
        layout.addView(tigerButton);
        layout.addView(tieButton);
        layout.addView(clearButton);
        layout.addView(historyView);

        setContentView(layout);

        dragonButton.setOnClickListener(v -> addResult("Dragon"));
        tigerButton.setOnClickListener(v -> addResult("Tiger"));
        tieButton.setOnClickListener(v -> addResult("Tie"));

        clearButton.setOnClickListener(v -> {
            history.clear();
            points = 1000;
            updateScreen();
        });
    }

    private void addResult(String result) {
        history.add(result);
        if (history.size() > 20) {
            history.remove(0);
        }

        points += 10;
        updateScreen();
    }

    private void updateScreen() {
        pointsView.setText("Virtual Points: " + points);

        if (history.isEmpty()) {
            historyView.setText("History:\\n\\nAbhi koi result nahi.");
            return;
        }

        StringBuilder text = new StringBuilder("History:\\n\\n");

        for (int i = history.size() - 1; i >= 0; i--) {
            text.append(history.size() - i)
                .append(". ")
                .append(history.get(i))
                .append("\\n");
        }

        historyView.setText(text.toString());
    }
}
