package com.asouwn.core.service;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    @Override
    public void serverStart(int port) {
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

//        httpServer.requestHandler(handler->{
//            System.out.println("handler");
//        });

        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port, httpListenHandler -> {
            if (httpListenHandler.succeeded())
                System.out.println("Successfully Start at port "+ port);
            else System.out.println("Failed to Start");
        });

    }
}
