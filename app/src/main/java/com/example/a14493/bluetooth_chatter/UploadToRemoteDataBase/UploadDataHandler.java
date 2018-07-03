package com.example.a14493.bluetooth_chatter.UploadToRemoteDataBase;


import android.util.Log;
import android.widget.Toast;

import com.example.a14493.bluetooth_chatter.CombineRelated.CombineDataFrame;
import com.example.a14493.bluetooth_chatter.MainActivity;

public class UploadDataHandler {

    public static String CombineDataHandler(CombineDataFrame combineDataFrame)
    {
        String params="action=upload&"+"CANID="+combineDataFrame.getCANID()+"&"
                +"DATA="+combineDataFrame.getDATA()+"&"
                +"TIMESTAMP="+combineDataFrame.getTimeStamp();
        return params;
    }

}
