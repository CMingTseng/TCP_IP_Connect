package com.jouhu.tcpserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class TCPServerThread extends Thread {
    private static final String tag = "TCPServerThread";
    private Handler handler = null;
    //100K
    private static final int BUFF_SIZE = 1024 * 100;
    public ServerSocket sock = null;
    private boolean running = false;
    private int port;
    private Context context = null;
    private byte[] jpegHeader = null;
    public Socket client;

    public TCPServerThread(Handler handler,int port,Context context)
    {
        this.handler = handler;
        this.port = port;
        this.context = context;

        try {
            sock = new ServerSocket(port); // 建立 sock 
            Log.v(tag, "ServerSocket start at port");
            sock.setReceiveBufferSize(BUFF_SIZE);
            sock.setReuseAddress(true);

        } catch (IOException e) {
            Message msg = handler.obtainMessage(AndroidTCPServerActivity.MSG_SERVER_START_ERROR);
            msg.obj = "ServerSocket MSG_SERVER_START_ERROR";
            msg.sendToTarget();
            e.printStackTrace();
        }
    }
    //將資料傳入 client 端
    public void writeData(String buff) throws IOException
    {
        BufferedWriter out=	new BufferedWriter( new OutputStreamWriter(client.getOutputStream()));
        out.write(buff);
        out.flush();
    }

    private String msg;
    public void run()
    {
        try {
            client = sock.accept(); // 接受  client 端的連線要求
            Log.v(tag, "ServerSocket a client come~~~");
            //InputStream is = client.getInputStream();
        	BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            running = true;
            while(running)
            {
                try
                {
                	msg= br.readLine(); // 將 client 端傳來的資料,由輸入緩衝器讀入
					//mHandler.obtainMessage(ServerSocket_android_demo.MESSAGE_READ, msg.length(), -1, msg);
					
					// 收到空字串時判定為斷線
					if (msg==null)
						break;
					
					// 輸出訊息 
					System.out.println(msg);
					// 將訊息 mesg (屬於 AndroidTCPServerActivity.RECEIVEDATA 的訊息;內含 msg)
					// 傳入 msgHandler 
					Message mesg = handler.obtainMessage(AndroidTCPServerActivity.RECEIVEDATA);
					mesg.obj = msg;
					mesg.sendToTarget();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }
}