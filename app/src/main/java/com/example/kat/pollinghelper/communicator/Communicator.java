package com.example.kat.pollinghelper.communicator;

/**
 * Created by KAT on 2016/5/3.
 */
public abstract class Communicator {
    public interface CommunicationParameter {
    }

    public interface OnDataReceivedListener {
        void onDataReceived(byte[] data);
    }

    public interface OnErrorOccuredListener {
        void onErrorOccured(Exception error);
    }

    public interface OnConnectedListener {
        void onConnected();
    }

    public void setDataReceivedListener(OnDataReceivedListener dataReceivedListener) {
        this.dataReceivedListener = dataReceivedListener;
    }

    public void setErrorOccuredListener(OnErrorOccuredListener errorOccuredListener) {
        this.errorOccuredListener = errorOccuredListener;
    }

    public void setConnectedListener(OnConnectedListener connectedListener) {
        this.connectedListener = connectedListener;
    }

    public Communicator() {
        close();
        onConnectExecutor = new OnConnectExecutor();
        onReceiveDataExecutor = new OnReceiveDataExecutor();
        onSendDataExecutor = new OnSendDataExecutor();
        //sendDataThread = new Thread(onSendDataExecutor);
    }

    public abstract CommunicationParameter getParameter();

    public boolean launch(CommunicationParameter localPara) {
        if (!launched) {
            try {
                localCommPara = localPara;
                onLaunch();
                launched = true;
            } catch (Exception e) {
                onErrorProcess(e);
            }
        }
        return launched;
    }

    public boolean launch() {
        return launch(autoGenerateLocalCommPara());
    }

    protected CommunicationParameter autoGenerateLocalCommPara() {
        return null;
    }

    protected abstract void onLaunch() throws Exception;

    public void connect(CommunicationParameter connectPara, boolean isAsynchronous) {
        if (launched) {
            connectCommPara = connectPara;
            if (isAsynchronous) {
                Thread connectThread = new Thread(onConnectExecutor);
                connectThread.start();
            } else {
                onConnectExecutor.run();
            }
        }
    }

    protected class OnConnectExecutor implements Runnable {

        @Override
        public void run() {
            try {
                onConnect(connectCommPara);
                connected = true;
            } catch (Exception e) {
                connected = false;
                errorOccuredListener.onErrorOccured(e);
            } finally {
                if (connected) {
                    if (connectedListener != null) {
                        connectedListener.onConnected();
                    }
                } else {
                    connectCommPara = null;
                }
            }
        }
    }

    protected abstract void onConnect(CommunicationParameter connectPara) throws Exception;

    protected class OnSendDataExecutor implements Runnable {

        @Override
        public void run() {
            while (connected && circulateTime >= 0) {
                try {
                    if (circulateTime == 0) {
                        if (enableSendDataInAnotherThread) {
                            onSendData(tmpSendData, remoteCommPara);
                            enableSendDataInAnotherThread = false;
                        }
                        Thread.sleep(MIN_SEND_DATA_TIME_INTERVAL);
                    } else {
                        onSendData(tmpSendData, remoteCommPara);
                        Thread.sleep(circulateTime);
                    }
                } catch (Exception e) {
                    onErrorProcess(e);
                }
            }
        }
    }

    //circulateTime < 0, 在当前调用线程发送数据
    //circulateTime = 0, 在后台发送线程发送数据
    //circulateTime > 0, 以circulateTime为时间间隔循环发送数据
    public void sendData(byte[] data, CommunicationParameter remotePara, int circulateTime) {
        if (connected) {
            try {
                if (circulateTime >= 0) {
                    tmpSendData = data;
                    remoteCommPara = remotePara;
                    this.circulateTime = circulateTime;
                    if (circulateTime == 0) {
                        enableSendDataInAnotherThread = true;
                    }
                    if (sendDataThread == null || !sendDataThread.isAlive()) {
                        sendDataThread = new Thread(onSendDataExecutor);
                        sendDataThread.start();
                    }
//                    if (!sendDataThread.isAlive()) {
//                        //重启线程
//                        sendDataThread.start();
//                    }
                } else {
                    onSendData(data, remotePara);
                }
            } catch (Exception e) {
                onErrorProcess(e);
            }
        }
    }

    public void sendData(byte[] data, int circulateTime) {
        sendData(data, connectCommPara, circulateTime);
    }

    //默认在同一线程中传递数据
    public void sendData(byte[] data, CommunicationParameter remotePara) {
        sendData(data, remotePara, -1);
    }

    //默认在同一线程中向已连接方传递数据
    public void sendData(byte[] data) {
        sendData(data, connectCommPara, -1);
    }

    protected abstract void onSendData(byte[] data, CommunicationParameter remotePara) throws Exception;

    public synchronized boolean setCirculateTime(int newTime) {
        if (newTime >= 0 && tmpSendData == null)
            return false;

        circulateTime = newTime;
        return true;
    }

    public void startListen(boolean isAsynchronous) {
        if (connected && !listening) {
            listening = true;
            paused = false;
            if (isAsynchronous) {
                Thread receiveDataThread = new Thread(onReceiveDataExecutor);
                receiveDataThread.start();
            } else {
                onReceiveDataExecutor.run();
            }
        }
    }

    public void stopListen() {
        listening = false;
    }

    protected class OnReceiveDataExecutor implements Runnable {
        @Override
        public void run() {
            while (listening) {
                if (!paused) {
                    try {
                        byte[] recvData = onReceiveData();
                        if (recvData != null && dataReceivedListener != null) {
                            dataReceivedListener.onDataReceived(recvData);
                        }
                    } catch (Exception e) {
                        onErrorProcess(e);
                    }
                }
            }
        }
    }

    protected void onErrorProcess(Exception e) {
        if (errorOccuredListener != null) {
            errorOccuredListener.onErrorOccured(e);
        }
    }

    protected abstract byte[] onReceiveData() throws Exception;

    public void close() {
        try {
            onClose();
            launched = false;
            connected = false;
            listening = false;
            paused = false;
            enableSendDataInAnotherThread = false;
            circulateTime = -1;
            localCommPara = null;
            connectCommPara = null;
            remoteCommPara = null;
            sendDataThread = null;
        } catch (Exception e) {
            onErrorProcess(e);
        }
    }

    protected abstract void onClose();

    public boolean isLaunched() {
        return launched;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isListening() {
        return listening;
    }

    public void pause() {
        paused = true;
    }

    public void reStart() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public CommunicationParameter getLocalCommPara() {
        return localCommPara;
    }

    public CommunicationParameter getConnectCommPara() {
        return connectCommPara;
    }

    public CommunicationParameter getRemoteCommPara() {
        return remoteCommPara;
    }

//    public boolean isSendDataInAnotherThread() {
//        return sendDataInAnotherThread;
//    }
//
//    public void setSendDataInAnotherThread(boolean sendDataInAnotherThread) {
//        this.sendDataInAnotherThread = sendDataInAnotherThread;
//    }

//    public boolean setCirculateSendParameter(byte[] data, int circulateTime) {
//        if (data == null || circulateTime <= 0) {
//            return false;
//        }
//        tmpSendData = data;
//        this.circulateTime = circulateTime;
//        //sendDataInAnotherThread = true;
//        return true;
//    }

    private static final int MIN_SEND_DATA_TIME_INTERVAL = 10;
    private int circulateTime;
    private Thread sendDataThread;
    private boolean enableSendDataInAnotherThread;
    private OnSendDataExecutor onSendDataExecutor;
    private OnReceiveDataExecutor onReceiveDataExecutor;
    private OnConnectExecutor onConnectExecutor;
    private boolean launched;
    private boolean connected;
    private boolean listening;
    private boolean paused;
    private byte[] tmpSendData;
    private CommunicationParameter localCommPara;
    private CommunicationParameter connectCommPara;
    private CommunicationParameter remoteCommPara;
    private OnDataReceivedListener dataReceivedListener;
    private OnErrorOccuredListener errorOccuredListener;
    private OnConnectedListener connectedListener;
}
