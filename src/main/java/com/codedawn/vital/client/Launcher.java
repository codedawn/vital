package com.codedawn.vital.client;

import com.codedawn.vital.client.connector.TCPConnect;

/**
 * @author codedawn
 * @date 2021-07-24 11:02
 */
public class Launcher {
    public static void main(String[] args) {
        TCPConnect.getInstance().start();
    }
}
