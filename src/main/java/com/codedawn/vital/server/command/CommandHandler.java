package com.codedawn.vital.server.command;

import com.codedawn.vital.server.context.ServerContext;
import com.codedawn.vital.server.proto.VitalProtobuf;

/**
 * @author codedawn
 * @date 2021-07-24 22:31
 */
public interface CommandHandler {
     void handle(ServerContext serverContext, VitalProtobuf.Protocol protocol);
}
