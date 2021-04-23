 package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoSignalQuality;
import com.neurosky.AlgoSdk.NskAlgoState;
import com.neurosky.AlgoSdk.NskAlgoType;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private static final String TAG1 = "Thread";
    private BluetoothAdapter bluetoothAdapter;

    public boolean checkThread = true;
    public static boolean status = false;

    threadBackground testThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (checkThread) {
            testThread = new threadBackground();
            testThread.start();
        } else {
            testThread.interrupt();
            Log.d(TAG1, "Welcome");
        }

//        try {
//            Intent intent = new Intent(MainActivity.this, ChatRoom.class);
//            TimeUnit.SECONDS.sleep(2);
//            startActivity(intent);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(MainActivity.this, ChatRoom.class);
        startActivity(intent);

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart: ");
//    }

    public class threadBackground extends Thread {

        // canned data variables
        private short raw_data[] = {0};
        private int raw_data_index= 0;
        private float output_data[];
        private int output_data_count = 0;
        private int raw_data_sec_len = 85;

        private TgStreamReader tgStreamReader;
        private NskAlgoSdk nskAlgoSdk;

        private String message = "เปิดแอปเพื่อเริ่มสนทนา";
        private String CHANNEL_ID = "notificationID";
        private int algoTypes = 0;
        private boolean bInited = false;
        private boolean bRunning = false;
        private NskAlgoType currentSelectedAlgo;

        private int checkMoreBeta;

//        public boolean status;


        threadBackground() {

            setAlgo();

            checkMoreBeta = 0;

            output_data_count = 0;
            output_data = null;

            raw_data = new short[512];
            raw_data_index = 0;

            nskAlgoSdk = new NskAlgoSdk();

            connectInit();
            startBT();

            processingData();

            Log.d("Algo type ", "" + algoTypes);

        }

        private void processingData() {
            // detect BP
            nskAlgoSdk.setOnBPAlgoIndexListener(new NskAlgoSdk.OnBPAlgoIndexListener() {
                @Override
                public void onBPAlgoIndex(float delta, float theta, float alpha, float beta, float gamma) {
                    Log.d(TAG, "NskAlgoBPAlgoIndexListener: BP: D[" + delta + " dB] T[" + theta + " dB] A[" + alpha + " dB] B[" + beta + " dB] G[" + gamma + "]");

                    final float fDelta = delta, fTheta = theta, fAlpha = alpha, fBeta = beta, fGamma = gamma;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG1, "delta : " + fDelta);
                            Log.d(TAG1, "theta : " + fTheta);
                            Log.d(TAG1, "alpha : " + fAlpha);
                            Log.d(TAG1, "beta : " + fBeta);
                            Log.d(TAG1, "gamma : " + fGamma);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG1, "delta : " + fDelta);
//                            Log.d(TAG1, "theta : " + fTheta);
//                            Log.d(TAG1, "alpha : " + fAlpha);
//                            Log.d(TAG1, "beta : " + fBeta);
//                            Log.d(TAG1, "gamma : " + fGamma);
//                        }
//                    });
                    if (fBeta > fAlpha) {
                        checkMoreBeta++;
                        if (checkMoreBeta > 7) {
                            notificationShow();
                        }
                    }
                    else {
                        checkMoreBeta = 0;
                    }
                }
            });

            // Detect Attention
            nskAlgoSdk.setOnAttAlgoIndexListener(new NskAlgoSdk.OnAttAlgoIndexListener() {
                @Override
                public void onAttAlgoIndex(int value) {
                    Log.d(TAG, "NskAlgoAttAlgoIndexListener: Attention:" + value);
                    String attStr = "[" + value + "]";
                    final String finalAttStr = attStr;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (value > 50) {
                                notificationShow();
                                Log.d(TAG1, "ATT more than 50");
//                                checkThread = false;
                            }
                        }
                    });
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (value > 50) {
//                                            notificationShow();
//                                Log.d(TAG1, "ATT more than 50");
////                                checkThread = false;
//                            }
//
//                        }
//                    });
                }
            });

            // Detect Signal
            nskAlgoSdk.setOnSignalQualityListener(new NskAlgoSdk.OnSignalQualityListener() {
                @Override
                public void onSignalQuality(final int level) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sqStr = NskAlgoSignalQuality.values()[level].toString();
//                                    sqText.setText(sqStr);
                            Log.d(TAG1, "Status is " + sqStr);
                        }
                    });
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // change UI elements here
//                            String sqStr = NskAlgoSignalQuality.values()[level].toString();
////                                    sqText.setText(sqStr);
//                            Log.d(TAG1, "Status is " + sqStr);
//                        }
//                    });
                }
            });

            //Check state Change
            nskAlgoSdk.setOnStateChangeListener(new NskAlgoSdk.OnStateChangeListener() {
                @Override
                public void onStateChange(int state, int reason) {
                    String stateStr = "";
                    String reasonStr = "";
                    for (NskAlgoState s : NskAlgoState.values()) {
                        if (s.value == state) {
                            stateStr = s.toString();
                        }
                    }
                    for (NskAlgoState r : NskAlgoState.values()) {
                        if (r.value == reason) {
                            reasonStr = r.toString();
                        }
                    }
                    Log.d(TAG, "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);
                    final String finalStateStr = stateStr + " | " + reasonStr;
                    final int finalState = state;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (finalState == NskAlgoState.NSK_ALGO_STATE_RUNNING.value || finalState == NskAlgoState.NSK_ALGO_STATE_COLLECTING_BASELINE_DATA.value) {
                                bRunning = true;
                            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_STOP.value) {
                                bRunning = false;
                                raw_data = null;
                                raw_data_index = 0;

                                if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                                    // Prepare for connecting
                                    tgStreamReader.stop();
                                    tgStreamReader.close();
                                }

                                output_data_count = 0;
                                output_data = null;

                                System.gc();
                            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                                bRunning = false;
                            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                                bRunning = true;
                            } else if (finalState == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                                bRunning = false;
                            }
                        }
                    });
                }
            });
        }

        private void startBT() {
            if (bRunning == false) {
                nskAlgoSdk.NskAlgoStart(false);
                Log.d(TAG1, "startBT: ");
            } else {
                nskAlgoSdk.NskAlgoPause();
                Log.d(TAG, "stopBT: ");
            }
        }

        private void connectInit() {

            tgStreamReader = new TgStreamReader(bluetoothAdapter,callback);

            if(tgStreamReader != null && tgStreamReader.isBTConnected()){

                // Prepare for connecting
                tgStreamReader.stop();
                tgStreamReader.close();
                Log.d(TAG1, "TG stop and close ");
            }

            tgStreamReader.connectAndStart();
        }

        public void setAlgo() {

            algoTypes = 0;

            currentSelectedAlgo = NskAlgoType.NSK_ALGO_TYPE_INVALID;

            algoTypes += NskAlgoType.NSK_ALGO_TYPE_BP.value;
            algoTypes += NskAlgoType.NSK_ALGO_TYPE_ATT.value;

            if (algoTypes == 0) {
                Log.d(TAG1, "algoType = 0");
            }
            else {
                if (bInited) {
                    nskAlgoSdk.NskAlgoUninit();
                    bInited = false;
                }
                int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                if (ret == 0) {
                    bInited = true;
                }

                Log.d(TAG, "NSK_ALGO_Init() " + ret);
            }

            currentSelectedAlgo = NskAlgoType.NSK_ALGO_TYPE_BP;

        }

        public void notificationShow() {
            if (status){
                notificationChannel();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                String textTitle = "การแจ้งเตือน";
                String textContent = "ตรวจจับความต้องการสื่อสาร";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle(textTitle)
                        .setContentText(textContent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                notificationManager.notify(0, builder.build());
                Log.d("Notification", "PASS");
            }
            else {
                Log.d("Notification", "Do not show notification");
            }

        }

        @Override
        public void run() {
            while (checkThread) {
                if (bluetoothAdapter.isEnabled()) {

                    if (!bRunning) {
                        try {
                            connectInit();
                            startBT();
                            sleep(5000);
                            Log.d(TAG1, "Connect again");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

//                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


                }
            }

        }

        private TgStreamHandler callback = new TgStreamHandler() {

            @Override
            public void onStatesChanged(int connectionStates) {
                // TODO Auto-generated method stub
                Log.d(TAG, "connectionStates change to: " + connectionStates);
                switch (connectionStates) {
                    case ConnectionStates.STATE_CONNECTING:
                        // Do something when connecting
                        Log.d(TAG1, "Connecting");
                        break;
                    case ConnectionStates.STATE_CONNECTED:
                        // Do something when connected
                        tgStreamReader.start();
//                        showToast("Connected", Toast.LENGTH_SHORT);
                        Log.d(TAG1, "Connected");
//                        checkThread = false;
                        break;
                    case ConnectionStates.STATE_WORKING:
                        // Do something when working

                        //(9) demo of recording raw data , stop() will call stopRecordRawData,
                        //or you can add a button to control it.
                        //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData
                        Log.d(TAG1, "Working");
                        bRunning = true;
//                        tgStreamReader.startRecordRawData();
                        break;
                    case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                        // Do something when getting data timeout

                        //(9) demo of recording raw data, exception handling
//                        tgStreamReader.stopRecordRawData();

//                        showToast("Get data time out!", Toast.LENGTH_SHORT);
                        Log.d(TAG1, "Get data time out!");

//                        if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
//                            tgStreamReader.stop();
//                            tgStreamReader.close();
//                        }
                        bRunning = false;

                        break;
                    case ConnectionStates.STATE_STOPPED:
                        // Do something when stopped
                        // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                        // tgStreamReader.connectAndstart(), because we have to prepare for that.
                        bRunning = true;
                        break;
                    case ConnectionStates.STATE_DISCONNECTED:
                        // Do something when disconnected
                        Log.d(TAG1, "Disconnected");
                        bRunning = true;
                        break;
                    case ConnectionStates.STATE_ERROR:
                        // Do something when you get error message
                        break;
                    case ConnectionStates.STATE_FAILED:
                        // Do something when you get failed message
                        // It always happens when open the BluetoothSocket error or timeout
                        // Maybe the device is not working normal.
                        // Maybe you have to try again
                        break;
                }
            }

            @Override
            public void onRecordFail(int flag) {
                // You can handle the record error message here
                Log.e(TAG,"onRecordFail: " +flag);

            }

            @Override
            public void onChecksumFail(byte[] payload, int length, int checksum) {
                // You can handle the bad packets here.
            }

            @Override
            public void onDataReceived(int datatype, int data, Object obj) {
                // You can handle the received data here
                // You can feed the raw data to algo sdk here if necessary.
//                Log.i(TAG1,"onDataReceived : " + data);
                switch (datatype) {
                    case MindDataType.CODE_ATTENTION:
                        short attValue[] = {(short)data};
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ATT.value, attValue, 1);
                        break;
                    case MindDataType.CODE_MEDITATION:
                        short medValue[] = {(short)data};
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_MED.value, medValue, 1);
                        break;
                    case MindDataType.CODE_POOR_SIGNAL:
                        short pqValue[] = {(short)data};
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_PQ.value, pqValue, 1);
                        break;
                    case MindDataType.CODE_RAW:
                        raw_data[raw_data_index++] = (short)data;
                        if (raw_data_index == 512) {
                            nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                            raw_data_index = 0;
                        }
                        break;
                    default:
                        break;
                }
            }

        };


        public void notificationChannel() {
            String CHANNEL_ID = "notificationID";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "NotificationChannel";
                String description = "มีการตรวจจับความตั้งใจ";
                int important = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, important);
                channel.setDescription(description);

                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        status = true;
        Log.d(TAG1, "onPause: Status is true");
    }
}
