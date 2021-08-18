package com.codedawn.vital.server.session;

/**
 * @author codedawn
 * @date 2021-07-28 10:54
 */
public interface ConnectionEventProcessor {
    public void onEvent(String remoteAddress, Connection connection);
}
