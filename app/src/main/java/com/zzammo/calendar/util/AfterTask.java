package com.zzammo.calendar.util;

/**
 * 비동기 실행 뒤 성공인지 실패인지에 따라 실행할 동작 <br>
 * <br>
 * {@code ifSuccess(Object result)}<br>
 * {@code ifFail(Object result)}
 */
public interface AfterTask {
    void ifSuccess(Object result);
    void ifFail(Object result);
}
