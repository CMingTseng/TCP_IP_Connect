package com.jouhu.tcpserver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static android.R.attr.port;


public class TCPServerThread extends Thread {
    private static final String TAG = "TCPServerThread";
    private Handler mHandler = null;
    //100K
    private static final int BUFF_SIZE = 1024 * 100;
    public ServerSocket mServerSocket = null;
    private boolean running = false;
    private int mPort;
    private byte[] jpegHeader = null;
    public Socket mClient;
    private String mMsg;

    public TCPServerThread(Handler handler, int port) {
        this.mHandler = handler;
        this.mPort = port;
    }

    //將資料傳入 mClient 端
    public void writeData(String buff) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(mClient.getOutputStream()));
        out.write(buff);
        out.flush();
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(port); // 建立 mServerSocket
            Log.v(TAG, "ServerSocket start at port : " + port);
            mServerSocket.setReceiveBufferSize(BUFF_SIZE);
            mServerSocket.setReuseAddress(true);
            mClient = mServerSocket.accept(); // 接受  mClient 端的連線要求
            Log.v(TAG, "ServerSocket a Client come~~~");
            BufferedReader br = new BufferedReader(new InputStreamReader(mClient.getInputStream()));
            running = true;
            while (running) {
                try {
                    mMsg = br.readLine(); // 將 mClient 端傳來的資料,由輸入緩衝器讀入
                    // 收到空字串時判定為斷線
                    if (mMsg == null)
                        break;
                    // 輸出訊息
                    System.out.println(mMsg);
                    // 將訊息 mesg (屬於 C.RECEIVEDATA 的訊息;內含 mMsg)傳入 msgHandler
                    //Way 1:
//                    Message mesg = mHandler.obtainMessage(C.RECEIVEDATA);
//                    mesg.obj = mMsg;
                    //Way 2:
                    Message mesg = mHandler.obtainMessage(C.RECEIVEDATA, mMsg.length(), -1, mMsg);
                    mesg.sendToTarget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Message msg = mHandler.obtainMessage(C.MSG_SERVER_START_ERROR);
            msg.obj = "ServerSocket MSG_SERVER_START_ERROR";
            msg.sendToTarget();
            e.printStackTrace();
            return;
        }
    }
}