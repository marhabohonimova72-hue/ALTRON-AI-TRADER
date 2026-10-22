package com.altron.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private LinearLayout root;
    private TextView status;
    private TextView result;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    private final int CYAN = Color.rgb(0, 220, 255);
    private final int BG = Color.rgb(3, 8, 15);
    private final int PANEL = Color.rgb(12, 22, 35);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getWindow().setStatusBarColor(BG);
            getWindow().setNavigationBarColor(BG);
            buildUI();
            initVoice();
        } catch (Exception e) {
            Toast.makeText(
                this,
                "ALTRON ishga tushishida xato yuz berdi",
                Toast.LENGTH_LONG
            ).show();
        }
    }

    private void buildUI() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(22, 30, 22, 30);
        root.setBackgroundColor(BG);

        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("◉ ALTRON");
        title.setTextSize(34);
        title.setTextColor(CYAN);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("AI ASSISTANT  •  DEMO MODE");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(13);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 25);
        root.addView(subtitle);

        status = panel("● SYSTEM ONLINE", CYAN, 16);

        addLabel("VALYUTA JUFTLIGINI TANLANG");

        String[] pairs = {
            "EUR/USD",
            "GBP/USD",
            "USD/JPY",
            "AUD/USD",
            "USD/CAD",
            "USD/CHF",
            "NZD/USD",
            "EUR/GBP"
        };

        Spinner spinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            pairs
        );

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);

        root.addView(spinner);

        result = panel(
            "ALTRON tayyor.\n\nHozircha bozor ma'lumotlari ulanmagan.",
            Color.WHITE,
            18
        );

        Button analyze = makeButton("TAHLIL HOLATINI TEKSHIRISH");

        analyze.setOnClickListener(v -> {
            result.setText(
                "DEMO REJIMI\n\n" +
                "Valyuta juftligi: " +
                spinner.getSelectedItem().toString() +
                "\n\nHozircha real narxlar ulanmagan. " +
                "Shuning uchun BUY yoki SELL tavsiyasi berilmaydi."
            );

            status.setText("● ILOVA ISHLAYAPTI");
        });

        Button speak = makeButton("OVOZDA O'QISH");

        speak.setOnClickListener(v -> {
            if (tts != null && ttsReady) {
                tts.speak(
                    result.getText().toString(),
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "altron_voice"
                );
            } else {
                Toast.makeText(
                    this,
                    "Telefon ovoz tizimi hali tayyor emas",
                    Toast.LENGTH_SHORT
                ).show();
            }
        });

        root.addView(analyze);
        root.addView(speak);

        addLabel("XAVFSIZLIK");

        TextView note = new TextView(this);
        note.setText(
            "Bu demo interfeys. Bozor ma'lumotlari ulanmagan. " +
            "Ilova avtomatik savdo bajarmaydi."
        );
        note.setTextColor(Color.LTGRAY);
        note.setTextSize(13);
        note.setPadding(5, 10, 5, 20);
        root.addView(note);

        setContentView(scroll);
    }

    private TextView panel(String text, int color, int size) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setPadding(18, 20, 18, 20);
        view.setGravity(Gravity.CENTER_VERTICAL);

        GradientDrawable background = new GradientDrawable();
        background.setColor(PANEL);
        background.setCornerRadius(18);
        background.setStroke(1, CYAN);

        view.setBackground(background);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 12, 0, 12);
        root.addView(view, params);

        return view;
    }

    private void addLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(CYAN);
        label.setTextSize(13);
        label.setPadding(3, 18, 3, 8);
        root.addView(label);
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(Color.BLACK);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(CYAN)
        );

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 8, 0, 8);
        root.addView(button, params);

        return button;
    }

    private void initVoice() {
        try {
            tts = new TextToSpeech(this, code -> {
                if (code == TextToSpeech.SUCCESS && tts != null) {
                    int languageResult =
                        tts.setLanguage(new Locale("uz", "UZ"));

                    tts.setSpeechRate(0.90f);

                    ttsReady =
                        languageResult != TextToSpeech.LANG_MISSING_DATA
                        && languageResult != TextToSpeech.LANG_NOT_SUPPORTED;
                }
            });
        } catch (Exception e) {
            ttsReady = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {
            }
            tts = null;
        }

        super.onDestroy();
    }
}
