package com.codedawn.vital.server.logic;

import com.codedawn.vital.server.context.MessageContext;

import java.util.List;

/**
 * 默认消息转发策略
 * @author codedawn
 * @date 2021-09-02 12:16
 */
public class TransmitLogic {
    /**
     * 群发时实现该方法，返回要转发的id数组
     * @param messageContext
     * @param toId
     * @return
     */
     public List<String> onGroup(MessageContext messageContext, String toId){
         return null;
     }

    /**
     * 单聊时实现该方法，返回要转发的id
     * @param messageContext
     * @param toId
     * @return
     */
     public String onOne(MessageContext messageContext, String toId) {
         return toId;
     }

}
