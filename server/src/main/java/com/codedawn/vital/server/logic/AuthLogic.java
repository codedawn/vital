package com.codedawn.vital.server.logic;

import com.codedawn.vital.server.context.MessageContext;
import com.codedawn.vital.server.proto.VitalPB;

public class AuthLogic {
    /**
     * 重写该方法，实现认证逻辑
     * @param messageContext
     * @param authRequestMessage
     * @return 认证成功返回true，否则返回false
     */
    public boolean onAuth(MessageContext messageContext, VitalPB.AuthRequestMessage authRequestMessage) {
        return true;
    }
}
