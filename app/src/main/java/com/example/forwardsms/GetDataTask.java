package com.example.forwardsms;

import static com.example.forwardsms.MainActivity.staticDB;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class GetDataTask extends AsyncTask<Void, Void, Cursor> {
    private final Context mContext;
    private final String mReceiveNo;
    private final String mSMS;
    private DatabaseHelper mDB;

    public GetDataTask(Context context, String receiveNo, String SMS) {
        mContext = context;
        mReceiveNo = receiveNo;
        mSMS = SMS;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDB = new DatabaseHelper(mContext);
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        return mDB.getdata();

    }

    @Override
    protected void onPostExecute(Cursor res) {
        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()){
            Log.d("a", "okudu2");
            if (res.getString(1).equals(mReceiveNo)){
                Log.d("a", "okudu3");

                Intent serviceIntent = new Intent(mContext, MyService.class);
                mContext.startService(serviceIntent);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(res.getString(2), null, mSMS, null, null);
                Toast.makeText(mContext, "SMS gönderildi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}