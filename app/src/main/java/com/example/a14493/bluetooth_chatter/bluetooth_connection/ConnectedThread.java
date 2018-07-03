package com.example.a14493.bluetooth_chatter.bluetooth_connection;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 连接上之后的通讯线程，服务器和客户端公用
 */
public class ConnectedThread extends Thread {

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;

    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                if( bytes >0) {

                    /*
                    * 进行转换，这里必须进行转换，因为发送的数据每一个字节是unsigned char类型，范围是0-255
                    * 如果是以byte类型接收，智能表示-128-127，因此需要和0xff相与才能还原到0-255范围，否则
                    * 在发送的单个字节数据大于127的时候接收则会出现错误
                    */

                    char[]  trans=  new char[bytes];
                    for(int i=0;i<bytes;i++)
                    {
                        trans[i]=(char)(buffer[i]&0xff);
                    }

                    //Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA,  new String(buffer, 0, bytes));
                    Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA,  new String(trans));
                    //Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA, buffer);
                    mHandler.sendMessage(message);
                }
                Log.d("GOTMSG", "message size  ：" + bytes + " Bytes");
            } catch (IOException e) {
                mHandler.sendMessage(mHandler.obtainMessage(Constant.MSG_ERROR, e));
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}
