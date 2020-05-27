package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.net.IpSecManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
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
    Button openScanner, generate_qr;
    boolean locked = false;

    String HOST_IP = "192.168.43.223";
    int HOST_PORT = 3000;

    UdpClientHandler udpClientHandler;
    UdpClientThread udpClientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        lock_image = (ImageView) findViewById(R.id.lock_img);
        openScanner = (Button) findViewById(R.id.gotoScanner);
        generate_qr = (Button) findViewById(R.id.generateQR);
        layout.setBackgroundResource(R.color.checking);

        udpClientHandler = new UdpClientHandler(this);

        UdpClientThread udpClientThread = new UdpClientThread(HOST_IP,HOST_PORT,udpClientHandler);
        udpClientThread.start();
    }

    public void scannerPopup(View view){
        Intent intent = new Intent(MainActivity.this, QRCodeScanActivity.class);
        startActivity(intent);
    }

    public void generateQRCode(View view){
        Intent intent = new Intent(MainActivity.this, QRCodeGenerateActivity.class);
        startActivity(intent);
    }

    private void changeBackground(View view, boolean state){

        int colorLocked = 0xFF90FF93;
        int colorUnlocked = 0xFFFF4040;

        ValueAnimator valueAnimator1 = ObjectAnimator.ofInt(view,"BackgroundColor",colorLocked,colorUnlocked);
        ValueAnimator valueAnimator2 = ObjectAnimator.ofInt(view,"BackgroundColor",colorUnlocked,colorLocked);
        if(state == false){

            valueAnimator1.setDuration(1000);
            valueAnimator1.setEvaluator(new ArgbEvaluator());
            valueAnimator1.start();
            Log.d(TAG, "changeBackground: ka");

        }else{

            valueAnimator2.setDuration(1000);
            valueAnimator2.setEvaluator(new ArgbEvaluator());
            valueAnimator2.start();
            Log.d(TAG, "changeBackground: ae");
        }


    }

    public void toggleLockState(View view){

        if(locked == false){
            changeBackground(layout,locked);
            lock_image.setImageResource(R.drawable.locked_icon);
            locked = true;
            Log.d(TAG,"CRVENO");
        }
        else{
            changeBackground(layout,locked);
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
                    parent.toggleLockState(parent.layout);
                    break;
                case UNLOCK:
                    parent.toggleLockState(parent.layout);
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }


}
