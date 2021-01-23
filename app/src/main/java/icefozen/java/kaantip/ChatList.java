package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChatList extends AppCompatActivity {

    private Button room1, room2, room3, room4, room5, edit;

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
            Intent intent = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intent);
        });
        room2.setOnClickListener(View -> {
            Intent intent = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intent);
        });
        room3.setOnClickListener(View -> {
            Intent intent = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intent);
        });
        room4.setOnClickListener(View -> {
            Intent intent = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intent);
        });
        room5.setOnClickListener(View -> {
            Intent intent = new Intent(ChatList.this, ChatRoom.class);
            startActivity(intent);
        });
    }
}