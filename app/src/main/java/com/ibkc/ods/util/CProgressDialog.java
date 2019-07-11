package com.ibkc.ods.util;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.ibkc.ods.R;
import com.ibkc.ods.databinding.DialogProgressBinding;

/**
 * 통신 중에 발생하는 delay 를 표시하기 위한 progress circle dialog class.
 */
public class CProgressDialog {
    private AnimationDrawable mAnimationDrawable = null;
    private static class LazyHolder {
        private static final CProgressDialog instance = new CProgressDialog();
    }

    public static CProgressDialog getInstance() {
        return LazyHolder.instance;
    }

    /**
     * progress circle show
     * @param context context
     * @return AlertDialog
     */
    public AlertDialog showProgress(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ProgressDialogTheme);
        DialogProgressBinding binding = DataBindingUtil.bind(LayoutInflater.from(context).inflate(R.layout.dialog_progress, null));
        mAnimationDrawable = (AnimationDrawable) binding.progressDialog.getBackground();

        mAnimationDrawable.start();
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(binding.getRoot());
        dialog.show();
        return dialog;
    }

    /**
     * progress circle hide
     * @param dialog AlertDialog
     */
    public void hideProgress(AlertDialog dialog) {
        mAnimationDrawable.stop();
        dialog.dismiss();
    }

}
