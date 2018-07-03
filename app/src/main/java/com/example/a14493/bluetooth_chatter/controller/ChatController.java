package com.example.a14493.bluetooth_chatter.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

import com.example.a14493.bluetooth_chatter.bluetooth_connection.AcceptThread;
import com.example.a14493.bluetooth_chatter.bluetooth_connection.ConnectThread;
import com.example.a14493.bluetooth_chatter.bluetooth_connection.Constant;
import com.example.a14493.bluetooth_chatter.bluetooth_connection.ProtocolHandler;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatController {
    private ConnectThread mConnectThread;
    private AcceptThread mAcceptThread;

    private static int byte_length_acc=0;    //累计收到的字节数
    private static String string_rec_acc=""; //累计收到的的字符


    private  class ChatProtocol implements ProtocolHandler<String> {

        private static final String CHARSET_NAME = "ascii";


        /**
         * 编码
         * @param data
         * @return
         */
        @Override
        public byte[] encodePackage(String data) {
            if( data == null) {
                return new byte[0];
            }

            else {
                try {
                    return data.getBytes(CHARSET_NAME);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return new byte[0];
                }
            }
        }

        /**
         * 解码
         * @param netData
         * @return
         */
        @Override
        public String decodePackage(byte[] netData)
        {
            if( netData == null) {
                return "";
            }
            try {
                return new String(netData, CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * 协议处理
     */
    private ChatProtocol mProtocol = new ChatProtocol();


    /**
     * 与服务器连接进行聊天
     * @param device
     * @param adapter
     * @param handler
     */
    public void startChatWith(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        mConnectThread = new ConnectThread(device,adapter,handler);
        mConnectThread.start();
    }


    /**
     * 等待客户端来连接
     * @param adapter
     * @param handler
     */
    public void waitingForConnection(BluetoothAdapter adapter, Handler handler) {
        mAcceptThread = new AcceptThread(adapter,handler);
        mAcceptThread.start();
    }


    /**
     * 发出消息
     * @param msg
     */
    public void sendMessage(String msg) {
        byte[] data = mProtocol.encodePackage(msg);
        if(mConnectThread != null) {
            mConnectThread.sendData(data);
        }
        else if( mAcceptThread != null) {
            mAcceptThread.sendData(data);
        }
    }

    /**
     * 网络数据解码
     * @param data
     * @return
     */
    public String decodeMessage(byte[] data) {
        return  mProtocol.decodePackage(data);
    }

    /**
     * 停止聊天
     */
    public void stopChat() {
        if(mConnectThread != null) {
            mConnectThread.cancel();
        }
        else if( mAcceptThread != null) {
            mAcceptThread.cancel();
        }
    }

    /**
     * 单例的写法
     */
    private static class ChatControlHolder {
        private static ChatController mInstance = new ChatController();
    }

    public static ChatController getInstance() {
        return ChatControlHolder.mInstance;
    }



}
