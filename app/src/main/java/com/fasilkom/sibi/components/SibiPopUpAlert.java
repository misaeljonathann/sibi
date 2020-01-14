//package com.fasilkom.sibi.components;
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//
//import androidx.annotation.NonNull;
//import androidx.databinding.BaseObservable;
//import androidx.databinding.Bindable;
//import androidx.databinding.DataBindingUtil;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentPagerAdapter;
//
//import com.fasilkom.sibi.R;
//import com.fasilkom.sibi.databinding.SibiPopupAlertBinding;
//import com.fasilkom.sibi.fragments.FragmentPopupTutorial;
//import com.fasilkom.sibi.fragments.SignToTextFragment;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//
//
//public class SibiPopUpAlert extends BaseObservable {
//
//    SibiPopupAlertBinding binding;
//
//    private Context context;
//
//    private BottomSheetDialog dialog;
//
//    private int imgInfo;
//
//    private String info;
//
//    private String subInfo;
//
//    private String negativeButtonText;
//
//    private String positiveButtonText;
//
//    private int negativeButtonVisibility;
//
//    private int positiveButtonVisibility;
//
//    private boolean isCancelable;
//
//    private int infoStyle;
//
//    private int subInfoStyle;
//
//    private boolean isFullButton;
//
//    private SibiPopUpAlert (Context context, Builder builder) {
//        this.context = context;
//
//        bind();
//    }
//

//
//
//
//    private void bind () {
//        binding = DataBindingUtil.inflate(
//                LayoutInflater.from(this.context),
//                R.layout.sibi_popup_alert,
//                null,
//                false
//        );
//
//
//        dialog = new BottomSheetDialog(context, R.style.AdvoticsBottomAlertTheme);
//        binding.viewpager.setAdapter(new MyPagerAdapter(context));
//        binding.dotsIndicator.setViewPager(binding.viewpager);
////        binding.txtInfo.setTypeface(null,infoStyle);
////        binding.imgInfo.setImageDrawable(ContextCompat.getDrawable(context, imgInfo));
////        binding.txtSubInfo.setTypeface(null,subInfoStyle);
////
////        dialog.setCanceledOnTouchOutside(isCancelable);
////        dialog.setContentView(binding.getRoot());
////        getButtonwWidth();
//    }
//
//    public void show () {
//        dialog.show();
//    }
//
//    public boolean isShowing () {
//        return dialog.isShowing();
//    }
//
//    public void dismiss () {
//        dialog.dismiss();
//    }
//
//    public BottomSheetDialog getDialog () {
//        return dialog;
//    }
//
//    public static class Builder {
//
//    }
//
//}
//
