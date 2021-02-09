package icefozen.java.kaantip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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

    private TextToSpeech textToSpeech;

//    private Intent intent;
    private String roomID;

    private BackgroundTread backgroundTread;


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

//        try {
//            backgroundTread = new BackgroundTread();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        backgroundTread.start();

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
//        intent = getIntent();
//        roomID = intent.getStringExtra("roomID");
        roomID = "2131231001";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomID);
//        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessages(firebaseUser.getUid());
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
//                    sendMessage(msg, firebaseUser.getUid(), roomID);
                    sendMessage(msg, firebaseUser.getUid());
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
//                speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "พูดได้เลย !");
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
            }
        });

        //text to speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "Language not supported");
                    } else {
                        Log.d("TTS", "Language passed");
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

//            typing.setText(matches.get(0));
            if (!matches.get(0).equals("")) {
//                sendMessage(matches.get(0), firebaseUser.getUid(), roomID);
                sendMessage(matches.get(0), firebaseUser.getUid());
            }

            Log.d(TAG, "text is : " + matches.get(0));
        }
        else {
            Log.d(TAG, "request code is " + requestCode);
        }
    }

    public void readMessages(String sender){

        chatOnConversation = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatOnConversation.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d(TAG, "snapshot = " + snapshot.getChildren());
                    ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                    if (chatModel.getSender().equals(sender)) {
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


    public void sendMessage(String msg, String sender) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", msg);

        reference.child("Chats").push().setValue(hashMap);
        speakTTS(msg);
    }

    public void sendMessageByIndividuals(String msg, String sender) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", msg);
        reference.child("Chats").push().setValue(hashMap);
    }


    public void speakTTS(String msg) {
        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "test");
//            for (int i=0; i<mChat.size(); i++) {
//                Log.d("TTS", "speak : " + i + " " + mChat.get(i).getMessage());
//            }
    }

    class BackgroundTread extends Thread {

        private static final String TAG1 = "Background Tread = ";

        public BackgroundTread() throws InterruptedException {

            while (true) {
                Thread.sleep(50000);
                Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "พูดได้เลย !");
                speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                startActivityForResult(speechIntent, RECOGNIZER_RESULT);
                Log.d(TAG1, "hello world");
                Thread.sleep(5000);
            }

        }


    }
}