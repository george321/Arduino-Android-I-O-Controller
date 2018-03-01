package gmitaros.arduinoiocontrol;

import android.app.Activity;
import android.content.Context;

import java.net.*;
import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.speech.RecognizerIntent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class MainActivity extends Activity {
    private Button on;
    private Button off;
    private Button test;
    private Button update;
    private Button sound;
    private Button newIP;
    private Button newPort;
    private EditText lightSet;
    private EditText ipSet;
    private EditText portSet;
    private TextView lightTextView;
    private TextView tempTextView;
    private TextView lightInfoTextView;
    private int portNumber = 8032;
    private static final int TIMEOUT_MILLIS = 2000;
    private String reply;
    private String defaultIP = "192.168.1.105";
    private DatagramSocket datagramSocketForSending;



    public Button speakButton;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    private Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public boolean sendData(String message) throws Exception {

        byte[] b = (message.getBytes());

        if (isOnline()) {

            byte messageBytes[] = message.getBytes();
            if (message.isEmpty()) {
                return false;
            }

            try {
                // Create the packet containing the message, IP and port number
                final DatagramPacket packetLocal = new DatagramPacket(messageBytes, messageBytes.length,
                        InetAddress.getByName(defaultIP), portNumber);
                InetAddress address = InetAddress.getByName("arduinohome.ddns.net");
                Log.d("Log IP: ", address.getHostAddress());
                Log.d("Log DomainName: ", address.getHostName());
                final DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
                        InetAddress.getByName("arduinohome.ddns.net"), portNumber);

                datagramSocketForSending.send(packet);
                datagramSocketForSending.send(packetLocal);

                // The UDP packet left the pc safely, we don't know if it was received somewhere
                return true;

            } catch (final UnknownHostException e) {
                showToastOnUiThread("Couldn't find host");
                return false;
            } catch (final IOException e) {
                Log.d("Log: ", e.getMessage());
                return false;   // Something went wrong
            }
        } else {
            showToastOnUiThread("No network");
            return false;
        }
    }

    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            for (int i = 0; i < matches.size(); i++) {
                Log.d("Log: ", i + " " + matches.get(i));
            }
            if (matches.contains("lights on")) {
                lightOn();
            } else if (matches.contains("lights off") || matches.contains("lights of")) {
                lightOff();
            } else if (matches.contains("play music") || matches.contains("buzzer")) {
                playSound();
            }
        }
    }


    public void onCreate(Bundle savedInstanceState) {

//        layout1 = (LinearLayout) findViewById(R.id.Layout1);
//        layout2 = (LinearLayout) findViewById(R.id.Layout2);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        on = (Button) findViewById(R.id.on);
        off = (Button) findViewById(R.id.off);
        update = (Button) findViewById(R.id.update);
        sound = (Button) findViewById(R.id.sound);
        lightTextView = (TextView) findViewById(R.id.lightValue);
        tempTextView = (TextView) findViewById(R.id.tempValue);
        lightInfoTextView = (TextView) findViewById(R.id.lightLimitValue);
        speakButton = (Button) findViewById(R.id.btn_speak);
        test = (Button) findViewById(R.id.test);
        lightSet = (EditText) findViewById(R.id.lightSet);
        ipSet = (EditText) findViewById(R.id.newIP);
        portSet = (EditText) findViewById(R.id.newPort);
        newIP = (Button) findViewById(R.id.setIP);
        newPort = (Button) findViewById(R.id.setPort);


        try {
            datagramSocketForSending = new DatagramSocket();  // Create a UDP socket
            datagramSocketForSending.setBroadcast(true);  // Enable broadcasts
            datagramSocketForSending.setSoTimeout(TIMEOUT_MILLIS); // Set timeout for socket operations
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }



        speakButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });

        on.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                lightOn();
            }
        });

        off.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                lightOff();
            }
        });

        sound.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                playSound();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            if (sendData("3")) {
                                reply = receivePacket();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        setTextViews(reply);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error: " + e);
                        }
                    }
                }.start();
                showToastOnUiThread("Updating...");


            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            String sendText = "s" + lightSet.getText().toString();
                            if (sendData(sendText)) {
                                showToastOnUiThread("Setting light sensor value..");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error: " + e);
                        }
                    }
                }.start();
            }
        });

        newPort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            String newPort = portSet.getText().toString();
                            portNumber = Integer.parseInt(newPort);
                            Log.d("Log", "SOS: " + portNumber);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error: " + e);
                        }
                    }
                }.start();
            }
        });

        newIP.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            String newIP = ipSet.getText().toString();
                            defaultIP = newIP;
                            Log.d("Log", "SOS: " + defaultIP);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error: " + e);
                        }
                    }
                }.start();
            }
        });

    }

    public void lightOn() {
        new Thread() {
            public void run() {
                try {
                    if (sendData("1")) {
                        showToastOnUiThread("The lights were successfully activated");
                        interrupt();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                }
            }
        }.start();
    }

    public void lightOff() {
        new Thread() {
            public void run() {
                try {
                    if (sendData("2")) {
                        showToastOnUiThread("The lights were successfully deactivated");
                        interrupt();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                }
            }
        }.start();
    }

    private void playSound() {
        new Thread() {
            public void run() {
                try {
                    if (sendData("4")) {
                        showToastOnUiThread("Buzzer activated for 1 sec..");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error: " + e);
                }
            }
        }.start();
    }

    private String receivePacket() {
        try {
            byte buffer[] = new byte[255];
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            datagramSocketForSending.receive(p);
            return new String(p.getData());    // Convert the packet to a string and return it
        } catch (IOException ignored) {
            Log.d("Log", "Size: " + " Nothing received, timeout " + ignored);
        }
        // Nobody replied to the packet, maybe the address didn't exist in the network
        return "Nothing received, timeout";
    }

    private void showToastOnUiThread(final String text) {
        runOnUiThread(new Runnable() {
            public void run() {
                showToast(text);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setTextViews(String message) {
        if (message == null) {
            message = "0-0-0";
        }
        String[] data = message.split("-");
        Log.d("Log", "Size: " + data.length + "  " + data[0]);
        if (data.length > 1) {
            lightTextView.setText(data[0]);
            tempTextView.setText(data[1]);
            lightInfoTextView.setText(data[2]);
        }
    }
}