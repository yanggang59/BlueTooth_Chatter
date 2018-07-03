package com.example.a14493.bluetooth_chatter.bluetooth_connection;


/**
 * 用到的一些常量定义在这里
 */
public class Constant {

    public static final String CONNECTTION_UUID = "00001101-0000-1000-8000-00805F9B34FB";


    /**
     * 开始监听
     */

    public static final int MSG_START_LISTENING = 1;

    /**
     * 结束监听
     */
    public static final int MSG_FINISH_LISTENING = 2;

    /**
     * 有客户端连接
     */
    public static final int MSG_GOT_A_CLINET = 3;

    /**
     * 连接到服务器
     */
    public static final int MSG_CONNECTED_TO_SERVER = 4;

    /**
     * 获取到数据
     */
    public static final int MSG_GOT_DATA = 5;

    /**
     * 出错
     */
    public static final int MSG_ERROR = -1;


    public static final int LOCAL_SAVING_MODE=6;

    public static final int REMOTE_SAVING_MODE=7;

    public static final int LOCAL_AND_REMOTE_SAVING_MODE=8;

    /**
     * 发送数据的长度
     */
    public static final int BYTE_LENGTH = 20;


    /**
     * 数据头 $
     */
    public static final char head='$';


    /**
     * 数据尾  #
     */
    public static final char tail='#';

}
