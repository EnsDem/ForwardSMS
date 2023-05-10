package com.example.forwardsms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Phonedatav2.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("create Table Phonenumbers(id INTEGER PRIMARY KEY,receiveno TEXT, forwardno TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop Table if exists Phonenumbers");
    }

    public Boolean insertphoneno(String receiveno, String forwardno){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("receiveno", receiveno);
        contentValues.put("forwardno", forwardno);


        long result=DB.insert("Phonenumbers",null, contentValues);
        if (result==-1)
            return false;
        else
            return true;
    }

       public Boolean deletedata(String id)
    {
        SQLiteDatabase DB = this.getWritableDatabase();

        Cursor cursor = DB.rawQuery("Select * from Phonenumbers where id = ?", new String[]{id.toString()});
        if (cursor.getCount() > 0) {
            long result = DB.delete("Phonenumbers", "id=?", new String[]{id.toString()});
            if (result == -1) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }
    public Cursor getdata ()
    {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from Phonenumbers", null);
        return cursor;
    }
}
