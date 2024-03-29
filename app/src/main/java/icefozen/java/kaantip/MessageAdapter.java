package icefozen.java.kaantip;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context mContext;

    public ArrayList<ChatModel> mChat;

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
                }
            });
        }

        public void speak(String msg) {
            textToSpeech.speak(msg, TextToSpeech.QUEUE_ADD, null, "test");
            playBtn.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        }
    }
}
