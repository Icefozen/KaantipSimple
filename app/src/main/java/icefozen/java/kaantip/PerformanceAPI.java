package icefozen.java.kaantip;

import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PerformanceAPI extends AppCompatActivity {

    private static final String TAG = "Performance";

    private TextToSpeech textToSpeech;
    private int count;
    private Timestamp timestamp1, timestamp2;
    private double average;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_a_p_i);
        count = 0;
        average = 0;
        textToSpeech = new TextToSpeech( this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("th"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "Language not supported");
                    } else {
                        Log.d("TTS", "Language passed");
                        try {
                            TestTTS();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

//        TextToSpeechConfig();

    }


    public void TestTTS() throws IOException {

        String string = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = this.getResources().openRawResource(R.raw.thaiword);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null)
                    break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            timestamp1 = new Timestamp(System.currentTimeMillis());
            Log.d(TAG, "Round " + count + "\nStart " +timestamp1 + " " + timestamp1.getTime());
            textToSpeech.speak(string, TextToSpeech.QUEUE_ADD, null, "test");
//            while (textToSpeech.isSpeaking()){ }
            timestamp2 = new Timestamp(System.currentTimeMillis());
            Log.d(TAG,"Round " + count + "\nStop " +timestamp2 + " " + timestamp2.getTime());
            average += timestamp2.getTime() - timestamp1.getTime();
            count++;

            //0.46305418719211821 millisecond

        }
        Log.d(TAG, "Average : " + average/count+1);
        is.close();
    }


}
