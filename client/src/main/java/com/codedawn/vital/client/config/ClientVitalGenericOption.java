package com.codedawn.vital.client.config;

import com.codedawn.vital.server.config.VitalOption;

/**
 * @author codedawn
 * @date 2021-08-02 9:52
 */
public class ClientVitalGenericOption {
    /**
     * netty的配置
     */
    public static final VitalOption<Boolean> TCP_NODELAY = new VitalOption<>("TCP_NODELAY", true);

    /**
     * netty的配置
     */
    public static final VitalOption<Boolean> SO_KEEPALIVE = new VitalOption<>("SO_KEEPALIVE", true);

    /**
     * TCP服务器绑定的端口
     */
    public static final VitalOption<Integer> SERVER_TCP_PORT = new VitalOption<>("SERVER_TCP_PORT", 8000);

    /**
     * 服务器ip，客户端连接时使用
     */
    public static final VitalOption<String> SERVER_TCP_IP = new VitalOption<>("SERVER_TCP_IP", "127.0.0.1");


    /**
     * 心跳包发送间隔，单位是毫秒
     */
    public static final VitalOption<Integer> HEART_BEAT_INTERVAL_TIME = new VitalOption<>("HEART_BEAT_INTERVAL_TIME", 3*60*1000);

    /**
     * 客户端重连间隔，单位是毫秒
     */
    public static final VitalOption<Integer> RECONNECT_INTERVAL_TIME = new VitalOption<>("RECONNECT_INTERVAL_TIME", 3 * 1000);
    /**
     * netty配置，连接超时时间
     */
    public  static final VitalOption<Integer> CONNECT_TIMEOUT_MILLIS = new VitalOption<>("CONNECT_TIMEOUT_MILLIS", 3 * 1000);

    /**
     * id，客户端认证时使用
     */
    public static final VitalOption<String> ID = new VitalOption<>("ID", "123");
    /**
     * token，客户端认证时使用
     */
    public static final VitalOption<String> TOKEN = new VitalOption<>("TOKEN", "279579218126863786489826");


    /**
     * sendQos 消息最大重发次数
     */
    public static final VitalOption<Integer> SEND_QOS_MAX_RETRY_COUNT= new VitalOption<>("SEND_QOS_MAX_RETRY_COUNT", 2);
    /**
     *  sendQos 消息最大延迟时间，超时重发
     */
    public static final VitalOption<Integer> SEND_QOS_MAX_DELAY_TIME= new VitalOption<>("SEND_QOS_MAX_DELAY_TIME", 5*1000);
    /**
     * sendQos遍历消息的间隔时间
     */
    public static final VitalOption<Integer> SEND_QOS_INTERVAL_TIME= new VitalOption<>("SEND_QOS_INTERVAL_TIME", 5*1000);

    /**
     * receiveQos遍历消息的间隔时间
     */
    public static final VitalOption<Integer> RECEIVE_QOS_INTERVAL_TIME= new VitalOption<>("RECEIVE_QOS_INTERVAL_TIME", 4*1000);

    /**
     * receiveQos消息的保存时间
     */
    public static final VitalOption<Integer> RECEIVE_QOS_MAX_SAVE_TIME= new VitalOption<>("RECEIVE_QOS_MAX_SAVE_TIME", 8*1000);
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
