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
import com.ibkc.ods.databinding.LayoutVisitGridItemBinding;
import com.ibkc.ods.network.IB20Connector;
import com.ibkc.ods.util.COnClickListener;
import com.ibkc.ods.util.CProgressDialog;
import com.ibkc.ods.util.ui.PopupUtil;
import com.ibkc.ods.vo.Report;
import com.ibkc.product.ocr.OCRManager;
import com.ibkc.product.ocr.ResultDocImageTask;
import com.rosisit.idcardcapture.CameraActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by macpro on 2018. 7. 3..
 */

/**
 * 방문사진 촬영 native 화면.
 * AddReportEquipGridActivity 참조.
 */
public class VisitPhotoGridActivity extends AppCompatActivity {
    private ActivityPhotoGridBinding mBinding = null;
    private ArrayList<byte[]> mVisitPhotoList = new ArrayList<>();
    private PhotoGridAdapter mAdapter = null;
    private JSONObject mParam = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CLog.d(">> onCreate");
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_grid);
        mBinding.txtGuide.setText(getResources().getString(R.string.take_picture_addition));
        mBinding.txtBtnCamera.setText(getResources().getString(R.string.pic_picture));
        mBinding.title.setText(getResources().getString(R.string.visit_title));
        mBinding.navi.setVisibility(View.GONE);
        mBinding.btnAlbum.setVisibility(View.VISIBLE);

        try {
            mParam = new JSONObject(getIntent().getStringExtra("param"));
        } catch (JSONException e) {
            CLog.printException(e);
            mParam = null;
        }

        mBinding.btnAction.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                OCRManager.getInstance().startNormalCamera(VisitPhotoGridActivity.this);
            }
        });

        mBinding.btnAlbum.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, Const.REQ_SELECT_CAPTURE);
            }
        });

        mBinding.btnBack.setOnClickListener(new COnClickListener() {
            @Override
            public void COnClick(View v) {
                PopupUtil.showDefaultPopup(VisitPhotoGridActivity.this, getString(R.string.alert_visit_photo_exit), new PopupUtil.OnPositiveButtonClickListener() {
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
                PopupUtil.showDefaultPopup(VisitPhotoGridActivity.this, getString(R.string.logout_message), new PopupUtil.OnPositiveButtonClickListener() {
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
                if (mVisitPhotoList.size() <= 0) {
                    Toast.makeText(VisitPhotoGridActivity.this, getString(R.string.alert_leas_no_photo), Toast.LENGTH_LONG).show();
                } else {
                    if (mParam != null) {
                        // 사진 촬영 목록과 사진 시트코드를 jsonObject 로 묶어서 jsp page 에 전송.
                        JSONObject obj = new JSONObject();
                        try {
                            JSONArray array = new JSONArray();
                            for (byte[] contentBase64 : mVisitPhotoList) {
                                JSONObject object = new JSONObject();
                                object.put("contentBase64", CommonUtils.convertByteArrayToBase64(contentBase64));
                                object.put("sheetCode", Report.REPORT.리스기타.code);
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
                            final android.app.AlertDialog alert = CProgressDialog.getInstance().showProgress(VisitPhotoGridActivity.this);
                            IB20Connector.getInstance().conn(VisitPhotoGridActivity.this, IB20Connector.IMAGE_UPLOAD, obj, new IB20Connector.IB20ConnectorCallbackListener() {
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
                                }
                            });
                        } else {
                            Toast.makeText(VisitPhotoGridActivity.this, "사진 정보 생성에 문제가 발생했습니다.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        mAdapter = new PhotoGridAdapter(this, mVisitPhotoList);

        mBinding.gridView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        PopupUtil.showDefaultPopup(VisitPhotoGridActivity.this, getString(R.string.alert_visit_photo_exit), new PopupUtil.OnPositiveButtonClickListener() {
            @Override
            public void onPositiveClick() {
                Intent i = new Intent();
                i.putExtra(Const.RESULT, Const.PAGE);
                setResult(RESULT_OK, i);
                finish();
            }
        });
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Const.REQ_DOCUMENT_ETC:
                if (resultCode == CameraActivity.RETURN_OK && data != null) {
                    mVisitPhotoList.add(data.getByteArrayExtra(CameraActivity.DATA_ENCRYT_IMAGE_BYTE_ARRAY));
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case Const.REQ_SELECT_CAPTURE: // 갤러리에서 사진 가지고 오기.
                if (resultCode == RESULT_OK && data != null) {
                    byte[] image = CommonUtils.uriImageResize(VisitPhotoGridActivity.this, data.getData());
                    mVisitPhotoList.add(image);
                    mAdapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class PhotoGridAdapter extends BaseAdapter {
        private Context mContext = null;

        public PhotoGridAdapter(Context context, ArrayList<byte[]> data) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mVisitPhotoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mVisitPhotoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final LayoutVisitGridItemBinding adapterBinding;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_visit_grid_item, null);
                adapterBinding = DataBindingUtil.bind(convertView);
                convertView.setTag(adapterBinding);
            } else {
                adapterBinding = (LayoutVisitGridItemBinding) convertView.getTag();
            }

            // 사진 grid 목록에 사진 이름 붙여줌. (사진 01, 사진 02 .... 사진 10, 사진 25..)
            String thumbName = Integer.toString(position + 1);
            if (position < 9) {
                thumbName = "사진 0" + thumbName;
            } else {
                thumbName = "사진 " + thumbName;
            }


            adapterBinding.reportName.setText(thumbName);

            adapterBinding.btnShowing.setOnClickListener(new COnClickListener() {
                @Override
                public void COnClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogTheme);
                    final DialogImagePreviewBinding binding = DataBindingUtil.bind(LayoutInflater.from(mContext).inflate(R.layout.dialog_image_preview, null));

                    // 이미지 복호화해서 보여주는 쓰레드
                    final ResultDocImageTask task = new ResultDocImageTask(binding.ivPreview);
                    if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mVisitPhotoList.get(position));
                    } else {
                        task.execute(mVisitPhotoList.get(position));
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
                            mVisitPhotoList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
            return adapterBinding.getRoot();
        }
    }
}
