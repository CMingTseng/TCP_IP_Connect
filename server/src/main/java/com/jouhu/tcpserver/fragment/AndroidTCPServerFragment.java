package com.jouhu.tcpserver.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jouhu.tcpserver.C;
import com.jouhu.tcpserver.R;
import com.jouhu.tcpserver.TCPServerThread;

import java.io.IOException;

public class AndroidTCPServerFragment extends Fragment {
    private static final String TAG = "AndroidTCPServerFragment";
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context context = container.getContext();
        mSocketServer = new TCPServerThread(mMsgHandler, PORT);
        mSocketServer.start();
        final View root = inflater.inflate(R.layout.main_fragment, container, false);
        final Button transmit = (Button) root.findViewById(R.id.transmit);
        final EditText transmitData = (EditText) root.findViewById(R.id.transmit_data);
        transmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSocketServer.writeData(transmitData.getText().toString() + "\n");
                } catch (IOException e) {

                }
            }
        });
        mMessageInfo = (EditText) root.findViewById(R.id.editText1);
        mMessageInfo.setText("Server Start At Port " + String.valueOf(PORT));
        return root;
    }

    @Override
    public void onPause() {
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
