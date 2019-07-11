package com.ibkc.ods.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.ibkc.CLog;
import com.ibkc.common.util.CommonUtils;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.databinding.ActivityPhotoGridBinding;
import com.ibkc.ods.databinding.DialogImagePreviewBinding;
import com.ibkc.ods.databinding.LayoutReportGridItemBinding;
import com.ibkc.ods.network.IB20Connector;
import com.ibkc.ods.util.COnClickListener;
import com.ibkc.ods.util.CProgressDialog;
import com.ibkc.ods.util.ui.PopupUtil;
import com.ibkc.ods.vo.AdditionReport;
import com.ibkc.product.ocr.ResultDocImageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by macpro on 2018. 7. 3..
 */

/**
 * 추가서류촬영 Native 화면.
 * 이곳에서 할부, 메디칼, 리스 프로세스에 있는 사진 촬영을 수행한다.
 * 사진은 3열 Grid 로 표시한다.
 */
public class AddReportEquipGridActivity extends AppCompatActivity {
    private ActivityPhotoGridBinding mBinding = null;
    private ArrayList<AdditionReport> mReportPhotoList = new ArrayList<>();
    private PhotoGridAdapter mAdapter = null;
    private String mMenuType = null;
    private JSONObject mParam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_grid);

        try {
            mParam = new JSONObject(getIntent().getStringExtra("param"));
            mMenuType = mParam.getString("type");
        } catch (JSONException e) {
            CLog.printException(e);
            Toast.makeText(getApplicationContext(), "화면으로부터 인자를 제대로 전달받지 못하여 화면 실행 불가.", Toast.LENGTH_LONG).show();
            finish();
        }

        if (mMenuType.equals(Const.TYPE_INST)) { // 할부에서 추가서류 촬영 진입.
            mBinding.txtGuide.setText(getResources().getString(R.string.take_report_addition));
            mBinding.txtBtnCamera.setText(getResources().getString(R.string.pic_report));
            mBinding.title.setText(getResources().getString(R.string.report_title));
            mBinding.btnAlbum.setVisibility(View.GONE);
        } else if (mMenuType.equals(Const.TYPE_LEAS)) { // 리스에서 장비목록 촬영 진입.
            mBinding.title.setText(getString(R.string.leas));
            mBinding.navi.setVisibility(View.GONE);
            mBinding.txtGuide.setText(getResources().getString(R.string.take_picture_addition));
            mBinding.txtBtnCamera.setText(getResources().getString(R.string.pic_picture));
            mBinding.btnAlbum.setVisibility(View.VISIBLE);
        } else if (mMenuType.equals(Const.TYPE_MEDI)) { // 메디컬에서 추가서류 촬영 진입.
            mBinding.txtGuide.setText(getResources().getString(R.string.take_report_addition));
            mBinding.txtBtnCamera.setText(getResources().getString(R.string.pic_report));
            mBinding.title.setText(getResources().getString(R.string.medi_title));
            mBinding.btnAlbum.setVisibility(View.GONE);
        }

        mBinding.btnAction.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                Intent i = new Intent(AddReportEquipGridActivity.this, ReportListPopupActivity.class);
                i.putExtra("type", mMenuType);
                i.putExtra("callType", "camera");
                startActivityForResult(i, Const.REQ_REPORT_EQUIP_LIST);
            }
        });

        mBinding.btnAlbum.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                Intent i = new Intent(AddReportEquipGridActivity.this, ReportListPopupActivity.class);
                i.putExtra("type", mMenuType);
                i.putExtra("callType", "album");
                startActivityForResult(i, Const.REQ_REPORT_EQUIP_LIST);
            }
        });

        mBinding.btnBack.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                PopupUtil.showDefaultPopup(AddReportEquipGridActivity.this,
                        mMenuType.equals(Const.TYPE_LEAS) ? getString(R.string.alert_leas_exit) : getString(R.string.alert_term_exit), new PopupUtil.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Intent i = new Intent();
                        i.putExtra(Const.RESULT, Const.PAGE);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                });
            }
        });

        mBinding.btnHome.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                Intent i = new Intent();
                i.putExtra(Const.RESULT, Const.HOME);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        mBinding.btnLogout.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                PopupUtil.showDefaultPopup(getApplicationContext(), getString(R.string.logout_message), new PopupUtil.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        Intent i = new Intent();
                        i.putExtra(Const.RESULT, Const.LOGOUT);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                });
            }
        });

        mBinding.btnComplete.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                // ReportListPopupActivity 에서 activityResult 로 받아온 사진 정보 (사진 file, code) 를 웹으로 전달하기 위함.
                if (mReportPhotoList.size() <= 0) {
                    if (mMenuType.equals(Const.TYPE_LEAS)) {
                        Toast.makeText(getApplicationContext(), getString(R.string.alert_leas_no_photo), Toast.LENGTH_LONG).show();
                    } else {
                        PopupUtil.showDefaultPopup(AddReportEquipGridActivity.this, getString(R.string.alert_photo_send), new PopupUtil.OnPositiveButtonClickListener() {
                            @Override
                            public void onPositiveClick() {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("resultCode", "200");
                                    obj.put("resultMsg", "SUCCESS");
                                } catch (JSONException e) {
                                    CLog.printException(e);
                                    obj = null;
                                }
                                Intent i = new Intent();
                                i.putExtra(Const.RESULT, obj.toString());
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        });
                    }
                } else {
                    // 사진 촬영 목록과 사진 시트코드를 jsonObject 로 묶어서 jsp page 에 전송.
                    JSONObject obj = new JSONObject();
                    try {
                        JSONArray array = new JSONArray();
                        for (AdditionReport report : mReportPhotoList) {
                            JSONObject object = new JSONObject();
                            object.put("contentBase64", CommonUtils.convertByteArrayToBase64(report.getImageByteArray()));
                            object.put("sheetCode", report.getTypeCode());
                            array.put(object);
                        }

                        obj.put("data", array);
                        obj.put("imgMngNo", mParam.getString("imgMngNo"));
                        obj.put("demdDstc", "upload");
                        obj.put("custMngNo", mParam.getString("custMngNo"));
                    } catch (JSONException e) {
                        CLog.printException(e);
                        obj = null;
                    }

                    if (obj != null) {
                        final android.app.AlertDialog alert = CProgressDialog.getInstance().showProgress(AddReportEquipGridActivity.this);
                        IB20Connector.getInstance().conn(getApplicationContext(), IB20Connector.IMAGE_UPLOAD, obj, new IB20Connector.IB20ConnectorCallbackListener() {
                            @Override
                            public void onSuccess(JSONObject body) throws JSONException {
                                alert.dismiss();
                                Intent i = new Intent();
                                i.putExtra(Const.RESULT, body.toString());
                                setResult(RESULT_OK, i);
                                finish();
                            }

                            @Override
                            public void onErrorResponse(String errorMsg) {
                                alert.dismiss();
                                Toast.makeText(AddReportEquipGridActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "이미지 생성 실패", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mAdapter = new PhotoGridAdapter(this);

        mBinding.gridView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_REPORT_EQUIP_LIST:
                if (resultCode == RESULT_OK && data != null) {
                    // result data 로 받아온 사진 관련 객체를 grid 로 관리하기 위해 ArrayList 에 add.
                    mReportPhotoList.add((AdditionReport) data.getSerializableExtra(Const.REPORT_OBJ));
                    mAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        PopupUtil.showDefaultPopup(AddReportEquipGridActivity.this, getString(R.string.alert_term_exit), new PopupUtil.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveClick() {
                Intent i = new Intent();
                i.putExtra(Const.RESULT, Const.PAGE);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    /**
     * 사진 촬영 결과를 viewing 해주는 adapter class
     * 사진 보기, 삭제 기능을 수행한다.
     */
    private class PhotoGridAdapter extends BaseAdapter {
        private Context mContext = null;


        public PhotoGridAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mReportPhotoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mReportPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutReportGridItemBinding adapterBinding;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_report_grid_item, null);
                adapterBinding = DataBindingUtil.bind(convertView);
                convertView.setTag(adapterBinding);
            } else {
                adapterBinding = (LayoutReportGridItemBinding) convertView.getTag();
            }


            adapterBinding.reportName.setText(mReportPhotoList.get(position).getName());

            adapterBinding.btnShowing.setOnClickListener(new COnClickListener() {
                @Override
                public void COnClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                    final DialogImagePreviewBinding binding = DataBindingUtil.bind(LayoutInflater.from(mContext).inflate(R.layout.dialog_image_preview, null));

                    // 이미지 복호화해서 보여주는 쓰레드
                    final ResultDocImageTask task = new ResultDocImageTask(binding.ivPreview);
                    if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mReportPhotoList.get(position).getImageByteArray());
                    } else {
                        task.execute(mReportPhotoList.get(position).getImageByteArray());
                    }
                    final AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.setView(binding.getRoot());
                    dialog.show();

                    binding.btnClose.setOnClickListener(new COnClickListener() {
                        @Override
                        public void COnClick(View v) {
                            dialog.dismiss();
                            task.cancel(true);
                        }
                    });
                }
            });

            adapterBinding.btnDelete.setOnClickListener(new COnClickListener() {
                @Override
                public void COnClick(View v) {
                    PopupUtil.showDefaultPopup(mContext, getString(R.string.delete_confirm), new PopupUtil.OnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveClick() {
                            mReportPhotoList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
            return adapterBinding.getRoot();
        }
    }
}
