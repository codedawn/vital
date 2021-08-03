package com.codedawn.vital.config;

/**
 * @author codedawn
 * @date 2021-08-02 9:52
 */
public class VitalGenericOption {
    public static final VitalOption<Boolean> TCP_NODELAY = new VitalOption<>("TCP_NODELAY", true);

    public static final VitalOption<Boolean> SO_KEEPALIVE = new VitalOption<>("SO_KEEPALIVE", true);

    public static final VitalOption<Integer> SERVER_TCP_PORT = new VitalOption<>("SERVER_TCP_PORT", 8000);

    public static final VitalOption<String> SERVER_TCP_IP = new VitalOption<>("SERVER_TCP_IP", "127.0.0.1");

    /**
     * 单位是秒
     */
    public static final VitalOption<Integer> SERVER_READ_TIMEOUT = new VitalOption<>("SERVER_READ_TIMEOUT", 6 * 60);

    /**
     * 单位是毫秒
     */
    public static final VitalOption<Integer> RECONNECT_INTERVAL_TIME = new VitalOption<>("RECONNECT_INTERVAL_TIME", 3 * 1000);
    public static final VitalOption<Integer> CONNECT_TIMEOUT_MILLIS = new VitalOption<>("CONNECT_TIMEOUT_MILLIS", 5 * 1000);

    public static final VitalOption<String> ID = new VitalOption<>("ID", "123");
    public static final VitalOption<String> TOKEN = new VitalOption<>("TOKEN", "279579218126863786489826");



    /**
     * 修改配置参数
     * @param option
     * @param value
     * @param <T>
     * @return
     */
    public static <T> VitalOption<T> option(VitalOption<T> option,T value){
        return option.setValue(value);
    }

}
