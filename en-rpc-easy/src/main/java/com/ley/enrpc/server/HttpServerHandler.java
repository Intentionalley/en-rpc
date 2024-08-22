package com.ley.enrpc.server;

import com.ley.enrpc.model.RpcRequest;
import com.ley.enrpc.model.RpcResponse;
import com.ley.enrpc.registry.LocalRegistry;
import com.ley.enrpc.serializer.JdkSerializer;
import com.ley.enrpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;
/**
 * 业务流程如下：
 *
 * 1. 反序列化请求为对象，并从请求对象中获取参数。
 * 2. 根据服务名称从本地注册器中获取到对应的服务实现类。
 * 3. 通过反射机制调用方法，得到返回结果。
 * 4. 对返回结果进行封装和序列化，并写入到响应中。
 */

/**
 * Http 处理请求
 */

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 指定序列化器
        final JdkSerializer serializer = new JdkSerializer();
        // 输出日志
        System.out.println("Received request: " + request.method() + " "+ request.uri());
        // 异步处理 Http 请求
        request.bodyHandler(body ->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;

            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest == null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try {
                // 获取要调用的服务实现类
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 通过 RpcRequest 中传递的方法名称、参数类型、参数值 + 反射 进行方法调用
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setMessage("ok");
                rpcResponse.setDataType(method.getReturnType());
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request,rpcResponse,serializer);
        });
    }

    /**
     * 响应
     *
     * @param request
     * @param rpcResponse
     * @param serializer
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer){
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }

}
