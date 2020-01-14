package com.fasilkom.sibi.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasilkom.sibi.R;
import com.fasilkom.sibi.SimpleRecyclerAdapter;
import com.fasilkom.sibi.activities.CameraActivity;
import com.fasilkom.sibi.activities.DetailActivity;
import com.fasilkom.sibi.databinding.CardItemBinding;
import com.fasilkom.sibi.databinding.FragmentSibiBinding;
import com.fasilkom.sibi.models.CardModel;
import com.fasilkom.sibi.models.PagerTutorialModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class SibiFragment extends Fragment {
    private String fragType;
    private FragmentSibiBinding sibiBinding;
    private ArrayList<CardModel> cardList;
    private SimpleRecyclerAdapter cardAdapter;

    private TutorialDialogFragment dialogTutorial = new TutorialDialogFragment();


    public SibiFragment() {
        // Required empty public constructor
    }

    public static SibiFragment newInstance(String typeFrag) {
        SibiFragment fragment = new SibiFragment();
        fragment.setFragType(typeFrag);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sibiBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sibi, container, false);
        initLayout();


        return sibiBinding.getRoot();
    }

    private void initLayout() {
        if (fragType.equalsIgnoreCase("SIBI")){
            initSibiLayout();
        } else {
            initBisindoLayout();
        }

        initData();
    }

    private void initSibiLayout(){
        //setting sibi title
        sibiBinding.fragImgBg.setImageResource(R.drawable.ic_background_title_sibi);
        sibiBinding.TitleFrag.setText(R.string.sibiText);
        sibiBinding.subtitleFrag.setText(R.string.sibiSubText);
        sibiBinding.dictButton.setTextColor(getResources().getColor(R.color.colorOren));
    }

    private void initBisindoLayout(){
        //setting bisindo title
        sibiBinding.fragImgBg.setImageResource(R.drawable.ic_background_bisindo_title);
        sibiBinding.TitleFrag.setText(R.string.bisindoText);
        sibiBinding.subtitleFrag.setText(R.string.bisindoSubText);
        sibiBinding.dictButton.setTextColor(getResources().getColor(R.color.colorBlue));
    }

    private void initData() {
        String[] title = getResources().getStringArray(R.array.cardTitleName);
        String[] desc = getResources().getStringArray(R.array.cardSubtitleText);
        TypedArray img;
        if (fragType.equalsIgnoreCase("SIBI")){
            img = getResources().obtainTypedArray(R.array.cardImgSibi);
        } else {
            img = getResources().obtainTypedArray(R.array.cardImgBisindo);
        }
        cardList = new ArrayList<>();
        for (int i = 0; i < title.length; i++) {
            CardModel card = new CardModel(title[i], desc[i], img.getResourceId(i,0));
            cardList.add(card);
        }
        if (cardAdapter == null) {
            cardAdapter = new SimpleRecyclerAdapter<CardModel>(cardList, R.layout.card_item, (holder, item) -> {
                CardItemBinding cardBinding = (CardItemBinding) holder.getLayoutBinding();

                cardBinding.TitleFrag.setText(item.getCardName());
                cardBinding.cardImg.setImageResource(item.getCardImg());

                setupButtonStartCamera(cardBinding, item);
            });
        }
        sibiBinding.rcCard.setLayoutManager(new LinearLayoutManager(getContext()));
        sibiBinding.rcCard.setAdapter(cardAdapter);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Snackbar snackbar = mCreateSnackbar(view);
        snackbar.setAction(R.string.ok, (View v) -> snackbar.dismiss());
        super.onViewCreated(view, savedInstanceState);
    }

    private Snackbar mCreateSnackbar(@NonNull View view) {
        return Snackbar.make(view, R.string.unavailable_feature, Snackbar.LENGTH_LONG);
    }

    private void showDialogStartRecording(String typeDialog){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sibi_dialog_start_recording);

        TextView tvHeader = dialog.findViewById(R.id.headerPopup);
        TextView tvSubHeader = dialog.findViewById(R.id.subPopup);

        Button startNoTutorial = dialog.findViewById(R.id.startNoTutorial);
        Button startWithTutorial = dialog.findViewById(R.id.startWithTutorial);

        ImageView imgClose = dialog.findViewById(R.id.closePopupButton);

        if (typeDialog.equalsIgnoreCase("Isyarat Ke Dalam Teks")){
            tvHeader.setText(R.string.IsyaratToTextHeader);
            tvSubHeader.setText(R.string.IsyaratToTextSub);
        } else {
            tvHeader.setText(R.string.TextToIsyaratHeader);
            tvSubHeader.setText(R.string.TextToIsyaratSub);
        }

        startWithTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTutorial.show(getChildFragmentManager(),"test1");
            }
        });

        startNoTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void setupButtonStartCamera(CardItemBinding cardBinding, CardModel item){
        if (fragType.equalsIgnoreCase("SIBI")){
            if (item.getCardName().equalsIgnoreCase("Isyarat ke Dalam Teks")){
                cardBinding.btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogStartRecording(item.getCardName());
                    }
                });
            } else {
                cardBinding.btnStart.setEnabled(false);
                cardBinding.btnStart.setBackground(getResources().getDrawable(R.drawable.bg_button_not_active));
            }
        } else {
            cardBinding.btnStart.setEnabled(false);
            cardBinding.btnStart.setBackground(getResources().getDrawable(R.drawable.bg_button_not_active));
        }
    }

    public String getFragType() {
        return fragType;
    }

    public void setFragType(String fragType) {
        this.fragType = fragType;
    }
}
