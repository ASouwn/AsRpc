package com.asouwn.core.test;

import com.asouwn.core.service.VertxHttpServer;

public class ServerStartTest {
    public static void main(String[] args) {
        new VertxHttpServer().serverStart(8080);
    }
}
