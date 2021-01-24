package icefozen.java.kaantip;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class ChatRoom extends AppCompatActivity {

    private ImageButton sendBtn, backBtn, micBtn, playBtn;
    private TextView roomName;
    private EditText typing;

    private TextToSpeech textToSpeech;

    private RecyclerView conversations;

    private ArrayList<ChatModel> chatOnConversation;

    MessageAdapter messageAdapter;

    private static final String TAG = "ChatRoom";

    private static final int RECOGNIZER_RESULT = 4;


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

        conversations = findViewById(R.id.recycle_view);
        conversations.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        conversations.setLayoutManager(linearLayoutManager);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        playBtn.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

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

    public void speak() {
        String msg = "Hello";

        textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "test");
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

    public void readMessages(String msg){


//        ChatModel chat = new ChatModel(msg);

        chatOnConversation.add(new ChatModel(msg));

        for (ChatModel str : chatOnConversation){
            Log.d(TAG, "Send :  " + str.getMessage());
        }

        messageAdapter = new MessageAdapter(ChatRoom.this, chatOnConversation);
        conversations.setAdapter(messageAdapter);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }
}