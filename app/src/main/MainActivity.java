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

        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Color.rgb(3, 8, 15));

        TextView title = new TextView(this);
        title.setText("ALTRON AI");
        title.setTextColor(Color.CYAN);
        title.setTextSize(36);
        title.setGravity(Gravity.CENTER);

        layout.addView(title);

        TextView message = new TextView(this);
        message.setText("TIZIM ISHLAYAPTI");
        message.setTextColor(Color.WHITE);
        message.setTextSize(20);
        message.setGravity(Gravity.CENTER);

        layout.addView(message);

        setContentView(layout);
    }
}
