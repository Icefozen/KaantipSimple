package icefozen.java.kaantip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;

    public ArrayList<ChatModel> mChat;

    private FirebaseUser firebaseUser;

    private boolean check;

    public MessageAdapter(Context mContext, ArrayList<ChatModel> mChat){
        this.mContext = mContext;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatModel chat = mChat.get(position);

        holder.show_message.setText(chat.getMessage());
        holder.playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        public ImageButton playBtn;
        public RelativeLayout messageLayout;
        private TextToSpeech textToSpeech;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            show_message = itemView.findViewById(R.id.chatText);
            playBtn = itemView.findViewById(R.id.playBtn);

            // Text to speech setting
            textToSpeech = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
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

            // Text to speech function
            check = true;
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (check){
                        playBtn.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
//                        speak(mChat.get(mChat.size()-1).getMessage());
                        speak(mChat.get(getAdapterPosition()).getMessage());
                    }
                    else {
                        playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                    }
//                    check = !check;
//                    Log.d("Onclick", " id = " + getItemCount() + " " + getAdapterPosition() + " " + getPosition());

                }
            });

            messageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("ลบข้อความ");
                    builder.setMessage("คุณต้องการลบข้อความนี้ใช่ไหม ?");

                    //delete btn
                    builder.setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMessage(getAdapterPosition());
                        }

                    });

                    //cancel btn
                    builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            });


        }


        public void speak(String msg) {

            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, "test");

            playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
//            for (int i=0; i<mChat.size(); i++) {
//                Log.d("TTS", "speak : " + i + " " + mChat.get(i).getMessage());
//            }
        }

        private void deleteMessage(int adapterPosition) {
            final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        if (dataSnapshot.child("sender").getValue().equals(myUID)){
                            dataSnapshot.getRef().removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }



}
