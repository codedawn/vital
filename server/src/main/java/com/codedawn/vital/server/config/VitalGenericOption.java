package com.codedawn.vital.server.config;

/**
 * @author codedawn
 * @date 2021-08-02 9:52
 */
public class VitalGenericOption {
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
     * websocket服务器绑定的端口
     */
    public static final VitalOption<Integer> SERVER_WEBSOCKET_PORT = new VitalOption<>("SERVER_WEBSOCKET_PORT", 8001);


    /**
     * 读超时时间，单位是秒
     */
    public static final VitalOption<Integer> SERVER_READ_TIMEOUT = new VitalOption<>("SERVER_READ_TIMEOUT", 7 * 60);


    /**
     * 服务器processorManage线程池最小线程数
     */
    public static final VitalOption<Integer> PROCESSOR_MIN_POOlSIZE= new VitalOption<>("PROCESSOR_MIN_POOlSIZE", Runtime.getRuntime().availableProcessors());
    /**
     *  服务器processorManage线程池最大线程数
     */
    public static final VitalOption<Integer> PROCESSOR_MAX_POOlSIZE= new VitalOption<>("PROCESSOR_MAX_POOlSIZE", Runtime.getRuntime().availableProcessors()*2);
    /**
     *  服务器processorManage线程池线程存活时间
     */
    public static final VitalOption<Integer> PROCESSOR_KEEP_ALIVE_TIME= new VitalOption<>("PROCESSOR_KEEP_ALIVE_TIME", 60);
    /**
     *  服务器processorManage线程池队列大小
     */
    public static final VitalOption<Integer> PROCESSOR_QUEUE_SIZE= new VitalOption<>("PROCESSOR_QUEUE_SIZE", 10000);

    /**
     * userProcessorManage线程池参数
     */
    public static final VitalOption<Integer> USER_PROCESSOR_MIN_POOlSIZE= new VitalOption<>("USER_PROCESSOR_MIN_POOlSIZE", Runtime.getRuntime().availableProcessors()*2);
    public static final VitalOption<Integer> USER_PROCESSOR_MAX_POOlSIZE= new VitalOption<>("USER_PROCESSOR_MAX_POOlSIZE", Runtime.getRuntime().availableProcessors()*5);
    public static final VitalOption<Integer> USER_PROCESSOR_KEEP_ALIVE_TIME= new VitalOption<>("USER_PROCESSOR_KEEP_ALIVE_TIME", 60);
    public static final VitalOption<Integer> USER_PROCESSOR_QUEUE_SIZE= new VitalOption<>("USER_PROCESSOR_QUEUE_SIZE", 10000);



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
    public static final VitalOption<Integer> RECEIVE_QOS_INTERVAL_TIME= new VitalOption<>("RECEIVE_QOS_INTERVAL_TIME", 10*1000);

    /**
     * receiveQos消息的保存时间
     */
    public static final VitalOption<Integer> RECEIVE_QOS_MAX_SAVE_TIME= new VitalOption<>("RECEIVE_QOS_MAX_SAVE_TIME", 60*1000);


    /**
     * 是否启动集群部署
     */
    public static final VitalOption<Boolean> CLUSTER = new VitalOption<>("CLUSTER",false);

    /**
     * 集群部署监听端口
     */
    public static final VitalOption<Integer> CLUSTER_PORT = new VitalOption<>("CLUSTER_PORT", 9090);

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


    /**
     * 雪花算法的work id
     */
    public static final VitalOption<Integer> WORK_ID = new VitalOption<>("WORK_ID", 0);

    /**
     * 雪花算法的data center id
     */
    public static final VitalOption<Integer> DATA_CENTER_ID = new VitalOption<>("DATA_CENTER_ID", 0);

}
