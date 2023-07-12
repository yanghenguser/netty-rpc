package com.example.rpcserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.rpcserver.server.NettyServer;

@SpringBootApplication
public class RpcServerApplication {

    public static void main(String[] args) {
        new NettyServer().start();
    }

}
