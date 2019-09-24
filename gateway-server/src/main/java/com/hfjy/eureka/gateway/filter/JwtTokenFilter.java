package com.hfjy.eureka.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.hfjy.eureka.gateway.jjwt.JwtUtil;
import com.hfjy.eureka.gateway.model.ReturnValue;
import com.hfjy.eureka.gateway.model.UserInfo;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 描述: JwtToken 过滤器
 *
 * @Auther: fxl
 * @Date: 2019/7/9 15:49
 */
@Component
@Slf4j
public class JwtTokenFilter implements GlobalFilter, Ordered {


    /**
     * 过滤器
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

//        String url = exchange.getRequest().getURI().getPath();
        //跳过不需要验证的路径
//        if (null != skipAuthUrls && Arrays.asList(skipAuthUrls).contains(url)) {
//            return chain.filter(exchange);
//        }

        //获取token
        String token = exchange.getRequest().getHeaders().getFirst("token");
        ServerHttpResponse resp = exchange.getResponse();
        if (StringUtils.isBlank(token)) {
            //没有token
            return errorMsg(resp, "请登陆");
        } else {
            //有token
            try {
                UserInfo userInfo = JwtUtil.parseJWT(token);
                //解析成功后加入到请求的header之中
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .header("userName", userInfo.getUserName())
                        .header("userId", userInfo.getUserId().toString())
                        .build();
                return chain.filter(exchange.mutate().request(request).build());
            } catch (ExpiredJwtException e) {
                log.error(e.getMessage(), e);
                if (e.getMessage().contains("Allowed clock skew")) {
                    return errorMsg(resp, "认证过期");
                } else {
                    return errorMsg(resp, "认证失败");
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return errorMsg(resp, "认证失败");
            }
        }
    }

    /**
     * 认证错误输出
     *
     * @param resp 响应对象
     * @param mess 错误信息
     * @return
     */
    private Mono<Void> errorMsg(ServerHttpResponse resp, String mess) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        ReturnValue<String> returnData = new ReturnValue(403, mess, mess);
        String returnStr = "";
        try {
            returnStr = JSONObject.toJSONString(returnData);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(returnStr.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

