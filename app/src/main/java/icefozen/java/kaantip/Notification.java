package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Notification extends AppCompatActivity {

    private TextView notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationText = findViewById(R.id.notification_text);

        String message = getIntent().getStringExtra("การแจ้งเตือน");

        notificationText.setText(message);
    }
}