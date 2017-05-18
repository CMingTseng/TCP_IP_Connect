package com.demo.client;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientActivity extends Activity {
    public static Handler mHandler = new Handler();
    Button calcbutton_ECG;
    private Button transmit;
    private TextView txtb_ID;
    private TextView txtb_PSWD;
    private Socket clientSocket = null;
    private String s = "";
    private EditText edittb_server;
    private EditText edittb_ECG_port;
    private EditText edittb_message;
    private EditText edittb_accept;
    String tmp;
    ServerSocket serverSocket;
    int serverPort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Thread t = new Thread(readData);
        t.start();
        findViews();
        setListeners();
    }

    private void findViews() {
        calcbutton_ECG = (Button) findViewById(R.id.submit);
        transmit = (Button) findViewById(R.id.transmit);
        edittb_server = (EditText) findViewById(R.id.server);
        edittb_ECG_port = (EditText) findViewById(R.id.ECG_port);
        edittb_message = (EditText) findViewById(R.id.message);
        edittb_message = (EditText) findViewById(R.id.message);
        edittb_accept = (EditText) findViewById(R.id.ECG_accept);
    }

    private void setListeners() {
        calcbutton_ECG.setOnClickListener(calcECG);
        transmit.setOnClickListener(SendMessage);
    }

    private Button.OnClickListener calcECG = new Button.OnClickListener() {
        public void onClick(View v) {
            InetAddress serverIp;
            try {
                serverIp = InetAddress.getByName(edittb_server.getText().toString());
                int serverPort = Integer.valueOf(edittb_ECG_port.getText().toString());
                clientSocket = new Socket(serverIp, serverPort); // Connect Server
                if (clientSocket.isConnected()) {
                    calcbutton_ECG.setEnabled(false);
                    s = "USER NAME=";
                    s += edittb_server.getText().toString() + " ";
                    s += "SERVER_PORT=";
                    s += edittb_ECG_port.getText().toString();
                    Toast.makeText(ClientActivity.this, s, Toast.LENGTH_SHORT).show();
                    SendData();
                    try {
                        Thread.currentThread().sleep(10);//sleep for 1000 ms
                    } catch (InterruptedException ie) {
                    }
                }
            } catch (IOException e) {

            }

        }
    };

    private void SendData() {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            bw.write(s + "\n");
            bw.flush();
        } catch (Exception ex) {

        }
    }

    private Button.OnClickListener SendMessage = new Button.OnClickListener() {
        public void onClick(View v) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                bw.write(edittb_message.getText().toString() + "\n");
                bw.flush();
            } catch (IOException e) {

            }
        }
    };

    private Runnable readData = new Runnable() {
        public void run() {
            try {
                while (true) {
                    if (clientSocket != null) {
                        while (clientSocket.isConnected()) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            tmp = br.readLine();
                            if (tmp != null)
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
            edittb_accept.setText(tmp);
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


