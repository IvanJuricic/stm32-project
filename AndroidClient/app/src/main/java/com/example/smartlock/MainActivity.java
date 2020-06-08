package com.example.smartlock;

import androidx.annotation.Nullable;
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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
    ImageButton openScanner;
    //Button generate_qr, dropBtn;
    boolean locked = false;

    String HOST_IP ;
    int HOST_PORT ;

    private Client client;
    Thread listen, run, connect;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lock_image = (ImageView) findViewById(R.id.lock_img);
        openScanner = (ImageButton) findViewById(R.id.gotoScanner);
        //generate_qr = (Button) findViewById(R.id.generateQR);
        //dropBtn = (Button) findViewById(R.id.dropUsers);

        mContext = MainActivity.this;
        final Activity activity = this;

        openScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this,"Cancelled/Invalid QR Code", Toast.LENGTH_LONG).show();
            }
            else{
                HOST_IP = result.getContents().split("\\:")[0];
                HOST_PORT = Integer.parseInt(result.getContents().split("\\:")[1]);

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

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void send(String message) {
        client.send(message);
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