package com.codedawn.vital.command;

import com.codedawn.vital.context.MessageContext;

/**
 * @author codedawn
 * @date 2021-07-24 22:31
 */
public interface CommandHandler {
     void handle(MessageContext messageContext, Object msg);
}
