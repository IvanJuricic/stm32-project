package com.example.smartlock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    private EditText passwordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordInput = (EditText)findViewById(R.id.editText);
    }

    public void sendData(View v){

        String message = passwordInput.getText().toString();

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(message);
    }

    class BackgroundTask extends AsyncTask<String,Void,Void>{

        Socket clientSocket;
        PrintWriter printWriter;

        @Override
        protected Void doInBackground(String... voids) {
            try{
                String message = voids[0];
                clientSocket = new Socket("192.168.1.6", 3000);
                printWriter = new PrintWriter(clientSocket.getOutputStream());
                printWriter.write(message);
                printWriter.flush();
                printWriter.close();

            }catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
