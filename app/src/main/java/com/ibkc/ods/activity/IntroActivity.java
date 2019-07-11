package com.ibkc.ods.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.barun.appiron.android.AppIron;
import com.cardcam.carnum.CarNumRcgn;
import com.ibkc.CLog;
import com.ibkc.common.Common;
import com.ibkc.common.util.CommonUtils;
import com.ibkc.common.util.network.ConnectivityUtill;
import com.ibkc.ods.BuildConfig;
import com.ibkc.ods.Const;
import com.ibkc.ods.R;
import com.ibkc.ods.config.ProjectUrl;
import com.ibkc.ods.databinding.ActivityIntroBinding;
import com.ibkc.ods.util.CPermission;
import com.ibkc.ods.util.ui.DialogUtil;
import com.ibkc.ods.util.ui.PopupUtil;
import com.ibkc.product.mvaccine.VaccineConst;
import com.ibkc.product.mvaccine.VaccineManager;
import com.raonsecure.touchen_mguard_4_0.MDMAPI;
import com.raonsecure.touchen_mguard_4_0.MDMAPI.MGuardConnectionListener;
import com.raonsecure.touchen_mguard_4_0.MDMResultCode;

/**
 * Created by macpro on 2018. 8. 1..
 */

/**
 * IntroActivity.
 * MDM initialize, Vaccine process, appIron authorize 진행.
 */
public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding = null;
    public Context mContext = null;
    public AppInit mAppInit = new AppInit();
    private AnimationDrawable mAnimationDrawable = null;

    private int initResult = -1;

    public MDMAPI mMdm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        CLog.d(">> onCreate");
        //커먼 로그 설정
        if(BuildConfig.IS_DEV || BuildConfig.IS_TEST){
            Common.debug = true;
        }
        ProjectUrl.initWebUrl(getApplicationContext());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        mContext = this;

        //네트워크 체킹
        if(!ConnectivityUtill.isUseNetwork(getApplicationContext())){
            PopupUtil.showOneButtonPopup(IntroActivity.this, IntroActivity.this.getResources().getString(R.string.init_app_network)
                    , new PopupUtil.OnPositiveButtonClickListener() {
                        @Override
                        public void onPositiveClick() {
                            IntroActivity.this.finish();
                        }
                    });
            return;
        }


        // loading bar
        mAnimationDrawable = (AnimationDrawable) binding.progressBar.getBackground();

        // MDM 빼고 임시로 추가.. 디버그 모드일 때에는 MDM 통과 안시킨다..
        if (!BuildConfig.IS_MDM_USE) {
            if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
                (new StartTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                (new StartTask()).execute();
            }
        }

        CPermission.getInstance().checkStoragePermission(this, new CPermission.PermissionGrantedListener() {
            @Override
            public void onPermissionGranted() {
                if (BuildConfig.IS_MDM_USE) {
                    mMdm = MDMAPI.getInstance();
                    initMdmSdk();
                }
            }

            @Override
            public void onPermissionDenied() {
                finish();
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 차량 번호 인식 라이브러리 초기화. 필수로 넣어줘야함.
        CarNumRcgn.SetActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.cancel(VaccineConst.MESSAGE_ID);
        mNotificationManager.cancel(VaccineConst.MESSAGE_ID1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Const.RES_APP_FINSISH) { // 앱종료 요청시
            CommonUtils.killAppProcess(mContext);
            ((AppCompatActivity) mContext).finish();
            return;
        }

        switch (requestCode) {
            case VaccineConst.REQUEST_CODE: //백신 콜백
                if (mAppInit.isAppFinish() || mAppInit.isAppUpdate())
                    return; // 앱종료나 강제 업데이트시에는 백신 콜백을 받지 않는다

                if (resultCode == com.secureland.smartmedic.core.Constants.EMPTY_VIRUS) {
                    mAppInit.setVaccineInit(true); //백신 정상
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(VaccineConst.MESSAGE_ID);
                    mNotificationManager.cancel(VaccineConst.MESSAGE_ID1);
                } else if (resultCode == com.secureland.smartmedic.core.Constants.ROOTING_EXIT_APP
                        || resultCode == com.secureland.smartmedic.core.Constants.ROOTING_YES_OR_NO) {
                    mAppInit.setInitFail(mContext.getResources().getString(R.string.mv_msg_rootingexitapp));
                } else if (resultCode == com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE1) { //악성코드 탐지 후 사용자가 악성코드 삭제함.
                    mAppInit.setInitFail(mContext.getResources().getString(R.string.mv_msg_rootapp));
                } else if (resultCode == com.secureland.smartmedic.core.Constants.EXIST_VIRUS_CASE2) {
                    mAppInit.setInitFail(mContext.getResources().getString(R.string.mv_msg_rootapp));
                } else {

                }
            default:
                break;
        }

    }

    /**
     * MDM initialize
     */
    private void initMdmSdk() {
        CLog.d("++ RS_MDM_getVersion : "+mMdm.RS_MDM_getVersion());
            mMdm.RS_MDM_Init(this, BuildConfig.MDM_URL, new MGuardConnectionListener() {
                @Override
                public void onComplete(int resultCode) {
                    initResult = resultCode;
                    CLog.d("initResult : " + initResult);
                    switch (resultCode) {
                        case MDMResultCode.ERR_MDM_SUCCESS:
                            //바인드되면 파이도 패턴조회 함
                            CLog.d("bind Success");
                            checkAgent();
                            break;
                        case MDMResultCode.ERR_MDM_FAILED:
                            Log.e("MDMAPI", "MDMResultCode.ERR_MDM_FAILED bind");
                            break;
                        case MDMResultCode.ERR_MDM_NOT_INSTALLED:
                            Log.e("MDMAPI", "MDM is not Installed");
                            agentNotInstalled();
                            break;
                    }
                }
            });
    }

    /**
     * MDM agent check.
     * 스마트폰 루팅, MDM 설치 여부, MDM 버전 체크, MDM 해킹 체크.
     */
    private void checkAgent() {
        mMdm.RS_MDM_CheckAgent(this, new MDMAPI.MGuardCallbackListener() {
            @Override
            public void onCompleted(int resultCode, String msg) {
                CLog.d("checkagent...resultCode: " + resultCode + ", onCompleted :" + msg);

                if (resultCode == MDMResultCode.ERR_MDM_NOT_INSTALLED) {
                    agentNotInstalled();
                } else if (resultCode == MDMResultCode.ERR_MDM_DEVICE_ROOTING) {
                    showOneButtonDialog("ERR_MDM_DEVICE_ROOTING", "루팅된 단말 - msg : " + msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else if (resultCode == MDMResultCode.ERR_MDM_AGENT_NOT_LAST_VERSION) {
                    showTwoButtonDialog("ERR_MDM_AGENT_NOT_LAST_VERSION", "에이전트 최신 버전이 아님 - msg : " + msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                                    _mdm.RS_MDM_InstallAgent(null);
                            mMdm.RS_MDM_InstallAgent(IntroActivity.this, new MDMAPI.FileDownloadProgressListener() {
                                @Override
                                public void onCompleted() {
                                    CLog.d("installAgent...onCompleted");
                                }

                                @Override
                                public void onFailed() {
                                    CLog.d("installAgent...onFailed");
                                }
                            });

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else if (resultCode == MDMResultCode.ERR_MDM_FIND_HACKED_APP) {
                    showOneButtonDialog("ERR_MDM_FIND_HACKED_APP", "에이전트 위조/변조 되었음 - msg : " + msg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                } else if (resultCode == MDMResultCode.ERR_MDM_SUCCESS) {
                    CLog.d("++ mdm check success");
                    agentCheckSuccess();
                } else {
                    showOneButtonDialog("ERR_MDM_ETC", "MDM 기타 오류 - MDMResultCode : " + resultCode, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    CLog.d("resultCode ?????  :" + resultCode);
                }
            }

        });
    }

    /**
     * MDM agent check 성공 후 불리는 함수.
     * 여기서 mdm, vaccine process 관리해주는 asyncTask 실행.
     */
    private void agentCheckSuccess() {
        if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            (new StartTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            (new StartTask()).execute();
        }
    }

    /**
     * MDM 이 설치가 되어있지 않을 때 불리는 함수.
     * 확인 버튼을 누르면 MDM 다운로드 밑 설치 진행.
     * 취소 버튼을 누르면 앱 종료.
     */
    private void agentNotInstalled() {
        final DialogUtil msgDialog = new DialogUtil(this);
        msgDialog.setTitle("ERR_MDM_APP_NOT_INSTALLED");
        msgDialog.setMessage("에이전트 미 설치\n" + "확인버튼 클릭시 설치됩니다");
        msgDialog.setPositiveButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMdm.RS_MDM_InstallAgent(IntroActivity.this, new MDMAPI.FileDownloadProgressListener() {
                    @Override
                    public void onCompleted() {
                        CLog.d("installAgent...onCompleted");
                    }

                    @Override
                    public void onFailed() {
                        CLog.d("installAgent...onFailed");
                    }
                });
            }

        });
        msgDialog.setNegativeButton(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                msgDialog.dismiss();
                finish();
            }

        });
        msgDialog.show();
    }

    /**
     * 백신 초기화
     */
    public void initVaccine() {
        VaccineManager.getInstance().initVaccine();
        VaccineManager.getInstance().mini(IntroActivity.this);
    }

    /**
     * appIron 초기화
     */
    public void initAppIron() {
        String code = AppIron.getInstance(this).authApp(BuildConfig.APPIRON_URL);

        if (BuildConfig.IS_DEV || BuildConfig.IS_TEST) { //개발모드일때는 앱아이온 그냥 통과한다
            CLog.d("++ 개발 모드 앱아이온 통과 code = " + code);
            code = "0000";
        }

        if ("0000".equals(code)) {
            mAppInit.setAppIronInit(true);
        } else {
            Log.e("code", "앱 위변조 초기화에 실패 했습니다. 어플리케이션을 종료 합니다.");
            mAppInit.setInitFail(mContext.getResources().getString(R.string.init_appiron_terminate));
            // 1100 통신오류, 9001 위변조앱, else 실패
        }
    }

    /**
     * 확인, 취소 버튼 alert dialog
     * @param title 제목
     * @param msg 내용
     * @param positiveListener 확인 버튼 리스너
     * @param negativeListener 취소 버튼 리스너
     */
    private void showTwoButtonDialog(String title, String msg, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        DialogUtil msgDialog = new DialogUtil(IntroActivity.this);
        msgDialog.setTitle(title);
        msgDialog.setMessage(msg);
        msgDialog.setPositiveButton(positiveListener);
        msgDialog.setNegativeButton(negativeListener);
        msgDialog.show();
    }

    /**
     * 확인 버튼 alert dialog
     * @param title 제목
     * @param msg 내용
     * @param listener 확인 버튼 리스너
     */
    private void showOneButtonDialog(String title, String msg, DialogInterface.OnClickListener listener) {
        DialogUtil msgDialog = new DialogUtil(IntroActivity.this);
        msgDialog.setTitle(title);
        msgDialog.setMessage(msg);
        msgDialog.setPositiveButton(listener);
        msgDialog.setVisibleNegative(false);
        msgDialog.show();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * appIron, vaccine 실행 시작과 progressBar 시작.
     */
    class StartTask extends AsyncTask<Integer, Integer, Integer> {
        private final int MAX_COUNT = 10 * 30; // 뒷자리가 초

        @Override
        protected void onPreExecute() {
            mAnimationDrawable.start();
        }

        @Override
        protected Integer doInBackground(Integer... args) {
            initVaccine();
            initAppIron();
            for (int i = 0; i <= MAX_COUNT; i++) {
                if (mAppInit.isAppFinish()) {
                    return -1;  //앱 초기화 실패
                }
                if (mAppInit.isAppUpdate()) {
                    return 2;  //앱 강제 업데이트 필요
                }

                if (mAppInit.isAppInit()) { //앱 초기화 성공 확인
                    publishProgress(MAX_COUNT); //프로그레스 100으로 바꿈
                    return 1;
                }

                try {
                    publishProgress(i);
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    CLog.printException(e);
                    break;
                }
            }

            mAppInit.setInitFail(mContext.getResources().getString(R.string.init_app_terminate));
            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {  //앱 초기화 성공
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                mAnimationDrawable.stop();
                binding.progressBar.setBackground(getResources().getDrawable(R.drawable.loading_bar_12));
                ((AppCompatActivity) mContext).finish();
            } else if (result == 2) { //앱 강제 업데이트 필요
                //작업 요망
            } else { //앱 초기 기동 실패
                mAnimationDrawable.stop();
                PopupUtil.showOneButtonPopup(mContext, mAppInit.getAppTerminateMsg(), new PopupUtil.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveClick() {
                        ((AppCompatActivity) mContext).finish();
                    }
                });
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            mAnimationDrawable.stop();
//            binding.progressBar.setBackground(getResources().getDrawable(R.drawable.loading_bar_00));
        }
    }

    /**
     * App 초기화 상태를 저장하고 반환해주는 내부 클래스.
     */
    class AppInit {
        private boolean appTerminate = false;   // 앱 초기화 실패후 강제 종료 여부   true = 강제 종료 필요
        private boolean appUpdate = false;      // 앱 강제 업데이트 여부     true = 업데이트 필요
        private boolean appIronInit = false;    // 위변조 초기화 여부   true = 초기화성공
        private boolean vaccineInit = false;    // 백신 초기화 여부    true = 초기화성공
        private boolean mdmInit = true;
        private String appTerminateMsg = "";    // 앱 초기화 실패시 사용자 확인 메시지

        /**
         * 앱이 초기화 됬는지 확인한다.
         *
         * @return
         */
        public boolean isAppInit() {
            if (appIronInit && vaccineInit && !appUpdate && mdmInit) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 앱초기화 실패 설정
         *
         * @param msg 초기화 실패 메시지
         */
        public void setInitFail(String msg) {
            appTerminate = true;
            appTerminateMsg = msg;
        }

        /**
         * 앱업데이트 설정
         *
         * @param appUpdate
         */
        public void setAppUpdate(boolean appUpdate) {
            this.appUpdate = appUpdate;
        }

        /**
         * 앱 강제 종료 여부
         *
         * @return
         */
        public boolean isAppFinish() {
            return appTerminate;
        }

        /**
         * 앱 업데이트 여부
         *
         * @return
         */
        public boolean isAppUpdate() {
            return appUpdate;
        }

        /**
         * 앱종료 메시지 가져옴
         *
         * @return
         */
        public String getAppTerminateMsg() {
            return appTerminateMsg;
        }

        /**
         * 앱아이온 초기화 성공실패 설정
         *
         * @param appIronInit
         */
        public void setAppIronInit(boolean appIronInit) {
            this.appIronInit = appIronInit;
        }

        /**
         * 백신 초기화 성공 실패 설정
         *
         * @param vaccineInit
         */
        public void setVaccineInit(boolean vaccineInit) {
            this.vaccineInit = vaccineInit;
        }

        /**
         * MDM 초기화 성공 실패 설정
         *
         * @param mdmInit
         */
        public void setMdmInit(boolean mdmInit) {
            this.mdmInit = mdmInit;
        }
    }
}
