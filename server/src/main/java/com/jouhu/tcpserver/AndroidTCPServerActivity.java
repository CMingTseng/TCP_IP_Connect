package com.jouhu.tcpserver;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class AndroidTCPServerActivity extends Activity {

    public static final int MSG_SERVER_START_ERROR = 1;
    public static final int RECEIVEDATA = 2;
    private Button transmit;
    private static final String tag = "AndroidTCPServerActivity";

    private TCPServerThread vSocketServer = null;
    private static final int PORT = 4444;
    private EditText tv;
    private EditText transmit_data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        transmit = (Button) findViewById(R.id.transmit);
        transmit_data = (EditText) findViewById(R.id.transmit_data);
        transmit.setOnClickListener(SendMessage);
        tv = (EditText) findViewById(R.id.editText1);
        vSocketServer = new TCPServerThread(msgHandler, PORT, this);
        vSocketServer.start();
        tv.setText("Server Start At Port " + String.valueOf(PORT));
    }

    @Override
    protected void onPause() {
        try {
            super.onPause();
            if (vSocketServer.client != null && vSocketServer.client.isConnected()) {
                vSocketServer.client.close();
                vSocketServer.sock.close();
            }
            super.onDestroy();
        } catch (IOException e) {

        }
    }

    private Button.OnClickListener SendMessage = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                vSocketServer.writeData(transmit_data.getText().toString() + "\n");
            } catch (IOException e) {

            }

        }
    };

    private final Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SERVER_START_ERROR:
                    //Log.i(tag, "CONNECTED");
                    break;
                case RECEIVEDATA:
                    String data = (String) msg.obj;
                    tv.setText(data);
                    break;

            }
        }
    };

    protected void doBmp(byte[] data) {
    }
}
