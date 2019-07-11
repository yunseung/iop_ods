package com.ibkc.common.util.reflection;


import java.lang.reflect.Field;

/**
 * Class 관련 클래스
 */
public class ReflectionUtil {
    /**
     * 클래스에 멤버변수의 값을 가져온다
     * @param targetClass 클래스
     * @param classValue 멤버 변수 이름
     * @return
     */
    public static Object getMemberFiled(Class<?> targetClass, String classValue) {
        try {
            Field field = targetClass.getField(classValue);
            return field.get(targetClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
