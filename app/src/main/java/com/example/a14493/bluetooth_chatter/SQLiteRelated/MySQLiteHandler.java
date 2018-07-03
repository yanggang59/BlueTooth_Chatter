package com.example.a14493.bluetooth_chatter.SQLiteRelated;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.a14493.bluetooth_chatter.CombineRelated.CombineDataFrame;

import java.util.ArrayList;
import java.util.List;

public class MySQLiteHandler extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "CombineData.db";

    // Computer Table Name
    private static final String TABLE_COMBINEDATA = "CombineData";

    // CombineData Table Columns
    private static final String COLUMN_COMBINEDATA_ID = "id";
    //private static final String COLUMN_COMBINEDATA_HEAD = "CombineDataHEAD";
    private static final String COLUMN_COMBINEDATA_CANID = "CombineDataCANID";
    private static final String COLUMN_COMBINEDATA_DATA = "CombineDataDATA";
    private static final String COLUMN_COMBINEDATA_TIMESTAMP = "CombineDataTimeStamp";
    //private static final String COLUMN_COMBINEDATA_TAIL = "CombineDataTail";


    String CREATE_COMBINEDATA_TABLE = "CREATE TABLE " + TABLE_COMBINEDATA + "(" + COLUMN_COMBINEDATA_ID +
            " INTEGER PRIMARY KEY, "  +
            COLUMN_COMBINEDATA_CANID + " TEXT, " +
            COLUMN_COMBINEDATA_DATA + " TEXT, " +
            COLUMN_COMBINEDATA_TIMESTAMP + " TEXT " + ")";

    public MySQLiteHandler(Context context) {


        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_COMBINEDATA_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMBINEDATA);

        onCreate(db);

    }


    // All Database Operations: create, read, update, delete

    // create
    public void addCombineData(CombineDataFrame combine_data_frame) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COLUMN_COMBINEDATA_HEAD, combine_data_frame.getHead());
        values.put(COLUMN_COMBINEDATA_CANID, combine_data_frame.getCANID());
        values.put(COLUMN_COMBINEDATA_DATA, combine_data_frame.getDATA());
        values.put(COLUMN_COMBINEDATA_TIMESTAMP, combine_data_frame.getTimeStamp());
        //values.put(COLUMN_COMBINEDATA_TAIL, combine_data_frame.getTail());

        database.insert(TABLE_COMBINEDATA, null, values);


        database.close();

    }




    // Getting a single CombineData - read
    public CombineDataFrame getCombineDataByID(int id) {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_COMBINEDATA, new String[]{COLUMN_COMBINEDATA_ID,
                        COLUMN_COMBINEDATA_CANID, COLUMN_COMBINEDATA_DATA,
                        COLUMN_COMBINEDATA_TIMESTAMP},
                COLUMN_COMBINEDATA_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);


        if (cursor != null)  {
            cursor.moveToFirst();
        }

        CombineDataFrame combine_data_frame = new CombineDataFrame(Integer.parseInt(cursor.getString(0)),
                 cursor.getString(2),cursor.getString(3),cursor.getString(4));
        return combine_data_frame;


    }



    // Getting all CombineData Objects
    public List<CombineDataFrame> getAllCombineData() {

        List<CombineDataFrame> CombineDataList = new ArrayList<>();

        String selectAllQuery = "SELECT * FROM " + TABLE_COMBINEDATA;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectAllQuery, null);


        if (cursor.moveToFirst()) {

            do {

                CombineDataFrame combine_data_frame = new CombineDataFrame();

                combine_data_frame.setId(Integer.parseInt(cursor.getString(0)));
                //combine_data_frame.setHead(cursor.getString(1));
                combine_data_frame.setCANID(cursor.getString(1));
                combine_data_frame.setDATA(cursor.getString(2));
                combine_data_frame.setTimeStamp(cursor.getString(3));
                //combine_data_frame.setTail(cursor.getString(5));


                CombineDataList.add(combine_data_frame);

            } while (cursor.moveToNext());


        }

        return CombineDataList;


    }





    // Updating a single computer

    public int updateComputer(CombineDataFrame combine_data_frame) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(COLUMN_COMBINEDATA_HEAD, combine_data_frame.getHead());
        values.put(COLUMN_COMBINEDATA_CANID, combine_data_frame.getCANID());
        values.put(COLUMN_COMBINEDATA_DATA, combine_data_frame.getDATA());
        values.put(COLUMN_COMBINEDATA_TIMESTAMP, combine_data_frame.getCANID());
        //values.put(COLUMN_COMBINEDATA_TAIL, combine_data_frame.getTail());

        return database.update(TABLE_COMBINEDATA, values, COLUMN_COMBINEDATA_ID + " = ? ",
                new String[] {String.valueOf(combine_data_frame.getId())});


    }



    // Deleteing a single computer
    public void deleteComputer(CombineDataFrame combine_data_frame) {

        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_COMBINEDATA, COLUMN_COMBINEDATA_ID + " = ?",
                new String[]{String.valueOf(combine_data_frame.getId())});
        database.close();


    }



    // Getting the number of computers

    public int getComputersCount() {
        String computersCountQuery = "SELECT * FROM " + TABLE_COMBINEDATA;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(computersCountQuery, null);
        cursor.close();

        return cursor.getCount();
    }




}
