package com.altron.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends Activity {

    private final String BASE =
        "https://api-fxpractice.oanda.com/v3";

    private LinearLayout root;
    private EditText tokenField;
    private Spinner pairSpinner, frameSpinner;
    private TextView status, result, priceView;
    private Button analyzeButton;

    private final String[] pairs = {
        "EUR_USD", "GBP_USD", "USD_JPY",
        "AUD_USD", "USD_CAD", "USD_CHF",
        "NZD_USD", "EUR_JPY", "GBP_JPY",
        "CHF_JPY", "EUR_GBP"
    };

    private final String[] frames = {"M1", "M5", "M15"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.rgb(3, 8, 15));
        getWindow().setNavigationBarColor(Color.BLACK);

        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 28);
        root.setBackgroundColor(Color.rgb(3, 8, 15));
        scroll.addView(root);

        TextView title = text("ALTRON AI TRADER", 28, Color.CYAN);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = text(
            "OANDA DEMO • BOZOR TAHLILI", 14, Color.LTGRAY
        );
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 10, 0, 22);
        root.addView(subtitle);

        addLabel("OANDA demo API tokeni");

        tokenField = new EditText(this);
        tokenField.setSingleLine(true);
        tokenField.setHint("Tokenni shu yerga kiriting");
        tokenField.setHintTextColor(Color.GRAY);
        tokenField.setTextColor(Color.WHITE);
        tokenField.setInputType(
            InputType.TYPE_CLASS_TEXT |
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        root.addView(tokenField);

        addLabel("Valyuta juftligi");

        pairSpinner = new Spinner(this);
        pairSpinner.setAdapter(new ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            pairs
        ));
        root.addView(pairSpinner);

        addLabel("Tahlil oralig‘i");

        frameSpinner = new Spinner(this);
        frameSpinner.setAdapter(new ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            frames
        ));
        root.addView(frameSpinner);

        analyzeButton = new Button(this);
        analyzeButton.setText("BOZORNI TAHLIL QILISH");
        root.addView(analyzeButton);

        status = text("Holat: tayyor", 15, Color.YELLOW);
        status.setPadding(0, 20, 0, 12);
        root.addView(status);

        priceView = text("Narx: —", 23, Color.CYAN);
        priceView.setGravity(Gravity.CENTER);
        priceView.setPadding(0, 12, 0, 18);
        root.addView(priceView);

        result = text(
            "Tahlil natijasi shu yerda ko‘rsatiladi.", 16, Color.WHITE
        );
        root.addView(result);

        analyzeButton.setOnClickListener(v -> analyze());

        setContentView(scroll);
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, 6, 0, 6);
        return t;
    }

    private void addLabel(String value) {
        TextView t = text(value, 16, Color.WHITE);
        t.setPadding(0, 18, 0, 8);
        root.addView(t);
    }

    private void analyze() {
        String token = tokenField.getText().toString().trim();

        if (token.isEmpty()) {
            status.setText("OANDA demo tokenini kiriting.");
            return;
        }

        String pair = pairSpinner.getSelectedItem().toString();
        String frame = frameSpinner.getSelectedItem().toString();

        analyzeButton.setEnabled(false);
        status.setText("OANDA ma’lumotlari olinmoqda...");

        new Thread(() -> {
            try {
                String accountId = getAccountId(token);
                JSONArray candles = getCandles(
                    token, accountId, pair, frame
                );

                if (candles.length() < 3) {
                    throw new Exception(
                        "Tahlil uchun shamlar yetarli emas."
                    );
                }

                JSONObject last = candles.getJSONObject(
                    candles.length() - 1
                );

                JSONObject mid = last.getJSONObject("mid");
                double open = mid.getDouble("o");
                double close = mid.getDouble("c");
                double high = mid.getDouble("h");
                double low = mid.getDouble("l");

                double body = Math.abs(close - open);
                double upper = high - Math.max(open, close);
                double lower = Math.min(open, close) - low;

                String direction;
                String explanation;

                if (close > open && lower > body) {
                    direction = "Yuqoriga yo‘nalish alomati";
                    explanation =
                        "Pastki soya tanadan uzun va sham ko‘tarilgan. " +
                        "Bu xaridorlar faolligini ko‘rsatishi mumkin.";
                } else if (close < open && upper > body) {
                    direction = "Pastga yo‘nalish alomati";
                    explanation =
                        "Yuqori soya tanadan uzun va sham pasaygan. " +
                        "Bu sotuvchilar faolligini ko‘rsatishi mumkin.";
                } else if (close > open) {
                    direction = "Sham ko‘tarilgan";
                    explanation =
                        "Yopilish narxi ochilish narxidan yuqori.";
                } else if (close < open) {
                    direction = "Sham pasaygan";
                    explanation =
                        "Yopilish narxi ochilish narxidan past.";
                } else {
                    direction = "Aniq yo‘nalish yo‘q";
                    explanation =
                        "Ochilish va yopilish narxlari deyarli teng.";
                }

                double previousClose = candles
                    .getJSONObject(candles.length() - 2)
                    .getJSONObject("mid")
                    .getDouble("c");

                String trend;
                if (close > previousClose) {
                    trend = "Oxirgi yopilishlar bo‘yicha o‘sish";
                } else if (close < previousClose) {
                    trend = "Oxirgi yopilishlar bo‘yicha pasayish";
                } else {
                    trend = "Oxirgi yopilishlar o‘zgarmagan";
                }

                final String output =
                    "💎 Juftlik: " + pair.replace("_", "/") +
                    "\n⏱ Tahlil oralig‘i: " + frame +
                    "\n\n📊 Sham tahlili" +
                    "\nOchilish: " + format(open) +
                    "\nYopilish: " + format(close) +
                    "\nEng yuqori: " + format(high) +
                    "\nEng past: " + format(low) +
                    "\n\n🧭 " + direction +
                    "\n📈 " + trend +
                    "\n\n🔎 Sabab: " + explanation +
                    "\n\n⚠️ Bu tarixiy narxga asoslangan texnik " +
                    "kuzatuv. Keyingi narx yo‘nalishini " +
                    "kafolatlamaydi.";

                runOnUiThread(() -> {
                    priceView.setText("Joriy yopilgan sham: " + format(close));
                    result.setText(output);
                    status.setText("Tahlil yakunlandi.");
                    analyzeButton.setEnabled(true);
                });

            } catch (Exception e) {
                String msg = e.getMessage();
                if (msg == null) msg = "Noma’lum xatolik";
                final String error = msg;

                runOnUiThread(() -> {
                    status.setText("Xatolik: " + error);
                    analyzeButton.setEnabled(true);
                });
            }
        }).start();
    }

    private String getAccountId(String token) throws Exception {
        JSONObject data = request(
            token, BASE + "/accounts"
        );

        JSONArray accounts = data.getJSONArray("accounts");

        if (accounts.length() == 0) {
            throw new Exception("OANDA hisob topilmadi.");
        }

        return accounts.getJSONObject(0).getString("id");
    }

    private JSONArray getCandles(
        String token,
        String accountId,
        String pair,
        String frame
    ) throws Exception {

        String url = BASE + "/instruments/" + pair +
            "/candles?granularity=" + frame + "&count=30&price=M";

        JSONObject data = request(token, url);
        JSONArray all = data.getJSONArray("candles");
        JSONArray complete = new JSONArray();

        for (int i = 0; i < all.length(); i++) {
            JSONObject candle = all.getJSONObject(i);

            if (candle.optBoolean("complete", false)) {
                complete.put(candle);
            }
        }

        return complete;
    }

    private JSONObject request(String token, String address)
        throws Exception {

        HttpURLConnection connection = null;

        try {
            URL url = new URL(address);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                "Authorization", "Bearer " + token
            );
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int code = connection.getResponseCode();

            if (code != 200) {
                throw new Exception(
                    "OANDA xatosi: HTTP " + code +
                    ". Demo token va hisobni tekshiring."
                );
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            return new JSONObject(response.toString());

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String format(double value) {
        return String.format(Locale.US, "%.5f", value);
    }
}
