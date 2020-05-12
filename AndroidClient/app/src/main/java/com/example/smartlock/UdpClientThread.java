package com.example.smartlock;

import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClientThread extends Thread {

    static final String TAG = "IVAN";
    String HOST_IP;
    int HOST_PORT;
    boolean running = true;

    DatagramSocket clientSocket;
    MainActivity.UdpClientHandler handler;

    UdpClientThread(String IP, int PORT, MainActivity.UdpClientHandler handler) {
        this.HOST_IP = IP;
        this.HOST_PORT = PORT;
        this.handler = handler;
    }

    private void updateLockState(int state){
        if(state == 1){
            handler.sendMessage(Message.obtain(handler,
                    MainActivity.UdpClientHandler.LOCK, state));
        }
        else if(state == 0){
            handler.sendMessage(Message.obtain(handler,
                    MainActivity.UdpClientHandler.UNLOCK, state));
        }

    }

    @Override
    public void run() {

        running = true;

        try {

            clientSocket = new DatagramSocket();

            String initial_message = "Hello, client here!";
            byte[] msg = initial_message.getBytes();
            byte[] received_data = new byte[64];

            DatagramPacket dPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(HOST_IP), HOST_PORT);
            clientSocket.send(dPacket);

            dPacket = new DatagramPacket(received_data,received_data.length);
            clientSocket.receive(dPacket);

            String received_message = new String(dPacket.getData(), 0, dPacket.getLength());

            if(received_message.equals("Hello from server")){
                Log.d(TAG, "Connected to server");
            }

            while(running){

                dPacket = new DatagramPacket(received_data,received_data.length);
                clientSocket.receive(dPacket);

                received_message = new String(dPacket.getData(), 0, dPacket.getLength());

                if(received_message.equals("LOCKED")){
                    updateLockState(1);
                }
                else if(received_message.equals("UNLOCKED")){
                    updateLockState(0);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

