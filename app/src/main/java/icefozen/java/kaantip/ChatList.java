package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.CloseGuard;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ChatList extends AppCompatActivity {

    private Button room1, room2, room3, room4, room5, edit;

    private String message = "เปิดแอปเพื่อเริ่มสนทนา";

    private final String CHANNEL_ID = "notificationID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        room1 = findViewById(R.id.roomChat1);
        room2 = findViewById(R.id.roomChat2);
        room3 = findViewById(R.id.roomChat3);
        room4 = findViewById(R.id.roomChat4);
        room5 = findViewById(R.id.roomChat5);

        room1.setOnClickListener(View -> {
            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intentForGoToChatRoom);
        });
        room2.setOnClickListener(View -> {
            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intentForGoToChatRoom);
        });
        room3.setOnClickListener(View -> {
            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intentForGoToChatRoom);
        });
        room4.setOnClickListener(View -> {
            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intentForGoToChatRoom);
        });
        room5.setOnClickListener(View -> {

            notificatioChannel();

            Intent intent = new Intent(this, ChatRoom.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            String textTitle = "การแจ้งเตือน";
            String textContent = "ตรวจจับคลื่นสมอง";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(textTitle)
                    .setContentText(textContent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
            Log.d("Notification", "PASS");
        });


    }

    public void notificatioChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationChannel";
            String description = "hello World";
            int important = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, important);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}