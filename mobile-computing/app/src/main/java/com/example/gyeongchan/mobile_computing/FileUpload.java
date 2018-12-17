package com.example.gyeongchan.mobile_computing;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FileUpload {

    static String SERVER_PATH = "http://10.20.19.197:5000/get_file";

    public static void doFileUpload(final String selectedPath, final Handler handler) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Debug", selectedPath);
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "------------------------afb19f4aeefb356c";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;

                String responseFromServer = "";
                int success = 0;

                try {
                //------------------ CLIENT REQUEST
                    File file = new File(selectedPath);
                    FileInputStream fileInputStream = new FileInputStream(file);
                    // open a URL connection to the Servlet
                    URL url = new URL(SERVER_PATH);
                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    // Allow Inputs
                    conn.setDoInput(true);
                    // Allow Outputs
                    conn.setDoOutput(true);
                    // Don't use a cached copy.
                    conn.setUseCaches(false);
                    // Use a post method.
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("file", file.getName());

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + file.getName() + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        Log.e("Debug", "Bytes write");
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    conn.connect();
                    String serverResponseMessage = conn.getResponseMessage();
                    Log.i("Debug", "HTTP Response is : "
                            + serverResponseMessage + ": " + conn.getResponseCode());

                    if(conn.getResponseCode() == 200){
                        success = 1;
                        serverResponseMessage = receiveJSON(conn);
                    }

                    conn.disconnect();
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                    sendMessageBack(serverResponseMessage, success, handler);

                } catch (MalformedURLException ex) {
                    Log.e("Debug", "error: " + ex.getMessage(), ex);
                    sendMessageBack(responseFromServer, 0, handler);
                    return;
                } catch (IOException ioe) {
                    Log.e("Debug", "error: " + ioe.getMessage(), ioe);
                    sendMessageBack(responseFromServer, 0, handler);
                    return;
                }
            }
        }).start();
    }

    private static String receiveJSON(HttpURLConnection conn) {
        String str, receiveMsg = "";
        try {
            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuffer buffer = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                buffer.append(str);
            }
            receiveMsg = buffer.toString();
            Log.i("receiveMsg : ", receiveMsg);
            reader.close();
        } catch (IOException e) {
            Log.e("Debug", "error: " + e.getMessage(), e);
        }

        return receiveMsg;
    }

    static void sendMessageBack(String responseFromServer, int success, Handler handler) {
        Message message = new Message();
        message.obj = responseFromServer;
        message.arg1 = success;
        handler.sendMessage(message);
    }
}
