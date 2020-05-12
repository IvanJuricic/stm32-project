package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.net.IpSecManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "IVAN";
    ConstraintLayout layout;
    ImageView lock_image;
    boolean locked = false;

    String HOST_IP = "192.168.1.2";
    int HOST_PORT = 3000;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        lock_image = (ImageView) findViewById(R.id.lock_img);

        layout.setBackgroundResource(R.color.checking);

        udpClientHandler = new UdpClientHandler(this);
        Log.d(TAG, "onCreate:");
        UdpClientThread udpClientThread = new UdpClientThread(HOST_IP,HOST_PORT,udpClientHandler);
        udpClientThread.start();
    }

    public void toggleLockState(){

        if(locked == false){
            layout.setBackgroundResource(R.color.locked);
            lock_image.setImageResource(R.drawable.locked_icon);
            locked = true;
            Log.d(TAG,"CRVENO");
        }
        else{
            layout.setBackgroundResource(R.color.unlocked);
            lock_image.setImageResource(R.drawable.unlocked_icon);
            locked = false;
            Log.d(TAG,"ZELENO");
        }
    }

    public static class UdpClientHandler extends Handler {
        public static final int LOCK = 1;
        public static final int UNLOCK = 0;

        private MainActivity parent;

        public UdpClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case LOCK:
                    parent.toggleLockState();
                    break;
                case UNLOCK:
                    parent.toggleLockState();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }


}
