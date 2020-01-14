package com.fasilkom.sibi.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.databinding.ActivityCameraResultBinding;

import java.io.File;

public class CameraResultActivity extends AppCompatActivity {
    public static final String argPathCamera= "ARG_GET_VIDEO_PATH";
    private ActivityCameraResultBinding cameraResultBinding;
    private Uri videoUri;
    private File videoFile;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraResultBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_result);

        Intent intent = getIntent();
        videoFile = (File) intent.getExtras().get(argPathCamera);
        initLayout();
    }

    private void initLayout(){
        videoUri = Uri.fromFile(videoFile);
        MediaController mc = new MediaController(CameraResultActivity.this);
        mc.setAnchorView(cameraResultBinding.videoRecorded);
        mc.setMediaPlayer(cameraResultBinding.videoRecorded);
        cameraResultBinding.videoRecorded.setVideoURI(videoUri);
        cameraResultBinding.videoRecorded.setMediaController(mc);
        cameraResultBinding.videoRecorded.requestFocus();
        cameraResultBinding.videoRecorded.start();
    }
}
