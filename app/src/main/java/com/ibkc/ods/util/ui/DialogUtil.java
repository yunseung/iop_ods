package com.ibkc.ods.util.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibkc.ods.R;

/**
 * Created by macpro on 2018. 8. 10..
 */

public class DialogUtil extends Dialog implements View.OnClickListener {

    public static final String Default_Positive_Button_Text = "확인";
    public static final String Default_Negative_Button_Text = "취소";

    private Context mContext = null;

    private DialogInterface.OnClickListener mPositiveButtonListener = null;
    private DialogInterface.OnClickListener mNegativeButtonListener = null;

    private TextView txtMessage;
    private RelativeLayout btnOk;
    private TextView txtOk;
    private RelativeLayout btnCancel;
    private TextView txtCancel;

    public void setMessage(int messageId) {
        setMessage(mContext.getText(messageId));
    }

    public void setMessage(CharSequence message) {
        txtMessage.setText(message);
    }

    public void setPositiveButtonText(int textId) {
        setPositiveButtonText(mContext.getText(textId));
    }

    public void setPositiveButtonText(CharSequence text) {
        txtOk.setText(text);
    }

    public void setNegativeButtonText(int textId) {
        setNegativeButtonText(mContext.getText(textId));
    }

    public void setNegativeButtonText(CharSequence text) {
        txtCancel.setText(text);
    }

    public void setPositiveButton(final DialogInterface.OnClickListener listener) {
        setPositiveButton(Default_Positive_Button_Text, listener);
    }

    public void setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
        setPositiveButton(mContext.getText(textId), listener);
    }

    public void setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        setPositiveButtonText(text);
        mPositiveButtonListener = listener;
    }

    public void setNegativeButton(final DialogInterface.OnClickListener listener) {
        setNegativeButton(Default_Negative_Button_Text, listener);
    }

    public void setVisibleNegative(boolean isShow) {
        btnCancel.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public void setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
        setNegativeButton(mContext.getText(textId), listener);
    }

    public void setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        setNegativeButtonText(text);
        mNegativeButtonListener = listener;
    }

    public DialogUtil(Context context) {
        super(context);

        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_alert2);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txtMessage = (TextView) findViewById(R.id.txt_message);
        btnOk = (RelativeLayout) findViewById(R.id.btn_ok);
        txtOk = (TextView) findViewById(R.id.txt_ok);
        btnCancel = (RelativeLayout) findViewById(R.id.btn_cancel);
        txtCancel = (TextView) findViewById(R.id.txt_cancel);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        setPositiveButton(null);
        setNegativeButton(null);

        setMessage("");
    }

    @Override
    public void onClick(View v) {
        if (v == btnOk) {
            if (mPositiveButtonListener != null) {
                mPositiveButtonListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
            }

            dismiss();
        } else if (v == btnCancel) {
            if (mNegativeButtonListener != null) {
                mNegativeButtonListener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
            }

            dismiss();
        }
    }
}