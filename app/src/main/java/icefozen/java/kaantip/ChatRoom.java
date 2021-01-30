package icefozen.java.kaantip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


public class ChatRoom extends AppCompatActivity {

    private ImageButton sendBtn, backBtn, micBtn, playBtn;
    private TextView roomName;
    private EditText typing;

    private RecyclerView conversations;

    private ArrayList<ChatModel> chatOnConversation;

    MessageAdapter messageAdapter;

    private static final String TAG = "ChatRoom";

    private static final int RECOGNIZER_RESULT = 4;

    private FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        playBtn = findViewById(R.id.playBtn);
        micBtn = findViewById(R.id.micBtn);
        backBtn = findViewById(R.id.backBtn);
        sendBtn = findViewById(R.id.sendBtn);
        roomName = findViewById(R.id.roomName_text);
        typing = findViewById(R.id.typingText);

        //setting RecyclerView
        conversations = findViewById(R.id.recycle_view);
        conversations.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        conversations.setLayoutManager(linearLayoutManager);

        // Back function
        backBtn.setOnClickListener(View -> {
            Intent intent = new Intent(ChatRoom.this, ChatList.class);

            startActivity(intent);
        });

        // Database
        intent = getIntent();

        String roomID = intent.getStringExtra("roomID");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessages(roomID, firebaseUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Send function
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = typing.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(msg, firebaseUser.getUid(), roomID);
                } else {
                    Toast.makeText(ChatRoom.this, "ไม่สามารถส่งข้อความว่างได้", Toast.LENGTH_LONG).show();
                }
                typing.setText("");
            }
        });

        // Speech to text
        micBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "พูดได้เลย !");
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            typing.setText(matches.get(0));
            Log.d(TAG, "text is : " + matches.get(0));
        }
        else {
            Log.d(TAG, "request code is " + requestCode);
        }
    }

    public void readMessages(String roomId, String sender){

        chatOnConversation = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatOnConversation.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "snapshot = " + snapshot.getChildren());
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getSender().equals(sender) && chatModel.getRoom().equals(roomId)) {
                        chatOnConversation.add(chatModel);
                    }
                    messageAdapter = new MessageAdapter(ChatRoom.this, chatOnConversation);
                    conversations.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Local Chat
//    public void readMessages(String msg) {
//
//        chatOnConversation = new ArrayList<>();
//
//        chatOnConversation.add(new ChatModel(msg));
//
//        for (ChatModel str : chatOnConversation){
//            Log.d(TAG, "Send :  " + str.getMessage());
//        }
//
//        messageAdapter = new MessageAdapter(ChatRoom.this, chatOnConversation);
//        conversations.setAdapter(messageAdapter);
//    }

    public void sendMessage(String msg, String sender, String room) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", msg);
        hashMap.put("room", room);

        reference.child("Chats").push().setValue(hashMap);
    }
}