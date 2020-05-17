package com.fasilkom.sibi.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import com.fasilkom.sibi.ModelProvider;
import com.fasilkom.sibi.R;
import com.fasilkom.sibi.fragments.SibiFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class HomePageActivity extends AppCompatActivity {

    String TAG = "HomePageActivity";
//    TODO: add disposable RxJava

    final SibiFragment sibiFragment = SibiFragment.newInstance("SIBI");
    final SibiFragment bisindoFragment = SibiFragment.newInstance("BISINDO");
    final FragmentManager fm = getSupportFragmentManager();
    SibiFragment activeFrag = sibiFragment;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(activeFrag).show(sibiFragment).commit();
//                    setNavMenuItemThemeColors(getResources().getColor(R.color.mColor));
                    activeFrag = sibiFragment;
                    break;
                case R.id.navigation_dashboard:
                    fm.beginTransaction().hide(activeFrag).show(bisindoFragment).commit();
//                    setNavMenuItemThemeColors(getResources().getColor(R.color.mColor));
                    activeFrag = bisindoFragment;
                    break;
            }
            return loadFragment(activeFrag);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        setNavMenuItemThemeColors(getResources().getColor(R.color.mColor));
        loadFragment(activeFrag);
        try {
            ModelProvider.getInstance().loadClassifier(getApplicationContext(), "all");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //javaCameraView.enableView();
                    //mOpenCvCameraView.enableView();
                    Log.i(TAG, "callback: OpenCV loaded successfully.");
                } break;
                default:
                {
                    Log.e(TAG, "callback: Could not load Open CV.");
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "On Resume was called");


        if (!OpenCVLoader.initDebug())
        {
            Log.i(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            boolean success = OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);

            if (!success)
                Log.i(TAG, "Asynchronous initialization failed!");
            else
                Log.i(TAG, "Asynchronous initialization succeeded!");
        }
        else {
            Log.i(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_Fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    public void setNavMenuItemThemeColors(int color){
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = getResources().getColor(R.color.defaultColor);
        int navDefaultIconColor = getResources().getColor(R.color.defaultColor);

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
        ColorStateList navMenuIconList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[] {
                        color,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor,
                        navDefaultIconColor
                }
        );

        navigation.setItemTextColor(navMenuTextList);
        navigation.setItemIconTintList(navMenuIconList);
    }

}
