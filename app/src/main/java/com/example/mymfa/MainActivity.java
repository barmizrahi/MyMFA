package com.example.mymfa;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List contactsInfoList;
    private MaterialTextView info;
    private MaterialButton refresh;
    private MaterialButton request;
    private MaterialButton stop;
    private TextInputEditText passWord;
    AudioChecker audioChecker;

    private String names;
    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;
    private String path;
    private boolean boolRecord;
    private boolean boolContact;
    private boolean boolBattery;
    private boolean boolPass;

    private final int REQUEST_CODE_PERMISSION_CONTACTS = 900;
    private final int REQUEST_CODE_PERMISSION_CAMERA = 901;
    private final int REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 902;
    private final int REQUEST_CODE_PERMISSION_MICROPHONE = 903;
    private final String PASSWORD = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        requestAllPermissions();
        audioChecker = new AudioChecker(stop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1)
            audioChecker.setPath(this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/"  + "recordingAudio.mp3");
        else
            audioChecker.setPath(Environment.getExternalStorageDirectory().toString() + "/"  + "recordingAudio.mp3");
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    private void refresh() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        boolean r1 =  ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        boolean r2 =  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if(result && r1 && r2) {
            getContacts();
            audioChecker.getVoice();
            audioChecker.playVoice();
            checkPassWord();
        }

        
    }

    private void checkPassWord() {
        if(PASSWORD.equals(passWord.getText().toString()))
            boolPass = true;
        else
            boolPass = false;
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level * 100 / (float)scale;
            if(batteryPct > 40){
                boolBattery = true;
            }
            else
                boolBattery = false;
            String b = String.valueOf(batteryPct) + "%";

        }
    };
    private void getVoice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
            audioChecker.setPath(this.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/"  + "recordingAudio.mp3");
           // path = this.getExternalFilesDir
                   // (Environment.DIRECTORY_DCIM) + "/"  + "recordingAudio.mp3";
        }
        else
        {
            audioChecker.setPath(Environment.getExternalStorageDirectory()
                    .toString() + "/"  + "recordingAudio.mp3");
           // path=Environment.getExternalStorageDirectory()
                 //   .toString() + "/"  + "recordingAudio.mp3";
        }
      //  path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "recordingAudio.mp3";
      /*
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setOutputFile(path);
        try {
            recorder.prepare();
            recorder.start();
            stop.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }


       */
    }
    private void StopListen() {
        audioChecker.getMediaRecorder().stop();
        audioChecker.getMediaRecorder().release();
        playVoice();
        stop.setVisibility(View.GONE);
        if(boolContact && audioChecker.getBoolRecord() && boolBattery && boolPass)
        {
            Toast.makeText(MainActivity.this, "Access Granted", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(MainActivity.this, "Access Not Granted", Toast.LENGTH_SHORT).show();
    }
    private void playVoice(){
        audioChecker.playVoice();
    }


    private void requestAllPermissions() {
       // ContactRequest();
       // MicRequest();
        //StorageRequest();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_CONTACTS); //request for contact
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION_MICROPHONE);//request for mircrophone
       // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);//request for mircrophone
    }
/*
    private void StorageRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE);
    }

    private void MicRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION_MICROPHONE);
    }

    private void ContactRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_PERMISSION_CONTACTS);
    }

*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CONTACTS: {
                //Log.d("pttt", "REQUEST_CODE_PERMISSION_CONTACTS");

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // refresh();
                    Toast.makeText(MainActivity.this, "Contacts ok", Toast.LENGTH_SHORT).show();
                } else {
                    //requestContacts();
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case REQUEST_CODE_PERMISSION_MICROPHONE: {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("pttt", "REQUEST_CODE_PERMISSION_MICROPHONE");
                 //   refresh();
                    Toast.makeText(MainActivity.this, "Mic ok", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to your Mic", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE: {

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("pttt", "REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE");
                    //   refresh();
                    Toast.makeText(MainActivity.this, "storage ok", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void findViews() {
        passWord = findViewById(R.id.passWord);
        info = findViewById(R.id.info);
        refresh = findViewById(R.id.Enter);
        request = findViewById(R.id.request);
        stop = findViewById(R.id.stop);
    }

    private void initViews() {
        refresh.setOnClickListener(v -> refresh());
        stop.setOnClickListener(v -> StopListen());
    }




    @SuppressLint("Range")
    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();
        String contactId = null;
        String displayName = null;
        contactsInfoList = new ArrayList<ContactsInfo>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (displayName.equals("אבא עבודה"))
                    {
                        info.setText("ok");
                        boolContact = true;
                        return;
                    }

                    contactsInfo.setContactId(contactId);
                    contactsInfo.setDisplayName(displayName);
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        contactsInfo.setPhoneNumber(phoneNumber);
                    }
                    phoneCursor.close();

                    contactsInfoList.add(contactsInfo);
                }
            }
            info.setText("no ok");
            boolContact = false;
        }
        cursor.close();
    }
}
























