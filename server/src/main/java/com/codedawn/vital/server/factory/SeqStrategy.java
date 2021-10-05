package com.codedawn.vital.server.factory;

/**
 * 消息序列策略接口
 */
public interface SeqStrategy {
    /**
     * 获取一个消息序列，用于qos
     * @return
     */
    public  String getSeqID();
}
