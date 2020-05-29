package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    static final String TAG = "IVAN";
    ConstraintLayout layout;
    ImageView lock_image;
    //Button openScanner;
    ImageButton generate_qr;
    Button dropBtn;
    boolean locked = false;

    String HOST_IP = "192.168.43.223";
    int HOST_PORT = 3000;

    private Client client;
    Thread listen, run, connect;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lock_image = (ImageView) findViewById(R.id.lock_img);
        //openScanner = (Button) findViewById(R.id.gotoScanner);
        generate_qr = (ImageButton) findViewById(R.id.generateQR);
        dropBtn = (Button) findViewById(R.id.dropUsers);

        mContext = MainActivity.this;

        Thread t = null;
        try {
            t = new Thread(client = new Client(HOST_IP, HOST_PORT,mContext));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        t.start();

        lock_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(locked == false){
                    String message = "LOCK!";
                    send(message);
                    toggleLockState(locked);
                    locked = true;
                }else if(locked == true){
                    String message = "UNLOCK!";
                    send(message);
                    toggleLockState(locked);
                    locked = false;
                }

            }
        });

        dropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Drop all!";
                send(msg);
            }
        });
    }

    public void send(String message) {
        client.send(message);
    }

    public void generateQRCode(View view){
        Intent intent = new Intent(MainActivity.this, QRCodeGenerateActivity.class);
        startActivity(intent);
    }

    public void toastMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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

    public void toggleLockState(boolean locked){

        if(locked == false){
            changeBackground(layout,locked);
            lock_image.setImageResource(R.drawable.locked_icon);
            //Log.d(TAG,"CRVENO");
        }
        else{
            changeBackground(layout,locked);
            lock_image.setImageResource(R.drawable.unlocked_icon);
            //Log.d(TAG,"ZELENO");
        }
    }


}