package com.ibkc.ods.vo;

import java.io.Serializable;

/**
 * Created by macpro on 2018. 7. 5..
 */

/**
 * 추가서류에서 사진 촬영 한 서류에 대한 객체를 만들어주는 vo class.
 */
@SuppressWarnings("serial")
public class AdditionReport implements Serializable {
    private byte[] imageByteArray;
    private String name;
    private String typeCode;

    public AdditionReport(byte[] imageByteArray, String name, String typecode) {
        this.imageByteArray = imageByteArray.clone();
        this.name = name;
        this.typeCode = typecode;
    }

    public byte[] getImageByteArray() {
        return imageByteArray.clone();
    }

    public String getName() {
        return name;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setImageByteArray(byte[] imageByteArray) {
        byte[] b = new byte[imageByteArray.length];
        for (int i = 0; i < imageByteArray.length; i++) {
            b[i] = imageByteArray[i];
        }
        this.imageByteArray = b;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
