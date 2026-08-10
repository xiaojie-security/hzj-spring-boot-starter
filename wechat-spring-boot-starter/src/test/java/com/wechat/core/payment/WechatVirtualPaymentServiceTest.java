package com.wechat.core.payment;

import com.google.gson.JsonObject;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.domain.WechatAccessTokenResponse;
import com.hzj.wechat.core.payment.domain.WechatQueryUserBalanceRequest;
import com.hzj.wechat.core.payment.domain.WechatSubmitSubscribePayOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatVirtualPaymentResponse;
import com.hzj.wechat.core.payment.service.WechatVirtualPaymentException;
import com.hzj.wechat.core.payment.service.impl.DefaultWechatVirtualPaymentService;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.entity.WechatVirtualPaymentConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 微信虚拟支付服务合约测试。
 */
class WechatVirtualPaymentServiceTest {

    @Test
    void shouldSendBalanceRequestWithSignaturesInQueryAndEnvInBody() throws Exception {
        AtomicReference<String> query = new AtomicReference<>();
        AtomicReference<String> body = new AtomicReference<>();
        HttpServer server = createServer("/xpay/query_user_balance", exchange -> {
            query.set(exchange.getRequestURI().getQuery());
            body.set(readBody(exchange));
            writeResponse(exchange, 200, "{\"errcode\":0,\"errmsg\":\"\",\"balance\":123}");
        });
        try {
            DefaultWechatVirtualPaymentService service = service(server, 1);
            WechatQueryUserBalanceRequest request = new WechatQueryUserBalanceRequest();
            request.setSignature("user-signature");
            request.setPaySig("pay-signature");
            request.setOpenid("openid");
            request.setUserIp("127.0.0.1");
            WechatVirtualPaymentResponse response = service.queryUserBalance(request);

            assertEquals(123L, response.getBalance());
            assertEquals("access_token=access-token&signature=user-signature&pay_sig=pay-signature", query.get());
            JsonObject requestJson = WechatPayUtils.fromJson(body.get(), JsonObject.class);
            assertEquals(1, requestJson.get("env").getAsInt());
            assertEquals("openid", requestJson.get("openid").getAsString());
            assertEquals("127.0.0.1", requestJson.get("user_ip").getAsString());
            assertFalse(requestJson.has("signature"));
            assertFalse(requestJson.has("paySig"));
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldApplyConfiguredDefaultsToSubscribePayment() throws Exception {
        AtomicReference<String> body = new AtomicReference<>();
        HttpServer server = createServer("/xpay/submit_subscribe_pay_order", exchange -> {
            body.set(readBody(exchange));
            writeResponse(exchange, 200, "{\"errcode\":0,\"errmsg\":\"\"}");
        });
        try {
            DefaultWechatVirtualPaymentService service = service(server, 0);
            WechatSubmitSubscribePayOrderRequest request = new WechatSubmitSubscribePayOrderRequest();
            request.setPaySig("pay-signature");
            request.setOpenid("openid");
            request.setProductId("product");
            request.setDeductPrice(100L);
            request.setOrderId("order-123");
            service.submitSubscribePayOrder(request);

            JsonObject requestJson = WechatPayUtils.fromJson(body.get(), JsonObject.class);
            assertEquals("offer-id", requestJson.get("offer_id").getAsString());
            assertEquals("CNY", requestJson.get("currency_type").getAsString());
            assertEquals(1, requestJson.get("buy_quantity").getAsInt());
            assertEquals(0, requestJson.get("env").getAsInt());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldThrowExceptionWhenWechatReturnsBusinessError() throws Exception {
        HttpServer server = createServer("/xpay/query_user_balance", exchange -> writeResponse(
                exchange, 200, "{\"errcode\":268490003,\"errmsg\":\"invalid signature\"}"));
        try {
            DefaultWechatVirtualPaymentService service = service(server, 0);
            WechatQueryUserBalanceRequest request = new WechatQueryUserBalanceRequest();
            request.setSignature("user-signature");
            request.setPaySig("pay-signature");
            request.setOpenid("openid");
            request.setUserIp("127.0.0.1");

            WechatVirtualPaymentException exception = assertThrows(WechatVirtualPaymentException.class,
                    () -> service.queryUserBalance(request));
            assertEquals(268490003, exception.getErrcode());
        } finally {
            server.stop(0);
        }
    }

    private DefaultWechatVirtualPaymentService service(HttpServer server, int env) {
        WechatVirtualPaymentConfig config = new WechatVirtualPaymentConfig();
        config.setOfferId("offer-id");
        config.setEnv(env);
        WechatVirtualPaymentConfigProvider provider = new WechatVirtualPaymentConfigProvider() {
            @Override
            public WechatVirtualPaymentConfig getConfig() {
                return config;
            }
        };
        WechatAccessTokenService tokenService = forceRefresh -> WechatAccessTokenResponse.builder()
                .accessToken("access-token")
                .expiresIn(7200)
                .build();
        return new DefaultWechatVirtualPaymentService(tokenService, provider, rewriteClient(server));
    }

    private HttpServer createServer(String path, Handler handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext(path, handler::handle);
        server.start();
        return server;
    }

    private OkHttpClient rewriteClient(HttpServer server) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    HttpUrl rewritten = new HttpUrl.Builder()
                            .scheme("http")
                            .host("localhost")
                            .port(server.getAddress().getPort())
                            .encodedPath(original.url().encodedPath())
                            .encodedQuery(original.url().encodedQuery())
                            .build();
                    return chain.proceed(original.newBuilder().url(rewritten).build());
                })
                .build();
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void writeResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    @FunctionalInterface
    private interface Handler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
