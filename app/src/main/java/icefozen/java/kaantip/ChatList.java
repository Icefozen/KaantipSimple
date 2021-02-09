package icefozen.java.kaantip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ChatList extends AppCompatActivity {

    private Button room1, room2, room3, room4, room5;
    private ImageView edit;

    private String message = "เปิดแอปเพื่อเริ่มสนทนา";

    private final String CHANNEL_ID = "notificationID";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        room1 = findViewById(R.id.roomChat1);
        room2 = findViewById(R.id.roomChat2);
        room3 = findViewById(R.id.roomChat3);
        room4 = findViewById(R.id.roomChat4);
        room5 = findViewById(R.id.roomChat5);

        mAuth = FirebaseAuth.getInstance();

        room1.setOnClickListener(View -> {
            makeRoomID(String.valueOf(room1.getId()));
//            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
//            intentForGoToChatRoom.putExtra("roomID", room1.getId());
//            startActivity(intentForGoToChatRoom);
        });
        room2.setOnClickListener(View -> {
            makeRoomID(String.valueOf(room2.getId()));
//            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
//            intentForGoToChatRoom.putExtra("roomID", room2.getId());
//            startActivity(intentForGoToChatRoom);
        });
        room3.setOnClickListener(View -> {
            makeRoomID(String.valueOf(room3.getId()));
//            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
//            intentForGoToChatRoom.putExtra("roomID", room3.getId());
//            startActivity(intentForGoToChatRoom);
        });
        room4.setOnClickListener(View -> {
            makeRoomID(String.valueOf(room4.getId()));
//            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
//            intentForGoToChatRoom.putExtra("roomID", room4.getId());
//            startActivity(intentForGoToChatRoom);
        });
        room5.setOnClickListener(View -> {
            makeRoomID(String.valueOf(room5.getId()));
//            Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
//            intentForGoToChatRoom.putExtra("roomID", room5.getId());
//            startActivity(intentForGoToChatRoom);
        });

        edit = findViewById(R.id.logout);

        edit.setOnClickListener(View -> {
            FirebaseAuth.getInstance().signOut();
            Intent intentForBackToLogin = new Intent(ChatList.this, LoginActivity.class);
            startActivity(intentForBackToLogin);
        });
    }

    public void makeRoomID(String roomid) {
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
//
//        HashMap<String, String > hashMap = new HashMap<>();
//        hashMap.put("id", roomid);
//
//        databaseReference.child(roomid).push().setValue(hashMap);

        Intent intentForGoToChatRoom = new Intent(ChatList.this, ChatRoom.class);
        intentForGoToChatRoom.putExtra("roomID", roomid);
        startActivity(intentForGoToChatRoom);

//        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Intent intent = new Intent(getApplicationContext(), ChatRoom.class);
//                intent.putExtra("roomID", roomid);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
}