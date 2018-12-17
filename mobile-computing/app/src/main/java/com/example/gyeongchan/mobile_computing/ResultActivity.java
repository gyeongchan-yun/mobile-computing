package com.example.gyeongchan.mobile_computing;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

import android.util.Log;


public class ResultActivity extends AppCompatActivity implements Handler.Callback {

    private TextView resultText;
    File file = Environment.getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultText = findViewById(R.id.textView);

        String path = file.getAbsolutePath() + "/current.m4a";  // Modify to cover all including previous files
        Log.e("Debug", path);
        resultText.setText(String.valueOf("Uploading " + path + " ... ")); // For testing
        FileUpload.doFileUpload(path, new Handler(this));
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.i("Debug", "Response :: " + msg.obj);
        String success, result;
        if (msg.arg1 == 1) {
            result = String.valueOf(msg.obj);
            if (result.split(",")[0].equals("0")) {
                success = result.split(",")[1];
            } else {
                success = "Different Rate: " + result.split(",")[1] + "%\n\n";
                if (result.split(",")[2].equals("1")) {
                    success += "Faster breath than usual with probability = " + result.split(",")[3] + "\n";
                } else {
                    success += "Slower breath than usual with probability = " + result.split(",")[3] + "\n";
                }
            }
        } else {
            success = "Error";
        }
        // String success = 1 == msg.arg1 ? String.valueOf(msg.obj) : "Error";

        resultText.setText(success);
        return false;
    }
}
