package com.codedawn.vital.server.command;

import com.codedawn.vital.server.context.MessageContext;

/**
 * @author codedawn
 * @date 2021-07-24 22:31
 */
public interface CommandHandler<T extends MessageContext> {
     void handle(T messageContext, Object msg);
}
