package icefozen.java.kaantip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private static final String TAG1 = "Thread";
    private static final String TAG2 = "UUID is";
    private UUID EEG_DRIVER;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt gatt;

    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    public boolean checkThread = true;

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

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    private void updateUI(FirebaseUser currentUser) {
    }

    class threadBackground extends Thread {

        private BluetoothSocket mmSocket;
        private BluetoothServerSocket mmServerSocket;
        private BluetoothDevice mmDevice;

        // UUID 0000110a-0000-1000-8000-00805f9b34fb
        // UUID 0000111f-0000-1000-8000-00805f9b34fb
        // UUID 00001112-0000-1000-8000-00805f9b34fb
        private UUID EEG_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
        ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
        Set<BluetoothDevice> pairedDevice;
        //Server constructor
//        threadBackground() {
//            BluetoothServerSocket tmp = null;
//            try {
//                // MY_UUID is the app's UUID string, also used by the client code.
//                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("EEG", EEG_UUID);
//                Log.d(TAG1, "pass 1");
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's listen() method failed", e);
//            }
//            mmServerSocket = tmp;
//        }

        //Client constructor
        threadBackground() {

            pairedDevice = bluetoothAdapter.getBondedDevices();
            arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();


            BluetoothSocket tmp = null;
//            mmDevice = arrayListPairedBluetoothDevices.get(0);

//            try {
//                tmp = mmDevice.createRfcommSocketToServiceRecord(EEG_UUID);
//            } catch (IOException e) {
//                Log.e(TAG1, "Socket's create() method failed", e );
//            }
//            mmSocket = tmp;
        }

        //Check UUID
//        threadBackground() {
//
//            Method getUuidsMethod = null;
//            try {
//                getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//
//            ParcelUuid[] uuids = new ParcelUuid[0];
//            try {
//                uuids = (ParcelUuid[]) getUuidsMethod.invoke(bluetoothAdapter, null);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//
//            for (ParcelUuid uuid: uuids) {
//                Log.d(TAG2, "UUID: " + uuid.getUuid().toString());
//            }
//        }

        @Override
        public void run() {
            while (checkThread) {
                if (bluetoothAdapter.isEnabled()) {

                    Log.d(TAG1, "pass 2");
                    checkThread = false;

                    //Client

                    //find devices
//                    if(pairedDevice.size()>0)
//                    {
//                        for(BluetoothDevice device : pairedDevice)
//                        {
//                            Log.d(TAG1, "Device name is " + device.getName()+"\n"+device.getAddress());
//                            arrayListPairedBluetoothDevices.add(device);
//                        }
//                    }

//                    bluetoothAdapter.cancelDiscovery();
//                    try {
//                        mmSocket.connect();
//                    } catch (IOException connectException) {
//                        try {
//                            mmSocket.close();
//                        } catch (IOException closeException) {
//                            Log.e(TAG1, "Could not close the client socket", closeException);
//                        }
//                        return;
//                    }

//                    if (mmSocket.isConnected()){
//                        login("superhero1");
//                    }

                    // Server
//                    try {
//                        mmSocket = mmServerSocket.accept();
//                        Log.d(TAG1, "pass 3");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    if (mmSocket != null) {

                    if (BluetoothAdapter.STATE_CONNECTED == 2) {
                        Log.d(TAG1, "Open BT with paired");
//                    login("superhero1");

                        // Slow time open app
//                        try {
//                            TimeUnit.SECONDS.sleep(2);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                        // Open app

//                        Intent intent = new Intent(MainActivity.this, ChatList.class);
//                        startActivity(intent);

//                        for (int i = 0; i < 100; i++) {
//                            Log.d(TAG1, "Open BT with paired: " + i);
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
                        String message = "เปิดแอปเพื่อเริ่มสนทนา";

                        String CHANNEL_ID = "notificationID";

                        notificatioChannel();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);

                        String textTitle = "การแจ้งเตือน";
                        String textContent = "ตรวจจับคลื่นสมอง";

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


                    } else {
                        for (int i = 0; i < 100; i++) {
                            Log.d(TAG1, "Open BT without paired : " + i);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

        }

        public void notificatioChannel() {
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
}
