package com.fasilkom.sibi.ui.signtotext;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fasilkom.sibi.BaseApplication;
import com.fasilkom.sibi.ModelProvider;
import com.fasilkom.sibi.R;
import com.fasilkom.sibi.di.component.ActivityComponent;
import com.fasilkom.sibi.di.component.DaggerActivityComponent;
import com.fasilkom.sibi.di.module.ActivityModule;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

public class PickerActivity extends AppCompatActivity implements PickerContract.View {

    @Inject
    PickerContract.Presenter presenter;

    HashMap<Integer, String> videoFileNames = new HashMap() {{
        put(0, "Siapa_Nama_Mu.mp4");
        put(1, "Saya_Sering_Sakit_Kepala_Saya_Harus_Periksa_Ke_Bagi_an_Mana.mp4");
    }};

    File filePath = Environment.getExternalStorageDirectory();
    File dir = new File(filePath.getAbsolutePath() + "/SIBI");
    String videoUri = "";


    private int REQUEST_CODE_PERMISSIONS = 101;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
        "android.permission.CAMERA",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.RECORD_AUDIO",
        "android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        if (allPermissionsGranted()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            initSpinner();
            initClickListener();
            injectDependency();
            presenter.attach(this);
            presenter.printString();

        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    public void initSpinner() {
        Spinner spinner = findViewById(R.id.spinner_video_selection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.sibiVideoSelectionAvailable, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                Log.d("FilePath", filePath.getAbsolutePath());
                Toast.makeText(getApplicationContext(), "" + position + " : " + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();

                videoUri = dir + "/" + videoFileNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initClickListener() {
        Button executeButton = findViewById(R.id.buttonExecute);
        executeButton.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                presenter.executeClassifier(videoUri);
            }
        });
    }

    private void injectDependency() {
        ActivityComponent activityComponent = ((BaseApplication) getApplication()).getActivityComponent();
        activityComponent.inject(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

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
