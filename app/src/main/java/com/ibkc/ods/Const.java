package com.ibkc.ods;

/**
 * Created by macpro on 2018. 6. 21..
 */

/**
 * ODS CONST.
 */
public class Const {
    public static final String URL = "url";

    /**
     * new window web view 에서 새로운 메뉴로 갈 때 마다 이전 기록을 지울 수 있게 하는 flag.
     */
    public static final String CLEAR_HISTORY = "clear history";
    public static final String INTENT_KEY_DATA = "data";

    /**
     * 부모 웹뷰
     */
    public static final int PARENTS_WEB = 10;

    /**
     * 자식 웹뷰
     */
    public static final int CHILD_WEB = 11;

    public static final String OCR_CALLBACK_SCRIPT_NAME = "OCR_CALLBACK";

    public static final int REQ_PERMISSION = 3000; //권한 설정
    public static final int RES_APP_FINSISH = 9999; //앱 종료

    /**
     * OCR
     */
    public static final int REQ_OCR_ID = 10001;
    public static final int REQ_DOCUMENT_A4 = 10002;
    public static final int REQ_DOCUMENT_ETC = 10003;
    public static final int REQ_DOCUMENT_LPR = 10004;
    public static final int REQ_TRANSKEY = 2000;
    public static final int MSG_LPRImgRcgn = 10005;
    public static final int MSG_LPREndProcess = 10006;
    public static final int MSG_RESULT_SUCCESS_LPR = 10007;
    public static final int MSG_RESULT_SUCCESS = 10008;
    public static final int MSG_RESULT_FAILE = 10009;

    /**
     * javascript
     */
    public static final int REQ_NEW_WEB = 101;


    /**
     * activity request code
     */
    public static final int REQ_REPORT_EQUIP_LIST = 200;
    public static final int REQ_INST_MEDI_REPORT_ACTIVITY = 201;
    public static final int REQ_VISIT_PHOTO_ACTIVITY = 202;
    public static final int REQ_LEAS_REPORT_ACTIVITY = 203;
    public static final int REQ_SELECT_CAPTURE = 204;


    /**
     * intent extras
     */
    public static final String REPORT_OBJ = "REPORT_OBJ";
    public static final String IMAGE = "IMAGE";
    public static final String CALL_TYPE = "CALL_TYPE";
    public static final String CALLBACK = "callback"; //팝업 시작시 콜백 소문자로해야 리턴이감
    public static final String RESULT = "RESULT";
    public static final String CALL_TYPE_JSP = "JSP";
    public static final String TOP_MENU = "TOP_MENU";
    public static final String IS_FINISH = "BACK";

    /**
     * camera type (ocr, car, default etc ..) javascript API function name : startOCR
     */
    public static final String TYPE_IDCARD = "idcard";
    public static final String TYPE_VISIT = "visit";
    public static final String TYPE_CAR = "car";
    public static final String TYPE_INST = "inst";
    public static final String TYPE_LEAS = "leas";
    public static final String TYPE_MEDI = "medi";


    public static final String SUCCESS = "succ"; // jsp 통신 성공 (사진 전송)
    public static final String FAIL = "fail"; // jsp 통신 실패 (사진 전송)
    public static final String PAGE = "page"; // 약정 진행 중 뒤로가기 버튼 클릭 시 초기 화면으로
    public static final String HOME = "home"; // 약정 진행 중 홈 버튼 클릭 시 홈으로
    public static final String LOGOUT = "logout"; // 약정 진행 중 로그아웃 버튼 클릭 시 팝업 후 로그아웃
}
