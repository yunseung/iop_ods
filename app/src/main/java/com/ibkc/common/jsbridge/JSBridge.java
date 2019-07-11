package com.ibkc.common.jsbridge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class JSBridge{
    /**
     * 네이티브 기능을 수행하기 위한 자바스크립트 인터페이스 어노테이션
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface JSApi {
        String invokeMethod() default "no api";
        String explain() default "api 설명";
        String[] param() default "api 호출 param";
    }
}
