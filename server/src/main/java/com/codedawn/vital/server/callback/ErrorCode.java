package com.codedawn.vital.server.callback;

/**
 * @author codedawn
 * @date 2021-08-07 9:48
 */
public enum ErrorCode {
    /**
     *
     */
    AUTH_FAILED(200,"认证失败"),
    SEND_FAILED(210,"发送失败"),
    ILLEGAL_DISAUTHMESSAGE(220,"非法disAuthMessage消息，当前connection中的id，与disAuthMessage消息中的id不同");


    private int code;
    private String extra;

    ErrorCode(int code, String extra) {
        this.code = code;
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "code=" + code +
                ", extra='" + extra + '\'' +
                '}';
    }
}
