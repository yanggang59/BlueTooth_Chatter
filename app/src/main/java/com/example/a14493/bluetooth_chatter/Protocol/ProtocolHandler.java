package com.example.a14493.bluetooth_chatter.Protocol;


import android.util.Log;

import com.example.a14493.bluetooth_chatter.bluetooth_connection.Constant;

/**
 * 协议层
 */
public class ProtocolHandler {

    //累计收到的字符串
    private static String StringRecAcc;


    public void ProtocolHandler()
    {
        setStringRecAcc("");
    }

    public  String getStringRecAcc() {
        return StringRecAcc;
    }

    public  void setStringRecAcc(String stringRecAcc) {
        StringRecAcc = stringRecAcc;
    }

    public int getStringRecAccLength()
    {
        return StringRecAcc.length();
    }

    public String handleStringRec(Object object)
    {
        String tmp=String.valueOf(object);

        /**
         * 只有当数据的数据头不为$
         */
        if(tmp.charAt(0) != Constant.head)
        {
            if(tmp.length() < Constant.BYTE_LENGTH)
                StringRecAcc+=tmp;
        }
        else
        {
            setStringRecAcc("");
            StringRecAcc+=tmp;
        }

        if(StringRecAcc.length()>=Constant.BYTE_LENGTH)
        {
            if(validateData(StringRecAcc))
            {
                String ret=StringRecAcc;
                setStringRecAcc("");
                return ret;
            }
            else
                return "";

        }
        else
        {
            return "";
        }

    }


    /**
     * 数据格式校验,包括帧头和帧尾校验，帧长度校验
     */
    public boolean validateData(String mStringRecAcc)
    {
        int mlength=mStringRecAcc.length();
        char mhead=mStringRecAcc.charAt(0);
        char mTail=mStringRecAcc.charAt(Constant.BYTE_LENGTH-1);
        if((mlength == Constant.BYTE_LENGTH) && (mhead == Constant.head) && (mTail == Constant.tail))
        {
                Log.d("TAG","Right Format");
                return true;
        }
        return false;
    }


}
