package com.demo.client.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.demo.client.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientFragment extends Fragment {
    private static Handler mHandler = new Handler();
    private Socket mClientSocket = null;
    private EditText mAccept;
    private String mTranTmp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        new Thread(readData).start();
        final View root = inflater.inflate(R.layout.main_fragment, container, false);
        final EditText serverip = (EditText) root.findViewById(R.id.server);
        final EditText port = (EditText) root.findViewById(R.id.ECG_port);
        final EditText input_message = (EditText) root.findViewById(R.id.message);
        mAccept = (EditText) root.findViewById(R.id.ECG_accept);
        final Button submit = (Button) root.findViewById(R.id.submit);
        submit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                InetAddress serverIp;
                try {
                    serverIp = InetAddress.getByName(serverip.getText().toString());
                    int serverPort = Integer.valueOf(port.getText().toString());
                    mClientSocket = new Socket(serverIp, serverPort); // Connect Server
                    if (mClientSocket.isConnected()) {
                        submit.setEnabled(false);
                        String s = "USER NAME=";
                        s += serverip.getText().toString() + " ";
                        s += "SERVER_PORT=";
                        s += port.getText().toString();
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
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
        final Button transmit = (Button) root.findViewById(R.id.transmit);
        transmit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendData(input_message.getText().toString());
            }
        });
        return root;
    }

    private void SendData(String message) {
        if (mClientSocket != null) {
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream()));
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
                    if (mClientSocket != null) {
                        while (mClientSocket.isConnected()) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
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
    public void onStop() {
        try {
            if (mClientSocket != null && mClientSocket.isConnected()) mClientSocket.close();
        } catch (IOException e) {

        }
        super.onStop();
    }
}


