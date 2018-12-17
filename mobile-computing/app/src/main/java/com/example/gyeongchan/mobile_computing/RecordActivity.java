package com.example.gyeongchan.mobile_computing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import android.os.Environment;
import android.widget.Toast; // For test

public class RecordActivity extends AppCompatActivity {

    private int count = 3;
    private TextView countText;

    CountDownTimer recordTimer;
    MediaRecorder recorder = null;
    File file = Environment.getExternalStorageDirectory();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        countText = findViewById(R.id.textView);

        recordTimer =  new CountDownTimer(5000, 1000) { // TODO Change to 5000 later
            public void onTick(long millisUntilFinished) {
                if (recorder == null) {
                    String path = file.getAbsolutePath() + "/current.m4a";
                    Log.e("Debug", path);
                    recorder = new MediaRecorder();

                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

                    recorder.setOutputFile(path);

                    try {
                        recorder.prepare();
                        recorder.start();

                    } catch (Exception ex) {
                        Log.e("SampleAudioRecorder", "Exception : ", ex);
                    }
                }
            }

            public void onFinish() {
                if (recorder != null) {
                    recorder.stop();
                    recorder.release();
                }
                Intent intent1 = new Intent(RecordActivity.this, ResultActivity.class);
                startActivity(intent1);
            }
        };

        CountDownTimer timer = new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
                countText.setText(String.valueOf(count));
                count --;
            }

            public void onFinish() {
                countText.setText(String.valueOf("Recording"));
                recordTimer.start();
            }
        };

        timer.start();
    }

}
