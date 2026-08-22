package com.hzj.amap.core.webapi.impl;

import cn.hutool.core.net.url.UrlBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzj.amap.core.enums.AMapHttpMethod;
import com.hzj.amap.core.webapi.AMapWebApiException;
import com.hzj.amap.core.webapi.AMapWebApiService;
import com.hzj.amap.core.webapi.adapter.AMapObjectMapperFactory;
import com.hzj.amap.core.webapi.domain.*;
import com.hzj.amap.provider.webapi.AMapWebApiConfigProvider;
import com.hzj.amap.provider.webapi.entity.WebApiConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * 高德 Web 服务 API 默认实现。
 */
@Slf4j
public class DefaultAMapWebApiService implements AMapWebApiService {

    /** 高德 Web 服务动态配置提供者。 */
    private final AMapWebApiConfigProvider provider;

    /** HTTP 调用客户端。 */
    private final OkHttpClient client;

    /** 高德响应解析器。 */
    private final ObjectMapper objectMapper;

    /**
     * 使用默认 HTTP 客户端创建高德 Web 服务 API。
     *
     * @param provider 高德 Web 服务动态配置提供者
     */
    public DefaultAMapWebApiService(AMapWebApiConfigProvider provider) {
        this(provider, new OkHttpClient.Builder().build(), AMapObjectMapperFactory.create());
    }

    /**
     * 创建高德 Web 服务 API。
     *
     * @param provider 高德 Web 服务动态配置提供者
     * @param client OkHttp 调用客户端
     */
    public DefaultAMapWebApiService(AMapWebApiConfigProvider provider, OkHttpClient client) {
        this(provider, client, AMapObjectMapperFactory.create());
    }

    /**
     * 创建高德 Web 服务 API，并复用应用的 ObjectMapper 配置。
     *
     * @param provider 高德 Web 服务动态配置提供者
     * @param client OkHttp 调用客户端
     * @param objectMapper 应用 ObjectMapper
     */
    public DefaultAMapWebApiService(AMapWebApiConfigProvider provider,
                                    OkHttpClient client,
                                    ObjectMapper objectMapper) {
        if (provider == null) {
            throw new IllegalArgumentException("AMapWebApiConfigProvider 不能为空");
        }
        if (client == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        if (objectMapper == null) {
            throw new IllegalArgumentException("ObjectMapper 不能为空");
        }
        this.provider = provider;
        this.client = client;
        this.objectMapper = AMapObjectMapperFactory.configure(objectMapper);
    }

    @Override
    public AMapGeoResponse queryGeo(AMapGeoRequest request) {
        return executeJson(request, AMapGeoResponse.class, "DefaultAMapWebApiService.queryGeo");
    }

    @Override
    public AMapReverseGeoResponse queryReverseGeo(AMapReverseGeoRequest request) {
        return executeJson(request, AMapReverseGeoResponse.class, "DefaultAMapWebApiService.queryReverseGeo");
    }

    @Override
    public AMapRouteResponse queryRoute(AMapRouteRequest request) {
        if (request == null) {
            log.error("DefaultAMapWebApiService.queryRoute 请求参数为空");
            throw new AMapWebApiException("高德接口请求参数不能为空", null, null);
        }
        request.setRequestUrl(request.resolveRequestUrl());
        return executeJson(request, AMapRouteResponse.class, "DefaultAMapWebApiService.queryRoute");
    }

    @Override
    public AMapDistrictResponse queryDistrict(AMapDistrictRequest request) {
        return executeJson(request, AMapDistrictResponse.class, "DefaultAMapWebApiService.queryDistrict");
    }

    @Override
    public AMapIpLocationResponse queryIpLocation(AMapIpLocationRequest request) {
        return executeJson(request, AMapIpLocationResponse.class, "DefaultAMapWebApiService.queryIpLocation");
    }

    @Override
    public AMapStaticMapResponse queryStaticMap(AMapStaticMapRequest request) {
        String action = "DefaultAMapWebApiService.queryStaticMap";
        String url = buildUrl(request, action);
        try (Response response = client.newCall(buildHttpRequest(url, request.getRequestMethod())).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() == null ? "" : response.body().string();
                log.error("{} 高德静态地图 HTTP 请求失败，status={}, url={}, responseBody={}", action,
                        response.code(), sanitizeUrl(url), abbreviate(responseBody));
                throw new AMapWebApiException("高德静态地图 HTTP 请求失败", response.code(), null);
            }
            return AMapStaticMapResponse.builder()
                    .httpStatus(response.code())
                    .contentType(response.body() == null || response.body().contentType() == null ? null
                            : response.body().contentType().toString())
                    .image(response.body() == null ? new byte[0] : response.body().bytes())
                    .build();
        } catch (AMapWebApiException e) {
            throw e;
        } catch (IOException e) {
            log.error("{} 调用高德静态地图接口异常，url={}", action, sanitizeUrl(url), e);
            throw new AMapWebApiException("调用高德静态地图接口异常", null, null);
        }
    }

    @Override
    public AMapCoordinateConvertResponse convertCoordinate(AMapCoordinateConvertRequest request) {
        return executeJson(request, AMapCoordinateConvertResponse.class, "DefaultAMapWebApiService.convertCoordinate");
    }

    @Override
    public AMapInputTipsResponse queryInputTips(AMapInputTipsRequest request) {
        return executeJson(request, AMapInputTipsResponse.class, "DefaultAMapWebApiService.queryInputTips");
    }

    @Override
    public AMapWeatherResponse queryWeather(AMapWeatherRequest request) {
        return executeJson(request, AMapWeatherResponse.class, "DefaultAMapWebApiService.queryWeather");
    }

    /**
     * 执行返回 JSON 的高德接口请求。
     *
     * @param request 高德接口请求
     * @param action 调用动作
     * @return 高德接口响应
     */
    private <T extends AMapWebApiResponse> T executeJson(AMapWebApiRequest request, Class<T> responseType, String action) {
        String url = buildUrl(request, action);
        try (Response response = client.newCall(buildHttpRequest(url, request.getRequestMethod())).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            JsonNode data = parseJson(responseBody, action, url);
            String status = getString(data, "status");
            String infoCode = getFirstNotBlank(data, "infocode", "errcode");
            String info = getFirstNotBlank(data, "info", "errmsg");
            if (!response.isSuccessful() || !isSuccess(data, status)) {
                log.error("{} 高德接口调用失败，httpStatus={}, status={}, infoCode={}, info={}, url={}",
                        action, response.code(), status, infoCode, info, sanitizeUrl(url));
                throw new AMapWebApiException("高德接口调用失败: " + info,
                        response.code(), infoCode);
            }
            T result = objectMapper.treeToValue(data, responseType);
            mapRouteData(data, result);
            result.setHttpStatus(response.code());
            result.setStatus(status);
            result.setInfoCode(infoCode);
            result.setInfo(info);
            return result;
        } catch (AMapWebApiException e) {
            log.error("{} 调用高德接口异常，url={}", action, sanitizeUrl(url), e);
            throw e;
        } catch (JsonProcessingException e) {
            log.error("{} 解析高德接口响应实体失败，url={}", action, sanitizeUrl(url), e);
            throw new AMapWebApiException("解析高德接口响应实体失败", null, null);
        } catch (IOException e) {
            log.error("{} 调用高德接口异常，url={}", action, sanitizeUrl(url), e);
            throw new AMapWebApiException("调用高德接口异常", null, null);
        }
    }

    /**
     * 构建携带动态 key 的请求地址。
     *
     * @param request 高德接口请求
     * @param action 调用动作
     * @return 请求地址
     */
    private String buildUrl(AMapWebApiRequest request, String action) {
        requireRequest(request, action);
        WebApiConfig config = provider.getConfig();
        if (config == null || isBlank(config.getSecretKey())) {
            log.error("{} 未获取到高德 Web 服务配置或 key", action);
            throw new AMapWebApiException("未获取到高德 Web 服务配置", null, null);
        }
        UrlBuilder builder = UrlBuilder.of(request.getRequestUrl());
        for (Map.Entry<String, String> entry : request.toQueryParameters().entrySet()) {
            builder.addQuery(entry.getKey(), entry.getValue());
        }
        builder.addQuery("key", config.getSecretKey());
        return builder.build();
    }

    /**
     * 校验请求参数。
     *
     * @param request 高德接口请求
     * @param action 调用动作
     */
    private void requireRequest(AMapWebApiRequest request, String action) {
        if (request == null || isBlank(request.getRequestUrl()) || request.getRequestMethod() == null) {
            log.error("{} 请求参数或请求路由为空", action);
            throw new AMapWebApiException("高德接口请求参数或请求路由不能为空", null, null);
        }
    }

    /**
     * 解析高德 JSON 响应。
     *
     * @param responseBody 响应内容
     * @param action 调用动作
     * @param url 请求地址
     * @return JSON 响应
     */
    private JsonNode parseJson(String responseBody, String action, String url) {
        try {
            JsonNode node = objectMapper.readTree(responseBody);
            if (node == null || !node.isObject()) {
                throw new IllegalArgumentException("高德接口响应不是 JSON 对象");
            }
            return node;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.error("{} 解析高德接口响应失败，url={}, responseBody={}", action, sanitizeUrl(url), abbreviate(responseBody), e);
            throw new AMapWebApiException("解析高德接口响应失败", null, null);
        }
    }

    /**
     * 获取 JSON 字符串属性。
     *
     * @param object JSON 对象
     * @param name 属性名
     * @return 属性值
     */
    private String getString(JsonNode object, String name) {
        JsonNode value = object.get(name);
        return value == null || value.isNull() ? "" : value.asText("");
    }

    /**
     * 获取第一个非空 JSON 字符串属性。
     *
     * @param object JSON 对象
     * @param names 属性名列表
     * @return 第一个非空属性值
     */
    private String getFirstNotBlank(JsonNode object, String... names) {
        for (String name : names) {
            String value = getString(object, name);
            if (!isBlank(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * 判断高德业务响应是否成功。
     *
     * @param data 高德响应 JSON 对象
     * @param status V3 接口业务状态
     * @return 是否成功
     */
    private boolean isSuccess(JsonNode data, String status) {
        if ("1".equals(status)) {
            return true;
        }
        return data.has("errcode") && !data.get("errcode").isNull()
                && "0".equals(data.get("errcode").asText());
    }

    /**
     * 映射路径规划 2.0 的 data 响应节点。
     *
     * @param data 高德响应 JSON 对象
     * @param response 自定义响应对象
     */
    private void mapRouteData(JsonNode data, AMapWebApiResponse response) {
        if (!(response instanceof AMapRouteResponse routeResponse)
                || routeResponse.getRoute() != null || !data.has("data") || data.get("data").isNull()) {
            return;
        }
        JsonNode routeData = data.get("data");
        if (!routeData.isObject()) {
            return;
        }
        if (routeData.has("route") && !routeData.get("route").isNull()) {
            routeData = routeData.get("route");
        }
        if (routeData.isObject()) {
            try {
                routeResponse.setRoute(objectMapper.treeToValue(routeData, AMapRouteResponse.AMapRoute.class));
            } catch (JsonProcessingException e) {
                throw new AMapWebApiException("解析高德路径规划结果失败", null, null);
            }
        }
    }

    /**
     * 构建 HTTP 请求。
     *
     * @param url 请求地址
     * @param requestMethod 高德请求方法
     * @return HTTP 请求
     */
    private Request buildHttpRequest(String url, AMapHttpMethod requestMethod) {
        Request.Builder builder = new Request.Builder().url(url);
        return switch (requestMethod) {
            case GET -> builder.get().build();
            case DELETE -> builder.delete().build();
            case POST, PUT, PATCH -> builder.method(requestMethod.name(), RequestBody.create(new byte[0])).build();
        };
    }

    /**
     * 判断字符串是否为空。
     *
     * @param value 字符串
     * @return 是否为空
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 截断过长响应文本。
     *
     * @param value 响应文本
     * @return 截断后文本
     */
    private String abbreviate(String value) {
        return value == null || value.length() <= 512 ? value : value.substring(0, 512);
    }

    /**
     * 隐藏 URL 中的高德密钥。
     *
     * @param url 请求地址
     * @return 隐藏密钥后的请求地址
     */
    private String sanitizeUrl(String url) {
        return url == null ? null : url.replaceAll("([?&]key=)[^&#]*", "$1***");
    }
}
