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
    private static final String TAG = "AndroidTCPServerActivity";
    private static final int PORT = 4444;
    private TCPServerThread mSocketServer = null;
    private EditText mMessageInfo;

    private final Handler mMsgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case C.MSG_SERVER_START_ERROR:
                    //Log.i(TAG, "CONNECTED");
                    break;
                case C.RECEIVEDATA:
                    final String data = (String) msg.obj;
                    mMessageInfo.setText(data);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSocketServer = new TCPServerThread(mMsgHandler, PORT);
        mSocketServer.start();
        final Button transmit = (Button) findViewById(R.id.transmit);
        final EditText transmitData = (EditText) findViewById(R.id.transmit_data);
        transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSocketServer.writeData(transmitData.getText().toString() + "\n");
                } catch (IOException e) {

                }
            }
        });
        mMessageInfo = (EditText) findViewById(R.id.editText1);

        mMessageInfo.setText("Server Start At Port " + String.valueOf(PORT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mSocketServer.mClient != null && mSocketServer.mClient.isConnected()) {
                mSocketServer.mClient.close();
                mSocketServer.mServerSocket.close();
            }
        } catch (IOException e) {

        }
    }
}
