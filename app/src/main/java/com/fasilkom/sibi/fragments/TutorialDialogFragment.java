package com.fasilkom.sibi.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.activities.CameraActivity;
import com.fasilkom.sibi.adapter.CustomAdapter;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class TutorialDialogFragment extends DialogFragment {

    ViewPager vpTutorial;
    private ImageView imgClose;
    private ImageButton btnNext;
    private ImageButton btnBack;
    private TextView tvPage;
    private Button btnStartCamera;
    final FragmentPopupTutorial firstTutorial = FragmentPopupTutorial.newInstance("first");
    final FragmentPopupTutorial secondTutorial = FragmentPopupTutorial.newInstance("second");
    final FragmentPopupTutorial thirdTutorial = FragmentPopupTutorial.newInstance("third");
    private Integer currentItem = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sibi_popup_alert,container,false);
        vpTutorial = view.findViewById(R.id.viewpager);
        imgClose = view.findViewById(R.id.closePopupButton);
        btnBack = view.findViewById(R.id.buttonBack);
        btnNext = view.findViewById(R.id.buttonNext);
        tvPage = view.findViewById(R.id.tvPage);
        btnStartCamera = view.findViewById(R.id.startCamera);
        CustomAdapter adapter = new CustomAdapter(getChildFragmentManager());


        adapter.addFragment("tutorial1", firstTutorial);
        adapter.addFragment("tutorial2", secondTutorial);
        adapter.addFragment("tutorial3", thirdTutorial);

        vpTutorial.setAdapter(adapter);
        btnBack.setVisibility(View.GONE);

        btnStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem++;
                checkVisibility();
                vpTutorial.setCurrentItem(currentItem);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem--;
                checkVisibility();
                vpTutorial.setCurrentItem(currentItem);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentItem = 0;
                vpTutorial.setCurrentItem(currentItem);
                getDialog().dismiss();
            }
        });
        return view;
    }

    private void checkVisibility(){
        if (currentItem == 0 ){
            btnBack.setVisibility(View.INVISIBLE);
            btnNext.setVisibility(View.VISIBLE);
            tvPage.setVisibility(View.VISIBLE);
            tvPage.setText(String.valueOf(currentItem+1)+"/3");
            btnStartCamera.setVisibility(View.GONE);

        } else if (currentItem == 2){
            btnNext.setVisibility(View.INVISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            tvPage.setVisibility(View.GONE);
            btnStartCamera.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
            tvPage.setVisibility(View.VISIBLE);
            tvPage.setText(String.valueOf(currentItem+1)+"/3");
            btnStartCamera.setVisibility(View.GONE);

        }

    }
}
