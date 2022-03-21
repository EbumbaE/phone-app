package com.example.phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Picture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static java.lang.Thread.sleep;

public class MainActivity extends Activity {
    Socket socket = null;
    boolean f = false;
    Thread thread = null;
    Thread thread2 = null;
    Thread thread3 = null;
    EditText HOST;
    String Host;
    boolean finput = false;
    boolean fconnect = false;
    boolean fport = false;
    int port;
    public boolean callbackCame  = true;

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instanc
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private Camera mCamera;
    private CameraPreview mPriview;
    private int CAMERA_CAPTURE;
    File photoFile;
    Button btn;

    Camera.PictureCallback callback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            final byte[] d = data;
            try {
                Runnable runnn = new Runnable() {
                    public void run() {
                        try {
                            socket = new Socket(InetAddress.getByName(Host), port);
                            socket.getOutputStream().write(d);
                            socket.getOutputStream().flush();
                            socket.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread3 = new Thread(runnn);
                thread3.start();
            } catch (Exception e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Ошибка отправки 0_______0", Toast.LENGTH_SHORT);
                toast.show();
            }
            Log.d(TAG, "EBUMBA");
            callbackCame = true;
        }
    };

    private final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private final int MY_PERMISSIONS_REQUEST_INTERNET = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            // разрешение предоставлено
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                 ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else{
        }
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
        }
        else {
            // разрешение предоставлено
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
            }
        } else{
        }
//
        HOST = (EditText)findViewById(R.id.inputID);
        mCamera = getCameraInstance();

        mPriview = new CameraPreview(this, mCamera);
        FrameLayout priview = (FrameLayout) findViewById(R.id.camera_preview);
        priview.addView(mPriview);

        btn = findViewById(R.id.ChoseCam1);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fport = true;
                        port = 8080;
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Camera 1, Port: 8080", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );

        btn = findViewById(R.id.ChoseCam2);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fport = true;
                        port = 8081;
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Camera 2, Port: 8081", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );

        btn = findViewById(R.id.button_input);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = HOST.getText().toString();
                        Log.d(TAG, "MEMAS <<" + s + ">>");
                        if (s.length() == 0){
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Вы ничего не ввели :(", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            Integer kol = 0;
                            for (int i = 0; i < s.length(); i++)
                                if (s.charAt(i) == '.')
                                    kol++;
                            if (kol == 0 || kol != 0 && s.charAt(0) == '.') {
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Что-то не то ввели 0__0", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            else {
                                Host = s;
                                finput = true;
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Ip Введен...", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                }
        );

        btn = findViewById(R.id.button_connect);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (finput) {
                            if (fport) {
                                try {
                                    Runnable runn = new Runnable() {
                                        public void run() {
                                            try {
                                                socket = new Socket(InetAddress.getByName(Host), port);
                                                socket.getOutputStream().write(1);
                                                socket.getOutputStream().flush();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    thread2 = new Thread(runn);
                                    thread2.start();

                                    if (socket.isClosed()) {
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "Не подключено...", Toast.LENGTH_SHORT);
                                        toast.show();
                                        fconnect = false;
                                    } else{
                                        Toast toast = Toast.makeText(getApplicationContext(),
                                                "Подключено!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        fconnect = true;
                                        socket.close();
                                    }
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Ошибка подключения :(", Toast.LENGTH_SHORT);
                                    toast.show();
                                    e.printStackTrace();
                                }
                                Log.d(TAG, "EBUMBA");
                            } else{
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Вы не выбрали камеру :(", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Вы ничего не ввели :(", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
        );

        btn = findViewById(R.id.button_SS);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v){
                        if (fconnect) {
                            if (f) {
                                f = false;
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Stop upload", Toast.LENGTH_SHORT);
                                toast.show();
                            } else {
                                f = true;
                                Runnable runnable = new Runnable() {
                                    public void run() {
                                        while (f) {
                                            while(!callbackCame){
                                                try{
                                                    sleep(1);
                                                }catch(Exception e){
                                                }
                                            }
                                            callbackCame = false;
                                            mCamera.startPreview();
                                            mCamera.takePicture(null, null, callback);
                                            try {
                                                sleep(150);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                };
                                thread = new Thread(runnable);
                                thread.start();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Start upload", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        else{
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Вы не подключились :(", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
}