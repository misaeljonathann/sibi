package com.fasilkom.sibi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.databinding.ActivityDetailBinding;
import com.fasilkom.sibi.ui.camera.CameraActivity;


public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.backButtonDetailActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.buttonStartCameraActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }
}
