package com.ibkc.ods.util.eventbus;

public class WebConsoleErrorEventBus {
    public static final int NONE = 0;
    public static final int BACK = 1;

    public String msg = null;
    public int type = NONE;
    public WebConsoleErrorEventBus(String msg, int type) {
        this.msg = msg;
        this.type = type;
    }
}
