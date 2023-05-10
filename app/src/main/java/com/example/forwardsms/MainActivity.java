package com.example.forwardsms;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    EditText forwardText, receiveText, deleteidText;
    Button insert, delete, view;
    ImageButton fContact, rContact;
    DatabaseHelper DB;
    static DatabaseHelper staticDB;
    private static final int PICK_CONTACT_REQUEST_CODE = 101;
    Integer whichBtn = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forwardText=findViewById(R.id.forwardno);
        receiveText=findViewById(R.id.receiveno);
        deleteidText=findViewById(R.id.deleteid);
        insert=findViewById(R.id.insertbtn);
        delete=findViewById(R.id.deletebtn);
        view=findViewById(R.id.viewbtn);
        fContact=findViewById(R.id.forwardContact);
        rContact=findViewById(R.id.receiveContact);
        DB = new DatabaseHelper(this);
        staticDB = DB;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1);
            }
        }

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String receiveNo = receiveText.getText().toString();
                String forwardNo = forwardText.getText().toString();

                if(receiveText.getText().toString().trim().length() <= 0){
                    receiveText.setError("metin girin");
                }
                if(forwardText.getText().toString().trim().length() <= 0){
                    forwardText.setError("metin girin");
                }

                if (receiveText.getText().toString().trim().length() > 0 && forwardText.getText().toString().trim().length() > 0) {
                    // Check if the new entry already exists in the database
                    Cursor res = DB.getdata();
                    boolean entryExists = false;
                    while(res.moveToNext()){
                        String existingReceiveNo = res.getString(1);
                        String existingForwardNo = res.getString(2);
                        if(existingReceiveNo.equals(receiveNo) && existingForwardNo.equals(forwardNo)){
                            entryExists = true;
                            break;
                        }
                    }
                    if(entryExists){
                        Toast.makeText(MainActivity.this, "Entry already exists", Toast.LENGTH_SHORT).show();
                    }else{
                        Boolean checkinsertdata = DB.insertphoneno(receiveNo, forwardNo);
                        if (checkinsertdata == true)
                            Toast.makeText(MainActivity.this, "New Entry Inserted", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "New Entry Not Inserted", Toast.LENGTH_SHORT).show();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        return;
                    }else{
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = deleteidText.getText().toString();

                Boolean checkudeletedata = DB.deletedata(id);

                if(checkudeletedata==true)
                    Toast.makeText(MainActivity.this, "Entry Deleted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Entry Not Deleted", Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor res = DB.getdata();
                if(res.getCount()==0){
                    Toast.makeText(MainActivity.this, "No Entry Exists", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("ID: "+ res.getString(0)+"\n");
                    buffer.append("Gelen: "+res.getString(1)+"\n");
                    buffer.append("Giden: "+res.getString(2)+"\n");
                    buffer.append("\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("User Entries");
                builder.setMessage(buffer.toString());
                builder.show();
            }
        });

        rContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
                whichBtn = 0;
            }
        });
        fContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
                whichBtn = 1;
            }
        });

    }
    private void pickContact() {
        Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContact, PICK_CONTACT_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT_REQUEST_CODE && data != null) {
            Uri contactData = data.getData();
            if (contactData != null) {
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String num = cursor.getString(phoneIndex);
                    Log.d("num", num);
                    Log.d("wb", String.valueOf(whichBtn));
                    if (whichBtn == 0){
                        String phoneNo= num.trim();
                        receiveText.setText(phoneNo);
                    }else if (whichBtn == 1){
                        String phoneNo= num.trim();
                        forwardText.setText(phoneNo);
                    }else{
                        return;
                    }
                    //Close cursor to prevent from memory leak
                    cursor.close();
                }
            }
        }
    }

    public static void forwardSMS(String receiveNo, String SMS,Context context){
        Log.d("a", "okudu");

        Intent serviceIntent = new Intent(context, MyService.class);
        context.startService(serviceIntent);
        Log.d("a", "okudu2");
        new GetDataTask(context, receiveNo, SMS).execute();
    }
}
