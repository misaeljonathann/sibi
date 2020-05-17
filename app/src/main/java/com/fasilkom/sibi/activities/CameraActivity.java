package com.fasilkom.sibi.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.VideoCaptureConfig;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;

import com.fasilkom.sibi.AutoFitTextureView;
import com.fasilkom.sibi.ModelProvider;
import com.fasilkom.sibi.R;
import com.fasilkom.sibi.databinding.ActivityCameraBinding;
import com.fasilkom.sibi.tensorflow.ModelNullException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

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

    private AutoFitTextureView cameraPreview;
    private ConstraintLayout overlay;

    CompositeDisposable disposables = new CompositeDisposable();

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

        // Set the content view and get references to our views
        cameraPreview = findViewById(R.id.view_finder);
        overlay = findViewById(R.id.overlay);
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

                    String videoFileName = "SIBI_" + getCurrentDateNaming() + ".mp4";

                    newfile = new File(dir, videoFileName);

                    if (newfile.exists()) newfile.delete();
                    newfile.setReadable(true);

                    getResultFromClassifier(videoFileName, "camera");


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

    @Override
    protected void onPause() {
        super.onPause();

        disposables.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disposables.clear();
    }

    public void getResultFromClassifier(
            String videoFileName,
            String modelType) {
        videoCapture.startRecording(newfile, new VideoCapture.OnVideoSavedListener() {
            @Override
            public void onVideoSaved(File file) {
                Intent cameraResultIntent = new Intent(CameraActivity.this, CameraResultActivity.class);
                cameraResultIntent.putExtra(CameraResultActivity.argPathCamera, newfile);

                /* Model Call */

                Log.d("RxJava", "Preparation");

//                try {
//                    ModelProvider.getInstance().runClassifier(
//                            getApplicationContext(),
//                            modelType,
//                            videoFileName,
//                            cameraPreview.getMeasuredHeight(),
//                            cameraPreview.getMeasuredWidth());
//                } catch (ModelNullException e) {
//                    e.printStackTrace();
//                }

                Disposable disposable = Observable.create(emitter -> {
                    System.out.println("processing item on thread " + Thread.currentThread().getName());
                    emitter.onNext(ModelProvider.getInstance().runClassifier(
                            getApplicationContext(),
                            modelType,
                            videoFileName,
                            cameraPreview.getMeasuredHeight(),
                            cameraPreview.getMeasuredWidth()));
                    emitter.onComplete();
                })
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError(error -> Log.d("CameraActivity", "Rx Schedulers on error : " + error))
                        .subscribe(result -> System.out.println("consuming item on thread " + Thread.currentThread().getName()));

                startActivity(cameraResultIntent);

                disposables.add(disposable);
            }

            @Override
            public void onError(VideoCapture.UseCaseError useCaseError, String message, @Nullable Throwable cause) {

            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Get the preview size
        int previewWidth = cameraPreview.getMeasuredWidth(),
                previewHeight = cameraPreview.getMeasuredHeight();

        Log.d("Bitmap", "previewWidth : " + previewWidth);
        Log.d("Bitmap", "previewHeight : " + previewHeight);

        // Set the height of the overlay so that it makes the preview a square
        RelativeLayout.LayoutParams overlayParams = (RelativeLayout.LayoutParams) overlay.getLayoutParams();
        overlayParams.height = previewHeight - previewWidth;
        overlay.setLayoutParams(overlayParams);
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
