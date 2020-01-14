package com.fasilkom.sibi.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.databinding.FragmentTutorialBinding;

public class FragmentPopupTutorial extends Fragment {
    private FragmentTutorialBinding tutorialBinding;
    private String fragPos;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static FragmentPopupTutorial newInstance(String fragPosition) {
        FragmentPopupTutorial fragment = new FragmentPopupTutorial();

        fragment.setFragPos(fragPosition);
        return fragment;
    }

    public String getFragPos() {
        return fragPos;
    }

    public void setFragPos(String fragPos) {
        this.fragPos = fragPos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        tutorialBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_tutorial, container, false);
        initLayout();
        return tutorialBinding.getRoot();
    }

    private void initLayout(){
        if (fragPos.equalsIgnoreCase("first")){
            tutorialBinding.imageTutorial.setImageDrawable(getResources().getDrawable(R.drawable.ic_first_tutorial));
            tutorialBinding.textTutorial.setText(getResources().getString(R.string.firstTutorial));
        } else if (fragPos.equalsIgnoreCase("second")){
            tutorialBinding.imageTutorial.setImageDrawable(getResources().getDrawable(R.drawable.ic_rekam_dengan_jelas));
            tutorialBinding.textTutorial.setText(getResources().getString(R.string.secondTutorial));
        } else {
            tutorialBinding.imageTutorial.setImageDrawable(getResources().getDrawable(R.drawable.ic_menjadi_teks));
            tutorialBinding.textTutorial.setText(getResources().getString(R.string.thirdTutorial));
        }

    }



}
