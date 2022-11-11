package com.example.mymfa;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;

public class AudioChecker {
    private boolean boolRecord;
    private String path;
    private MediaRecorder recorder;
    private MediaPlayer mediaPlayer;
    private MaterialButton stop;
    public AudioChecker(MaterialButton stop) {
        boolRecord = false;
        this.stop = stop;
    }
   public void setPath(String path){
        this.path = path;
   }
    public void getVoice() {
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

    }

    public void playVoice(){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            int timer =  mediaPlayer.getDuration();
            if(timer<5000)
                boolRecord = true;
            else
                boolRecord = false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean getBoolRecord(){
        return boolRecord;
    }

    public MediaRecorder getMediaRecorder()
    {
        return recorder;
    }
}
