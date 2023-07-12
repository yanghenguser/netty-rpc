package com.example.rpcserver.server;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.rpcapi.module.RpcRequest;
import com.example.rpcapi.module.RpcResponse;
import com.example.rpcapi.Serializer.impl.JSONSerializer;
import com.example.rpcapi.utils.RpcDecoder;
import com.example.rpcapi.utils.RpcEncoder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Slf4j
public class NettyServer{
    private EventLoopGroup boss = null;
    private EventLoopGroup worker = null;
    @Autowired
    private ServerHandler serverHandler;

    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        p.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));
                        p.addLast(serverHandler);
                    }
                });
        bind(serverBootstrap, 9999);
    }
    /**
     * If port binding fails, port number + 1, rebind
     *
     * @param serverBootstrap
     * @param port
     */
    public void bind(final ServerBootstrap serverBootstrap,int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("port[ {} ] Binding success",port);
            } else {
                log.error("port[ {} ] Binding failed", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully().sync();
        log.info("close Netty");
    }
}
