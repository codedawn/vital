package com.codedawn.vital.server.processor.impl.server;

import com.codedawn.vital.server.context.DefaultMessageContext;

import java.util.List;

/**
 * 消息转发策略接口
 * @author codedawn
 * @date 2021-09-02 12:16
 */
public interface Transmitter {
    /**
     * 群发时实现该方法，返回要转发的id数组
     * @param defaultMessageContext
     * @param toId
     * @return
     */
     List<String> onGroup(DefaultMessageContext defaultMessageContext, String toId);

    /**
     * 单聊时实现该方法，返回要转发的id
     * @param defaultMessageContext
     * @param toId
     * @return
     */
     String onOne(DefaultMessageContext defaultMessageContext, String toId) ;
}
