package com.fasilkom.sibi.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.icu.util.Output;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.databinding.ActivityCameraBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements LifecycleOwner {
    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.RECORD_AUDIO", "android.permission.READ_EXTERNAL_STORAGE"};
    private ActivityCameraBinding cameraBinding;
    private boolean isRecording = false;
    private VideoCapture videoCapture;
    private SimpleDateFormat fileNameDateFormat;
    File newfile;
    File filePath = Environment.getExternalStorageDirectory();
    File dir = new File(filePath.getAbsolutePath() + "/SIBI/");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        if (allPermissionsGranted()) {
            cameraBinding.viewFinder.post(new Runnable() {
                @Override
                public void run() {
                    startCamera();
                }
            }); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        View.OnTouchListener ot = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        };



        cameraBinding.imgCapture.setClickable(false);


        cameraBinding.imgCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    isRecording = true;
                    cameraBinding.imgCapture.setImageResource(R.drawable.ic_stop_recording);

                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    newfile = new File(dir, "SIBI_" + getCurrentDateNaming() + ".mp4");

                    if (newfile.exists()) newfile.delete();
                    newfile.setReadable(true);
                    videoCapture.startRecording(newfile, new VideoCapture.OnVideoSavedListener() {
                        @Override
                        public void onVideoSaved(File file) {
                            Intent cameraResultIntent = new Intent(CameraActivity.this, CameraResultActivity.class);
                            cameraResultIntent.putExtra(CameraResultActivity.argPathCamera, newfile);
                            startActivity(cameraResultIntent);
                        }

                        @Override
                        public void onError(VideoCapture.UseCaseError useCaseError, String message, @Nullable Throwable cause) {

                        }
                    });
                } else if (isRecording) {
                    isRecording = false;
                    videoCapture.stopRecording();
                    cameraBinding.imgCapture.setImageResource(R.drawable.ic_camera_not_recording);
//                    String uri = Android.Net.Uri.Parse

                }
            }
        });

        cameraBinding.imgCapture.setOnTouchListener(ot);

    }


    public String getCurrentDateNaming() {
        fileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return fileNameDateFormat.format(new Date());
    }

    private void startCamera() {
        PreviewConfig pConfig = new PreviewConfig.Builder().build();
        Preview prevw = new Preview(pConfig);


        VideoCaptureConfig videoCaptureConfig = new VideoCaptureConfig.Builder().setTargetRotation(cameraBinding.viewFinder.getDisplay().getRotation()).build();
        videoCapture = new VideoCapture(videoCaptureConfig);

        prevw.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
                cameraBinding.viewFinder.setSurfaceTexture(output.getSurfaceTexture());
            }
        });
        CameraX.bindToLifecycle(this, prevw, videoCapture);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraBinding.viewFinder.post(new Runnable() {
                    @Override
                    public void run() {
                        startCamera();
                    }
                });
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
