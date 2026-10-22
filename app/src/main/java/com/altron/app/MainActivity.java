package com.altron.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.speech.RecognizerIntent;
import java.util.Locale;

public class MainActivity extends Activity {

LinearLayout layout;
TextView status, result;
EditText input;
TextToSpeech tts;

int cyan = Color.rgb(0, 220, 255);

@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().setStatusBarColor(Color.rgb(3, 8, 15));
    getWindow().setNavigationBarColor(Color.BLACK);

    layout = new LinearLayout(this);
    layout.setOrientation(LinearLayout.VERTICAL);
    layout.setGravity(Gravity.CENTER);
    layout.setPadding(24, 30, 24, 24);
    layout.setBackgroundColor(Color.rgb(3, 8, 15));

    TextView title = new TextView(this);
    title.setText("A L T R O N");
    title.setTextSize(34);
    title.setTextColor(cyan);
    title.setGravity(Gravity.CENTER);
    layout.addView(title);

    TextView orb = new TextView(this);
    orb.setText("◉");
    orb.setTextSize(100);
    orb.setTextColor(cyan);
    orb.setGravity(Gravity.CENTER);
    layout.addView(orb);

    status = new TextView(this);
    status.setText("● ALTRON SYSTEM READY");
    status.setTextColor(Color.GREEN);
    status.setTextSize(16);
    status.setGravity(Gravity.CENTER);
    layout.addView(status);

    result = new TextView(this);
    result.setText("Assalomu alaykum! Men ALTRON AI.");
    result.setTextColor(Color.WHITE);
    result.setTextSize(18);
    result.setGravity(Gravity.CENTER);
    result.setPadding(0, 20, 0, 20);
    layout.addView(result);

    input = new EditText(this);
    input.setSingleLine(false);
    input.setHint("Savolingizni yozing...");
    input.setHintTextColor(Color.GRAY);
    input.setTextColor(Color.WHITE);
    layout.addView(input);

    Button send = new Button(this);
    send.setText("AI GA YUBORISH");
    layout.addView(send);

    Button voice = new Button(this);
    voice.setText("🎙 OVOZ BILAN GAPIRISH");
    layout.addView(voice);

    tts = new TextToSpeech(this, code -> {
        if (code == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(new Locale("uz"));
            if (language == TextToSpeech.LANG_MISSING_DATA ||
                language == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(new Locale("ru"));
            }
        }
    });

    send.setOnClickListener(v -> {
        String question = input.getText().toString().trim();
        if (question.isEmpty()) {
            result.setText("Iltimos, savolingizni yozing.");
        } else {
            result.setText("Savolingiz: " + question +
                "\n\nAI ulanishi hali sozlanmagan. Keyingi bosqichda ulaymiz.");
            status.setText("● ALTRON READY");
        }
    });

    voice.setOnClickListener(v -> {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uz-UZ");

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            result.setText("Ovozli kiritish telefoningizda mavjud emas.");
        }
    });

    setContentView(layout);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
        java.util.ArrayList<String> matches =
            data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        if (matches != null && !matches.isEmpty()) {
            input.setText(matches.get(0));
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
