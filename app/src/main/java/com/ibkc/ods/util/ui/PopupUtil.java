package com.ibkc.ods.util.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.ibkc.ods.R;
import com.ibkc.ods.databinding.DialogDeleteConfirmBinding;
import com.ibkc.ods.util.COnClickListener;

/**
 * Created by macpro on 2018. 8. 1..
 */

public class PopupUtil {

    public interface OnPositiveButtonClickListener {
        void onPositiveClick();
    }

    private static OnPositiveButtonClickListener mPositiveButtonClickListener = null;

    public static void showDefaultPopup(Context context, String title, final OnPositiveButtonClickListener positiveListener) {

        mPositiveButtonClickListener = positiveListener;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        final DialogDeleteConfirmBinding binding = DataBindingUtil.bind(LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirm, null));
        final AlertDialog dialog = builder.create();
        binding.message.setText(title);

        dialog.setCancelable(false);
        dialog.setView(binding.getRoot());
        dialog.show();

        binding.btnOk.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                positiveListener.onPositiveClick();
                dialog.dismiss();
            }
        });

        binding.btnCancel.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public static void showOneButtonPopup(Context context, String title, final OnPositiveButtonClickListener positiveListener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTheme);
        final DialogDeleteConfirmBinding binding = DataBindingUtil.bind(LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirm, null));
        final AlertDialog dialog = builder.create();
        binding.btnCancel.setVisibility(View.GONE);
        binding.message.setText(title);

        dialog.setCancelable(false);
        dialog.setView(binding.getRoot());
        dialog.show();



        binding.btnOk.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                positiveListener.onPositiveClick();
                dialog.dismiss();
            }
        });
    }
}
