package com.ibkc.ods.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.ibkc.CLog;
import com.ibkc.common.util.CommonUtils;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.databinding.ActivityPopupReportListBinding;
import com.ibkc.ods.databinding.LayoutPopupReportRowBinding;
import com.ibkc.ods.util.COnClickListener;
import com.ibkc.ods.util.ui.PopupUtil;
import com.ibkc.ods.vo.AdditionReport;
import com.ibkc.ods.vo.Report;
import com.ibkc.product.ocr.OCRManager;
import com.ibkc.product.ocr.ResultIdCardMaskingTask;
import com.rosisit.idcardcapture.CameraActivity;

import java.util.ArrayList;

/**
 * Created by macpro on 2018. 7. 5..
 */

/**
 * 추가서류 촬영 시 서류 리스트를 보여주고 선택할 수 있는 popup native 화면.
 * 할부, 리스, 메디컬에 따라서 각각 다른 리스트가 보여진다.
 */
public class ReportListPopupActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityPopupReportListBinding mBinding = null;
    private ArrayList<Report.REPORT> mReportList = new ArrayList<>();
    private String mSelectedCode = null;
    private String mSelectedName = null;
    private String mMenuType = null;
    private String mCallType = null;
    private boolean mIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_popup_report_list);

        mMenuType = getIntent().getStringExtra("type");
        mCallType = getIntent().getStringExtra("callType");

        // 할부 화면의 추가서류, 리스 화면의 장비 화면에 따라 사진 타입의 종류가 다르기 때문에 분기.
        if (mMenuType.equals(Const.TYPE_INST)) {
            mReportList = Report.getInstance().getInst();
        } else if (mMenuType.equals(Const.TYPE_LEAS)) {
            mReportList = Report.getInstance().getLeas();
        } else if (mMenuType.equals(Const.TYPE_MEDI)) {
            mReportList = Report.getInstance().getMedi();
        }

        mBinding.additionReportList.setAdapter(new ReportListAdapter(getApplicationContext(), mReportList));

        mBinding.btnSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mIsChecked) {
            if (mCallType.equals("camera")) {
                if (mMenuType.equals(Const.TYPE_LEAS) || mSelectedName.equals(Report.REPORT.할부메디칼기타.name)) {
                    OCRManager.getInstance().startNormalCamera(this);
                } else {
//                OCRManager.getInstance().startA4Doc(this);
                    // 신분증 사진만 따로 호출
                    if (mSelectedName.equals(Report.REPORT.신분증.name)) {
                        OCRManager.getInstance().startOCR(this);
                    } else {
                        OCRManager.getInstance().startA4Doc(this);
                    }
                }
            } else if (mCallType.equals("album")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Const.REQ_SELECT_CAPTURE);
            }
        } else {
            PopupUtil.showOneButtonPopup(ReportListPopupActivity.this, getString(R.string.alert_select_one), new PopupUtil.OnPositiveButtonClickListener() {
                @Override
                public void onPositiveClick() {}
            });
        }

        mIsChecked = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_DOCUMENT_A4:
                if (resultCode == CameraActivity.RETURN_OK && data != null) {
                    byte[] encryptImage_Document_A4 = data.getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY);
                    Intent i = new Intent();
                    i.putExtra(Const.REPORT_OBJ, new AdditionReport(encryptImage_Document_A4, mSelectedName, mSelectedCode));
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;
            case Const.REQ_DOCUMENT_ETC: // 일반 사진
                if (resultCode == CameraActivity.RETURN_OK && data != null) {
                    byte[] encryptImage_Document = data.getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY);
                    Intent i = new Intent();
                    i.putExtra(Const.REPORT_OBJ, new AdditionReport(encryptImage_Document, mSelectedName, mSelectedCode));
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;

            case Const.REQ_OCR_ID: // 신분증 사진
                if (resultCode == CameraActivity.RETURN_OK || resultCode == CameraActivity.RETURN_OVERTIME & data != null) { //인식 성공 및 3번 실패
                    final byte[] aa = data.getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY);
                    final ResultIdCardMaskingTask task = new ResultIdCardMaskingTask(null, null, new ResultIdCardMaskingTask.TaskCompleted() {
                        @Override
                        public void onImageMaskingCompleted(byte[] maskingImage) {
                            Intent i = new Intent();
                            i.putExtra(Const.REPORT_OBJ, new AdditionReport(maskingImage, mSelectedName, mSelectedCode));
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    });
                    if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
                    } else {
                        task.execute(data);
                    }
                }
                break;

            case Const.REQ_SELECT_CAPTURE: // 갤러리에서 사진 가지고 오기
                if (resultCode == RESULT_OK && data != null) {
                    byte[] image = CommonUtils.uriImageResize(ReportListPopupActivity.this, data.getData());
                    Intent i = new Intent();
                    i.putExtra(Const.REPORT_OBJ, new AdditionReport(image, mSelectedName, mSelectedCode));
                    setResult(RESULT_OK, i);
                    finish();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 서류 종류 리스트 만들어주는 adapter class.
     */
    private class ReportListAdapter extends BaseAdapter {

        private Context mContext = null;
        private ArrayList<Report.REPORT> reportList;
        private int mSelectedPosition = -1;
        private int mCheckCount = 0;

        public ReportListAdapter(Context context, ArrayList<Report.REPORT> data) {
            mContext = context;
            reportList = data;
        }

        @Override
        public int getCount() {
            return reportList.size();
        }

        @Override
        public Object getItem(int position) {
            return reportList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutPopupReportRowBinding adapterBinding;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_popup_report_row, null);
                adapterBinding = DataBindingUtil.bind(convertView);
                convertView.setTag(adapterBinding);
            } else {
                adapterBinding = (LayoutPopupReportRowBinding) convertView.getTag();
            }

            if (mSelectedPosition == position) {
                adapterBinding.checkbox.setChecked(true);
            } else {
                adapterBinding.checkbox.setChecked(false);
            }

            adapterBinding.reportName.setText(reportList.get(position).name);

            adapterBinding.checkbox.setOnClickListener(new COnClickListener() {
                @Override
                public void COnClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        mSelectedCode = reportList.get(position).code;
                        mSelectedName = reportList.get(position).name;
                        mSelectedPosition = position;
                        mIsChecked = true;
                    } else {
                        mSelectedPosition = -1;
                        mIsChecked = false;
                    }

                    notifyDataSetChanged();
                }
            });

            adapterBinding.setReport(reportList.get(position));
            return adapterBinding.getRoot();
        }
    }
}
