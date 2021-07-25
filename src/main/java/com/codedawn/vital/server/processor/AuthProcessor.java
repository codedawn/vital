package com.codedawn.vital.server.processor;

import com.codedawn.vital.server.proto.VitalProtobuf;
import io.netty.channel.Channel;

/**
 * @author codedawn
 * @date 2021-07-23 10:45
 */
public interface AuthProcessor {

    boolean verify(Channel channel, VitalProtobuf.AuthMessage msg);


}
