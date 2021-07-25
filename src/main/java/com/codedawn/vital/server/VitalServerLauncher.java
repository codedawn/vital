package com.codedawn.vital.server;

/**
 * @author codedawn
 * @date 2021-07-21 14:21
 */
public class VitalServerLauncher {



    public static void main(String[] args) {
        new TCPServer().start();
    }
}
