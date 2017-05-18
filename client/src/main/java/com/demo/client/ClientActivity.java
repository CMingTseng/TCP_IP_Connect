package com.demo.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientActivity extends Activity {
    private static Handler mHandler = new Handler();
    private Socket clientSocket = null;
    private EditText mAccept;
    private String mTranTmp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        new Thread(readData).start();
        final EditText serverip = (EditText) findViewById(R.id.server);
        final EditText port = (EditText) findViewById(R.id.ECG_port);
        final EditText input_message = (EditText) findViewById(R.id.message);
        mAccept = (EditText) findViewById(R.id.ECG_accept);
        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                InetAddress serverIp;
                try {
                    serverIp = InetAddress.getByName(serverip.getText().toString());
                    int serverPort = Integer.valueOf(port.getText().toString());
                    clientSocket = new Socket(serverIp, serverPort); // Connect Server
                    if (clientSocket.isConnected()) {
                        submit.setEnabled(false);
                        String s = "USER NAME=";
                        s += serverip.getText().toString() + " ";
                        s += "SERVER_PORT=";
                        s += port.getText().toString();
                        Toast.makeText(ClientActivity.this, s, Toast.LENGTH_SHORT).show();
                        SendData(s);
                        try {
                            Thread.currentThread().sleep(10);//sleep for 1000 ms
                        } catch (InterruptedException ie) {
                        }
                    }
                } catch (IOException e) {

                }
            }
        });
        final Button transmit = (Button) findViewById(R.id.transmit);
        transmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData(input_message.getText().toString());
            }
        });
    }

    private void SendData(String message) {
        if (clientSocket != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                bw.write(message + "\n");
                bw.flush();
            } catch (Exception ex) {

            }
        }
    }

    private Runnable readData = new Runnable() {
        public void run() {
            try {
                while (true) {
                    if (clientSocket != null) {
                        while (clientSocket.isConnected()) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            mTranTmp = br.readLine();
                            if (mTranTmp != null)
                                mHandler.post(updateText);
                        }
                    }
                }
            } catch (IOException e) {

            }
        }
    };

    private Runnable updateText = new Runnable() {
        public void run() {
            mAccept.setText(mTranTmp);
        }
    };

    @Override
    protected void onStop() {
        try {
            if (clientSocket != null && clientSocket.isConnected()) clientSocket.close();
        } catch (IOException e) {

        }
        super.onStop();
    }
}


