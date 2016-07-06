package com.example.kat.pollinghelper.communicator;

import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by KAT on 2016/6/14.
 */
public class Udp extends Communicator {

    public class UdpParameter implements CommunicationParameter {
        public InetAddress getAddress() {
            return address;
        }

        public UdpParameter setAddress(InetAddress address) {
            this.address = address;
            return this;
        }

        public UdpParameter setAddress(String addressName) {
            try {
                this.address = InetAddress.getByName(addressName);
            } catch (UnknownHostException e) {
                onErrorProcess(e);
            }
            return this;
        }

        public int getPort() {
            return port;
        }

        public UdpParameter setPort(int port) {
            this.port = port;
            return this;
        }

        private int port;
        private InetAddress address;
    }

    @Override
    public UdpParameter getParameter() {
        return new UdpParameter();
    }

    @Override
    protected void onLaunch() throws SocketException {
        UdpParameter localPara = (UdpParameter)getLocalCommPara();
        if (localPara == null) {
            socket = new DatagramSocket();
        } else {
            socket = new DatagramSocket(localPara.getPort());
        }
        byte[] data = new byte[MAX_RECEIVE_DATA_LEN];
        sendPacket = new DatagramPacket(data, data.length);
        receivePacket = new DatagramPacket(data, data.length);
    }

    @Override
    protected void onConnect(CommunicationParameter connectPara) {
    }

    @Override
    protected void onSendData(byte[] data, CommunicationParameter remotePara) throws IOException {
        UdpParameter serverPara = (UdpParameter)remotePara;
        sendPacket.setData(data);
        sendPacket.setAddress(serverPara.getAddress());
        sendPacket.setPort(serverPara.getPort());
        socket.send(sendPacket);
        //Log.d("PollingHelper", "data sent");
    }

    @Override
    protected byte[] onReceiveData() throws IOException {
        //Arrays.fill(receivePacket.getData(), (byte) 0x00);
        socket.receive(receivePacket);
        //Log.d("PollingHelper", "data received");
        return receivePacket.getData();
    }

    @Override
    protected void onClose() {
        if (socket != null) {
            socket.close();
            socket = null;
        }
    }

    @Override
    public void stopListen() {
        super.stopListen();
    }

    private static final int MAX_RECEIVE_DATA_LEN = 255;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket socket;
}
