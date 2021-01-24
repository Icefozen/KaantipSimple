package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ChatRoom extends AppCompatActivity {

    private ImageButton sendBtn,backBtn;
    private TextView roomName;
    private EditText typing;

    private RecyclerView conversations;

    private ArrayList<ChatModel> chatOnConversation;

    MessageAdapter messageAdapter;

    private static final String TAG = "ChatRoom";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);
        roomName = findViewById(R.id.roomName_text);
        typing = findViewById(R.id.typingText);

        conversations = findViewById(R.id.recycle_view);
        conversations.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        conversations.setLayoutManager(linearLayoutManager);

        chatOnConversation = new ArrayList<>();

        backBtn.setOnClickListener(View -> {
            Intent intent = new Intent(ChatRoom.this,ChatList.class);
            startActivity(intent);
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typing.getText().toString();
                if (!msg.equals("")) {

                    readMessages(msg);
                }
                else {
                    Toast.makeText(ChatRoom.this, "ไม่สามารถส่งข้อความว่างได้", Toast.LENGTH_LONG).show();
                }
                typing.setText("");
            }
        });

    }

    public void readMessages(String msg){


//        ChatModel chat = new ChatModel(msg);

        chatOnConversation.add(new ChatModel(msg));

        for (ChatModel str : chatOnConversation){
            Log.d(TAG, "Send :  " + str.getMessage());
        }

        messageAdapter = new MessageAdapter(ChatRoom.this, chatOnConversation);
        conversations.setAdapter(messageAdapter);

    }
}