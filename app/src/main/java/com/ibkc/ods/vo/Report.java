package com.ibkc.ods.vo;

import java.util.ArrayList;

/**
 * Created by macpro on 2018. 7. 20..
 */

/**
 * 할부, 메디컬, 리스 마다 있는 서류 목록을 만들어주는 class.
 */
public class Report {
    private ArrayList<REPORT> mInst = new ArrayList<>();
    private ArrayList<REPORT> mMedi = new ArrayList<>();
    private ArrayList<REPORT> mLeas = new ArrayList<>();

    public static Report getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final Report INSTANCE = new Report();
    }

    private Report() {
        for (REPORT report : REPORT.values()) {
            if (report.containsType(REPORT_TYPE.INST)) {
                mInst.add(report);
            }
            if (report.containsType(REPORT_TYPE.MEDI)) {
                mMedi.add(report);
            }
            if (report.containsType(REPORT_TYPE.LEAS)) {
                mLeas.add(report);
            }
        }
    }

    public ArrayList<REPORT> getInst() {
        ArrayList<REPORT> list = new ArrayList<>();

        for (int i = 0; i < mInst.size(); i++) {
            list.add(mInst.get(i));
        }
        return list;
    }

    public ArrayList<REPORT> getMedi() {
        ArrayList<REPORT> list = new ArrayList<>();

        for (int i = 0; i < mMedi.size(); i++) {
            list.add(mMedi.get(i));
        }
        return list;
    }

    public ArrayList<REPORT> getLeas() {
        ArrayList<REPORT> list = new ArrayList<>();

        for (int i = 0; i < mLeas.size(); i++) {
            list.add(mLeas.get(i));
        }
        return list;
    }

    private enum REPORT_TYPE {
        INST,
        MEDI,
        LEAS
    }

    public enum REPORT {
        인감증명서("인감증명서", "PD170001020", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        사업자등록증("사업자등록증", "PD170001018", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        신분증("신분증", "PD170001022", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        주민등록등본("주민등록등본", "PD170002001", REPORT_TYPE.INST),
        부가가치세과세표준증명원("부가가치세 과세표준증명원", "PD170002012", REPORT_TYPE.INST),
        통장사본("통장사본", "PD170001024", REPORT_TYPE.INST),
        국세완납증명서("국세완납증명서", "PD170002014", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        지방세완납증명서("지방세완납증명서", "PD170002032", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        매매계약서("매매계약서", "PD170001086", REPORT_TYPE.INST),
        매출처별세금계산서합계표("매출처별 세금계산서 합계표", "PD170001087", REPORT_TYPE.INST),
        건강보험자격득실확인서("건강보험 자격득실 확인서", "PD170002030", REPORT_TYPE.INST),
        보유장비등록증("보유장비 등록증", "PD170001088", REPORT_TYPE.INST),
        법인등기부등본("법인등기부 등본", "PD170001021", REPORT_TYPE.INST),
        재무재표("재무재표", "PD170002005", REPORT_TYPE.INST),
        정관("정관", "PD170001023", REPORT_TYPE.INST),
        이사회결의서("이사회결의서", "PD170001019", REPORT_TYPE.INST),
        보험급여환출("보험급여환출", "PD170001083", REPORT_TYPE.MEDI),
        사대보험완납확인서("4대보험 완납확인서", "PD170001084", REPORT_TYPE.MEDI),
        의료기관개설허가증("의료기관 개설 허가증", "PD170001085", REPORT_TYPE.MEDI),
        할부메디칼기타("기타", "PD170002047", REPORT_TYPE.INST, REPORT_TYPE.MEDI),
        시리얼No("시리얼No", "PD170016001", REPORT_TYPE.LEAS),
        기기사진("기기사진", "PD170016002", REPORT_TYPE.LEAS),
        스티커사진("스티커사진", "PD170016003", REPORT_TYPE.LEAS),
        회사사진("회사사진", "PD170016004", REPORT_TYPE.LEAS),
        명함("명함", "PD170016005", REPORT_TYPE.LEAS),
        리스기타("기타", "PD170016099", REPORT_TYPE.LEAS);


        REPORT(String name, String code, REPORT_TYPE... types) {
            this.name = name;
            this.code = code;
            this.types = types;
        }

        public String name;
        public String code;
        REPORT_TYPE[] types;

        public boolean containsType(REPORT_TYPE type) {
            for (REPORT_TYPE t : this.types) {
                if (type == t) {
                    return true;
                }
            }
            return false;
        }
    }
}
