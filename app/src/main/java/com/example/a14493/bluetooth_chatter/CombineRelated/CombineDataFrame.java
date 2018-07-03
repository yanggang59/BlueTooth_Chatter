package com.example.a14493.bluetooth_chatter.CombineRelated;

import com.example.a14493.bluetooth_chatter.bluetooth_connection.Constant;

public class CombineDataFrame {
    private int id;
    //private String head;          //帧头  , 1 byte
    private String CANID;           //CAN id , 8 bytes
    private String DATA;            //数据,    8 bytes
    private String TimeStamp;       //时间戳  ,3 bytes
    //private String tail;          //帧尾 ,1 byte

    //An Empty Constructor
    public CombineDataFrame()
    {
        super();
    }

    public CombineDataFrame(int id,String CANID,String DATA,String TimeStamp)
    {
        this.id=id;
        //this.head=head;
        this.CANID=CANID;
        this.DATA=DATA;
        this.TimeStamp=TimeStamp;
        //this.tail=tail;
    }


    public CombineDataFrame(String strRec)
    {
        assert (strRec.length()== Constant.BYTE_LENGTH);
        String mCANID=strRec.substring(1,5);       //4字节CANID
        String mDATA=strRec.substring(5,13);       //8字节数据
        String mTIMESTAMP=strRec.substring(13,19); //6字节，年月日时分秒

        this.CANID=mCANID;
        this.DATA=mDATA;
        this.TimeStamp=mTIMESTAMP;

    }

    public CombineDataFrame(String CANID,String DATA,String TimeStamp)
    {
        //this.head=head;
        this.CANID=CANID;
        this.DATA=DATA;
        this.TimeStamp=TimeStamp;
        //this.tail=tail;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public String getHead() {
//        return head;
//    }
//
//    public void setHead(String head) {
//        this.head = head;
//    }

    public String getCANID() {
        return CANID;
    }

    public void setCANID(String CANID) {
        this.CANID = CANID;
    }

    public String getDATA() {
        return DATA;
    }

    public void setDATA(String DATA) {
        this.DATA = DATA;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

//    public String getTail() {
//        return tail;
//    }
//
//    public void setTail(String tail) {
//        this.tail = tail;
//    }
}
