package com.soya.launcher.manager;

import android.util.Log;

import com.soya.launcher.bean.MyRunnable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer extends MyRunnable {
    private static final int SERVICE_PORT = 8659;
    private static final int MAX_BYTES = 60 * 1000;

    private DatagramSocket socket;
    private Callback callback;

    public UDPServer(){
        try {
            socket = new DatagramSocket(SERVICE_PORT);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] receiveBytes = new byte[MAX_BYTES];
        DatagramPacket packet = new DatagramPacket(receiveBytes,receiveBytes.length);
        while (!isInterrupt()){
            try {
                socket.receive(packet);
                String data = new String(packet.getData(),0,packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                if (callback != null) callback.callback(data, clientAddress, clientPort);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        destory();
    }

    public boolean send(byte[] bytes, InetAddress address, int port) {
        boolean success = true;
        try {
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
            socket.send(packet);
        }catch (Exception e){
            success = false;
            e.printStackTrace();
        }finally {
            return success;
        }
    }

    public void destory(){
        interrupt();
        if (socket != null) socket.close();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void callback(String data, InetAddress address, int port);
    }
}
