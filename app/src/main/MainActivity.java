package com.altron.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setStatusBarColor(Color.rgb(3, 8, 15));
        getWindow().setNavigationBarColor(Color.BLACK);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(3, 8, 15));
        layout.setPadding(24, 24, 24, 24);

        TextView title = new TextView(this);
        title.setText("ALTRON AI");
        title.setTextColor(Color.CYAN);
        title.setTextSize(34);
        title.setGravity(Gravity.CENTER);

        layout.addView(title);

        TextView status = new TextView(this);
        status.setText("TIZIM ISHLAYAPTI");
        status.setTextColor(Color.WHITE);
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 30, 0, 0);

        layout.addView(status);

        setContentView(layout);

    } catch (Exception e) {
        TextView error = new TextView(this);
        error.setText("ALTRON AI ishga tushmoqda...");
        error.setTextColor(Color.WHITE);
        error.setBackgroundColor(Color.BLACK);
        error.setGravity(Gravity.CENTER);
        setContentView(error);
    }
}

}
