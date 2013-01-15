package com.latentflip.notify;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.AsyncTask;


public class SendMessageTask extends AsyncTask<String, Void, String> {
    private static final int DISCOVERY_PORT = 2562;

    private Exception exception;

    private final Context context;

    public SendMessageTask(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... messages) {
        try {
            String msg = messages[0];
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            WifiManager.MulticastLock lock = wifi.createMulticastLock("Log_Tag");

            lock.acquire();
            System.out.println("Got lock");

            DhcpInfo dhcp = wifi.getDhcpInfo();


            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            InetAddress address = InetAddress.getByAddress(quads);

            DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT);
            socket.setBroadcast(true);
            DatagramPacket datagramPacket = new DatagramPacket(msg.getBytes(), msg.length(), address, DISCOVERY_PORT);
            socket.send(datagramPacket);

            System.out.println("Sent message");
            socket.close();

            if (lock.isHeld()) {
                System.out.println("Lock released!!");
                lock.release();
            }


        } catch (Exception e) {
            this.exception = e;
        }
        return "okay";
    }
}