package com.example.rpcclient.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import com.example.rpcapi.module.RpcRequest;
import com.example.rpcapi.module.RpcResponse;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author yangheng <yangheng@kuaishou.com>
 * Created on 2023-07-12
 */
public class ClientHandler extends ChannelDuplexHandler {
    /**
     * Maintaining mapping relationship between request object ID and response result Future using Map
     */
    private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse) {
            //Get the response object
            RpcResponse response = (RpcResponse) msg;
            DefaultFuture defaultFuture =
                    futureMap.get(response.getRequestId());
            //Write the result to DefaultFuture
            defaultFuture.setResponse(response);
        }
        super.channelRead(ctx,msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RpcRequest) {
            RpcRequest request = (RpcRequest) msg;
            //Before sending the request object, save the request ID and construct a mapping relationship with the response Future.
            futureMap.putIfAbsent(request.getRequestId(), new DefaultFuture());
        }
        super.write(ctx, msg, promise);
    }



    /**
     * Get response results
     *
     * @param requsetId
     * @return
     */
    public RpcResponse getRpcResponse(String requsetId) {
        try {
            // 暂时放在这里，看着没什么用
            futureMap.putIfAbsent(requsetId, new DefaultFuture());
            DefaultFuture future = futureMap.get(requsetId);
            return future.getRpcResponse(5000);
        } finally {
            //After success, remove from map
            futureMap.remove(requsetId);
        }
    }
}
