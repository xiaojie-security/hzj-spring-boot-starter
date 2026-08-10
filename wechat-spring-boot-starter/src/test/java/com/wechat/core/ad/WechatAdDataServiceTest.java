package com.wechat.core.ad;

import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.domain.WechatAccessTokenResponse;
import com.hzj.wechat.core.ad.WechatAdDataException;
import com.hzj.wechat.core.ad.domain.WechatAdDataItem;
import com.hzj.wechat.core.ad.domain.WechatAdDataRequest;
import com.hzj.wechat.core.ad.domain.WechatAdDataResponse;
import com.hzj.wechat.core.ad.domain.WechatAdDataDetailResponse;
import com.hzj.wechat.core.ad.domain.WechatAdUnitListResponse;
import com.hzj.wechat.core.ad.domain.WechatAdSettlementResponse;
import com.hzj.wechat.core.ad.impl.DefaultWechatAdDataService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 微信小程序广告汇总数据服务合约测试。
 */
class WechatAdDataServiceTest {

    @Test
    void shouldRequestPublisherAdPositionSummaryAndMapSnakeCaseFields() throws Exception {
        HttpServer server = createServer("/publisher/stat", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            assertTrue(query != null && query.contains("action=publisher_adpos_general"));
            assertTrue(query.contains("access_token=stable-token"));
            assertTrue(query.contains("page=2"));
            assertTrue(query.contains("page_size=20"));
            assertTrue(query.contains("start_date=2026-07-01"));
            assertTrue(query.contains("end_date=2026-07-30"));
            assertTrue(query.contains("ad_slot=SLOT_ID_WEAPP_BANNER"));
            writeResponse(exchange, 200, "{\"base_resp\":{\"err_msg\":\"ok\",\"ret\":0},"
                    + "\"list\":[{\"slot_id\":123,\"ad_slot\":\"SLOT_ID_WEAPP_BANNER\","
                    + "\"date\":\"2026-07-30\",\"req_succ_count\":100,"
                    + "\"exposure_count\":80,\"exposure_rate\":0.8,\"click_count\":10,"
                    + "\"click_rate\":0.125,\"income\":200,\"ecpm\":2.5}],"
                    + "\"summary\":{\"req_succ_count\":100,\"exposure_count\":80,"
                    + "\"exposure_rate\":0.8,\"click_count\":10,\"click_rate\":0.125,"
                    + "\"income\":200,\"ecpm\":2.5},\"total_num\":1}");
        });
        try {
            DefaultWechatAdDataService service = new DefaultWechatAdDataService(tokenService(), rewriteClient(server));

            WechatAdDataResponse response = service.getAdDataSummary(WechatAdDataRequest.builder()
                    .page(2)
                    .pageSize(20)
                    .startDate("2026-07-01")
                    .endDate("2026-07-30")
                    .adSlot("SLOT_ID_WEAPP_BANNER")
                    .build());

            WechatAdDataItem item = response.getList().get(0);
            assertEquals(0, response.getBaseResp().getRet());
            assertEquals(1, response.getTotalNum());
            assertEquals(123L, item.getSlotId());
            assertEquals(100L, item.getReqSuccCount());
            assertEquals(80L, item.getExposureCount());
            assertEquals(0.8D, item.getExposureRate());
            assertEquals(200L, response.getSummary().getIncome());
        } finally {
            server.stop(0);
        }
    }

    @Test
    void shouldRejectDateRangeLongerThanNinetyDays() {
        DefaultWechatAdDataService service = new DefaultWechatAdDataService(tokenService());

        assertThrows(WechatAdDataException.class, () -> service.getAdDataSummary(WechatAdDataRequest.builder()
                .startDate("2026-01-01")
                .endDate("2026-04-02")
                .build()));
    }

    @Test
    void shouldSupportDetailAdUnitListAndSettlementActions() throws Exception {
        HttpServer server = createServer("/publisher/stat", exchange -> {
            String query = exchange.getRequestURI().getQuery();
            if (query.contains("action=publisher_adunit_general")) {
                assertTrue(query.contains("ad_unit_id=adunit-test"));
                writeResponse(exchange, 200, "{\"base_resp\":{\"err_msg\":\"ok\",\"ret\":0},"
                        + "\"list\":[{\"ad_unit_id\":\"adunit-test\",\"ad_unit_name\":\"Reward\","
                        + "\"stat_item\":{\"ad_slot\":\"SLOT_ID_WEAPP_REWARD_VIDEO\","
                        + "\"date\":\"2026-07-30\",\"income\":123}}],\"total_num\":1}");
            } else if (query.contains("action=get_adunit_list")) {
                writeResponse(exchange, 200, "{\"base_resp\":{\"err_msg\":\"ok\",\"ret\":0},"
                        + "\"ad_unit\":[{\"ad_slot\":\"SLOT_ID_WEAPP_BANNER\","
                        + "\"ad_unit_id\":\"adunit-test\",\"ad_unit_name\":\"Banner\","
                        + "\"ad_unit_status\":\"AD_UNIT_STATUS_ON\"}],\"total_num\":1}");
            } else {
                assertTrue(query.contains("action=publisher_settlement"));
                assertTrue(query.contains("start_date=2026-07-01"));
                writeResponse(exchange, 200, "{\"base_resp\":{\"err_msg\":\"ok\",\"ret\":0},"
                        + "\"body\":\"主体\",\"revenue_all\":1000,\"penalty_all\":20,"
                        + "\"settled_revenue_all\":800,\"settlement_list\":[],\"total_num\":0}");
            }
        });
        try {
            DefaultWechatAdDataService service = new DefaultWechatAdDataService(tokenService(), rewriteClient(server));

            WechatAdDataDetailResponse detail = service.getAdDataDetail(WechatAdDataRequest.builder()
                    .startDate("2026-07-01")
                    .endDate("2026-07-30")
                    .adUnitId("adunit-test")
                    .build());
            assertEquals("adunit-test", detail.getList().get(0).getAdUnitId());
            assertEquals(123L, detail.getList().get(0).getStatItem().getIncome());

            WechatAdUnitListResponse unitList = service.getAdUnitList(WechatAdDataRequest.builder()
                    .adSlot("SLOT_ID_WEAPP_BANNER")
                    .build());
            assertEquals("AD_UNIT_STATUS_ON", unitList.getAdUnit().get(0).getAdUnitStatus());

            WechatAdSettlementResponse settlement = service.getSettlementData(WechatAdDataRequest.builder()
                    .startDate("2026-07-01")
                    .endDate("2026-07-30")
                    .build());
            assertEquals("主体", settlement.getBody());
            assertEquals(1000L, settlement.getRevenueAll());
        } finally {
            server.stop(0);
        }
    }

    private WechatAccessTokenService tokenService() {
        return forceRefresh -> WechatAccessTokenResponse.builder()
                .accessToken("stable-token")
                .expiresIn(7200)
                .build();
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

    private void writeResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }

    @FunctionalInterface
    private interface Handler {
        void handle(HttpExchange exchange) throws IOException;
    }
}
