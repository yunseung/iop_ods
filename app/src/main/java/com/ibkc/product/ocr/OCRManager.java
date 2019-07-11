package com.ibkc.product.ocr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import com.ibkc.CLog;
import com.ibkc.common.util.CommonUtils;
import com.ibkc.ods.Const;
import com.ibkc.ods.util.CPermission;
import com.rosisit.idcardcapture.CameraActivity;

/**
 * Created by macpro on 2018. 7. 2..
 */

public class OCRManager {
    private static class LazyHolder {
        private static final OCRManager instance = new OCRManager();
    }

    public static OCRManager getInstance() {
        return OCRManager.LazyHolder.instance;
    }

    /**
     * 신분증 인식
     *
     * @param activity activity
     */
    public void startOCR(final Activity activity) {
        CPermission.getInstance().checkCameraPermission(activity, new CPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    if (CommonUtils.checkCameraHardware(activity)) {
                        Intent intent = new Intent(activity, CameraActivity.class);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_ORIENTATION, CameraActivity.ORIENTATION_LANDSCAPE);
                        intent.putExtra(CameraActivity.DATA_ENCRYPT_KEY, "");
                        activity.startActivityForResult(intent, Const.REQ_OCR_ID);
                    }
                } catch (ActivityNotFoundException e) {
                    CLog.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    /**
     * 차량 번호 인식
     *
     * @param activity activity
     */
    public void startCarNumCamera(final Activity activity) {
        CPermission.getInstance().checkCameraPermission(activity, new CPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    if (CommonUtils.checkCameraHardware(activity)) {
                        Intent intent = new Intent(activity, CameraActivity.class);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_TYPE, CameraActivity.TYPE_DOCUMENT_LPR);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_ORIENTATION, CameraActivity.ORIENTATION_LANDSCAPE);
                        intent.putExtra(CameraActivity.DATA_TITLE_MESSAGE_MANUAL, "카메라 영역에 [번호판영역]을 맞추고 촬영해 주세요");
                        intent.putExtra(CameraActivity.DATA_ENCRYPT_KEY, "");
                        activity.startActivityForResult(intent, Const.REQ_DOCUMENT_LPR);
                    }
                } catch (ActivityNotFoundException e) {
                    CLog.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    /**
     * A4 서류 인식
     *
     * @param activity activity
     */
    public void startA4Doc(final Activity activity) {
        CPermission.getInstance().checkCameraPermission(activity, new CPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    if (CommonUtils.checkCameraHardware(activity)) {
                        Intent intent = new Intent(activity, CameraActivity.class);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_TYPE, CameraActivity.TYPE_DOCUMENT_A4);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_ORIENTATION, CameraActivity.ORIENTATION_PORTRAIT);
                        intent.putExtra(CameraActivity.DATA_TITLE_MESSAGE_AUTO, "카메라 영역에 [문서(A4)]를 맞추면 자동촬영 됩니다");
                        intent.putExtra(CameraActivity.DATA_TITLE_MESSAGE_MANUAL, "카메라 영역에 [문서(A4)]를 맞추고 촬영해 주세요");
                        intent.putExtra(CameraActivity.DATA_ENCRYPT_KEY, "");
                        activity.startActivityForResult(intent, Const.REQ_DOCUMENT_A4);
                    }
                } catch (ActivityNotFoundException e) {
                    CLog.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }

    /**
     * 일반 카메라
     *
     * @param activity activity
     */
    public void startNormalCamera(final Activity activity) {
        CPermission.getInstance().checkCameraPermission(activity, new CPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                try {
                    if (CommonUtils.checkCameraHardware(activity)) {
                        Intent intent = new Intent(activity, CameraActivity.class);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_TYPE, CameraActivity.TYPE_DOCUMENT_ETC);
                        intent.putExtra(CameraActivity.DATA_DOCUMENT_ORIENTATION, CameraActivity.ORIENTATION_LANDSCAPE);
//                        intent.putExtra(CameraActivity.DATA_TITLE_MESSAGE_AUTO,   "카메라 영역에 [문서(A4)]를 맞추면 자동촬영 됩니다");
                        intent.putExtra(CameraActivity.DATA_TITLE_MESSAGE_MANUAL, "카메라 영역에 [촬영대상]을 맞추고 촬영해 주세요");
                        intent.putExtra(CameraActivity.DATA_ENCRYPT_KEY, "");
                        activity.startActivityForResult(intent, Const.REQ_DOCUMENT_ETC);
                    }
                } catch (ActivityNotFoundException e) {
                    CLog.printException(e);
                }
            }

            @Override
            public void onPermissionDenied() {

            }
        });
    }
}
