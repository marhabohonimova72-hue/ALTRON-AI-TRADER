package com.altron.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout root;
    TextView status, priceText, signalText, detailText;
    Spinner pairSpinner;
    TextToSpeech tts;

    final int CYAN = Color.rgb(0, 220, 255);
    final int BG = Color.rgb(3, 8, 15);
    final int PANEL = Color.rgb(12, 22, 35);

    // OANDA demo API tokenini kodga yozmang.
    // Tokenni xavfsiz server orqali ulash kerak.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("uz", "UZ"));
                tts.setSpeechRate(0.90f);
            }
        });

        buildUI();
    }

    void buildUI() {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 28, 20, 24);
        root.setBackgroundColor(BG);
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("◉ ALTRON");
        title.setTextColor(CYAN);
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        root.addView(title);

        status = addText("AI TRADER  |  DEMO MODE", 14, CYAN);
        status.setGravity(Gravity.CENTER);

        addText("\nVALYUTA JUFTLIGI", 14, Color.LTGRAY);

        String[] pairs = {
            "EUR_USD", "GBP_USD", "USD_JPY",
            "AUD_USD", "USD_CAD", "USD_CHF",
            "NZD_USD", "EUR_GBP"
        };

        pairSpinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            pairs
        );
        pairSpinner.setAdapter(adapter);
        root.addView(pairSpinner);

        priceText = addPanelText("Narx: hali yuklanmagan", 22, Color.WHITE);
        signalText = addPanelText("SIGNAL: KUTILMOQDA", 24, CYAN);
        detailText = addPanelText(
            "OANDA demo ma'lumotlarini ulash uchun himoyalangan API server kerak.",
            15, Color.LTGRAY
        );

        Button analyze = makeButton("BOZORNI TAHLIL QILISH");
        analyze.setOnClickListener(v -> {
            status.setText("Tahlil qilinmoqda...");
            fetchPrice();
        });

        Button speak = makeButton("🔊 OVOZDA O'QISH");
        speak.setOnClickListener(v -> {
            String message = signalText.getText().toString()
                + ". " + detailText.getText().toString();
            if (tts != null) {
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "altron");
            }
        });

        root.addView(analyze);
        root.addView(speak);

        addText("\nESLATMA", 16, CYAN);
        addText(
            "Bu ilova tahliliy yordamchi. Signallar xato bo'lishi mumkin. " +
            "Hozircha haqiqiy yoki avtomatik savdo bajarilmaydi.",
            14, Color.LTGRAY
        );

        setContentView(scroll);
    }

    TextView addText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(4, 12, 4, 12);
        root.addView(t);
        return t;
    }

    TextView addPanelText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(16, 18, 16, 18);
        t.setBackgroundColor(PANEL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            -1, -2
        );
        p.setMargins(0, 10, 0, 10);
        root.addView(t, p);
        return t;
    }

    Button makeButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.BLACK);
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(CYAN)
        );
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            -1, -2
        );
        p.setMargins(0, 8, 0, 8);
        root.addView(b, p);
        return b;
    }

    void fetchPrice() {
        // OANDA tokenini APK ichiga joylashtirmaslik uchun
        // bu versiya hali API ga ulanmaydi.
        status.setText("API SERVER KERAK");
        priceText.setText("Narx: ulanmagan");
        signalText.setText("SIGNAL: MA'LUMOT YO'Q");
        detailText.setText(
            "OANDA demo API ni ulash uchun tokenni himoya qiluvchi " +
            "server kerak. Hozircha haqiqiy narx olinmayapti."
        );
        Toast.makeText(
            this,
            "OANDA ulanishi hali sozlanmagan",
            Toast.LENGTH_LONG
        ).show();
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
