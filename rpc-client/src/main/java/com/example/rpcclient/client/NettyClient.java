package com.example.rpcclient.client;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.alibaba.fastjson.JSON;
import com.example.rpcapi.Serializer.impl.JSONSerializer;
import com.example.rpcapi.module.RpcRequest;
import com.example.rpcapi.module.RpcResponse;
import com.example.rpcapi.utils.RpcDecoder;
import com.example.rpcapi.utils.RpcEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
@Slf4j
public class NettyClient {
    private EventLoopGroup eventLoopGroup;
    private Channel channel;
    private ClientHandler clientHandler;
    private String host;
    private Integer port;
    private static final int MAX_RETRY = 5;
    public NettyClient(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
    public void connect() {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        //Startup class
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                //Channel for specified transmission
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //Add encoder
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JSONSerializer()));
                        //Add Decoder
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JSONSerializer()));
                        //Request Processing Class
                        pipeline.addLast(clientHandler);
                    }
                });
        connect(bootstrap, host, port, MAX_RETRY);
    }

    /**
     * Failure reconnection mechanism, refer to Netty's entry actual gold digging Brochure
     *
     * @param bootstrap
     * @param host
     * @param port
     * @param retry
     */
    private void connect(Bootstrap bootstrap, String host, int port, int retry) {
        ChannelFuture channelFuture = bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("Successful connection to server");
            } else if (retry == 0) {
                log.error("The number of retries has been exhausted and the connection has been abandoned");
            } else {
                //The number of reconnections:
                int order = (MAX_RETRY - retry) + 1;
                //The interval between reconnections
                int delay = 1 << order;
                log.error("{} : Connection failed, para. {} Reconnect....", new Date(), order);
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
        channel = channelFuture.channel();
    }

    /**
     * send message
     *
     * @param request
     * @return
     */
    public RpcResponse send(final RpcRequest request) throws InterruptedException {
        log.info("send  req = {}", JSON.toJSONString(request));
        channel.writeAndFlush(request);
        RpcResponse rpcResponse = clientHandler.getRpcResponse(request.getRequestId());
        log.info("get resp = {}", JSON.toJSONString(rpcResponse));

        return rpcResponse;
    }
    @PreDestroy
    public void close() {
        eventLoopGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
    }
}

