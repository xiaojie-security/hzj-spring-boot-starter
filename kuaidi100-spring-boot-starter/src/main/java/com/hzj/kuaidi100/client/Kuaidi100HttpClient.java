package com.hzj.kuaidi100.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzj.common.utils.JsonUtils;
import com.hzj.kuaidi100.core.common.Kuaidi100ApiException;
import com.hzj.kuaidi100.provider.kuaidi100.Kuaidi100StaticConfigProvider;
import com.hzj.kuaidi100.provider.kuaidi100.entity.Kuaidi100StaticConfig;
import com.hzj.kuaidi100.utils.Kuaidi100SignUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 快递100 HTTP 请求客户端。
 *
 * <p>该类只负责鉴权、表单编码和网络通信，业务接口的参数及响应解析由各业务服务负责。</p>
 */
@Slf4j
public class Kuaidi100HttpClient {

    /** 快递100静态配置提供者。 */
    private final Kuaidi100StaticConfigProvider staticConfigProvider;

    /** OkHttp客户端。 */
    private final OkHttpClient httpClient;

    /**
     * 使用默认 HTTP 客户端创建快递100客户端。
     *
     * @param staticConfigProvider 静态授权配置提供者
     */
    public Kuaidi100HttpClient(Kuaidi100StaticConfigProvider staticConfigProvider) {
        this(staticConfigProvider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建快递100客户端。
     *
     * @param staticConfigProvider 静态授权配置提供者
     * @param httpClient OkHttp客户端
     */
    public Kuaidi100HttpClient(Kuaidi100StaticConfigProvider staticConfigProvider,
                               OkHttpClient httpClient) {
        if (staticConfigProvider == null) {
            throw new IllegalArgumentException("Kuaidi100StaticConfigProvider 不能为空");
        }
        if (httpClient == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        this.staticConfigProvider = staticConfigProvider;
        this.httpClient = httpClient;
    }

    /**
     * 调用实时查询或地图轨迹接口。
     *
     * @param endpoint 接口地址
     * @param parameters param主体
     * @param signType 签名类型
     * @return JSON响应
     */
    public JsonNode postCustomer(String endpoint, Map<String, Object> parameters, String signType) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.postCustomer");
        String param = writeJson(parameters);
        Map<String, String> form = new LinkedHashMap<>();
        form.put("customer", config.getCustomer());
        form.put("sign", Kuaidi100SignUtils.sign(param + config.getKey() + config.getCustomer(), signType));
        form.put("signType", isBlank(signType) ? "MD5" : signType);
        form.put("param", param);
        putUserId(form, config);
        return postJson(endpoint, form, "Kuaidi100HttpClient.postCustomer");
    }

    /**
     * 调用快递100订阅接口。
     *
     * <p>订阅接口将 key 放在 param 主体内，不能复用实时查询接口的 customer/sign 表单。</p>
     *
     * @param endpoint 接口地址
     * @param parameters param主体
     * @return JSON响应
     */
    public JsonNode postSubscription(String endpoint, Map<String, Object> parameters) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("schema", "json");
        form.put("param", writeJson(parameters));
        return postJson(endpoint, form, "Kuaidi100HttpClient.postSubscription");
    }

    /**
     * 获取当前静态授权 key。
     *
     * @return 授权key
     */
    public String getKey() {
        return getConfig("Kuaidi100HttpClient.getKey").getKey();
    }

    /**
     * 调用企业签名接口。
     *
     * @param endpoint 接口地址
     * @param method 业务类型，可为空
     * @param parameters param主体
     * @param includeTimestamp 是否参与时间戳签名并提交 t 参数
     * @return JSON响应
     */
    public JsonNode postEnterprise(String endpoint, String method, Map<String, Object> parameters,
                                   boolean includeTimestamp) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.postEnterprise");
        String param = writeJson(parameters);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signContent = param + (includeTimestamp ? timestamp : "")
                + config.getKey() + config.getSecret();
        Map<String, String> form = new LinkedHashMap<>();
        if (!isBlank(method)) {
            form.put("method", method);
        }
        form.put("key", config.getKey());
        if (includeTimestamp) {
            form.put("t", timestamp);
        }
        form.put("sign", Kuaidi100SignUtils.md5(signContent));
        form.put("param", param);
        putUserId(form, config);
        return postJson(endpoint, form, "Kuaidi100HttpClient.postEnterprise");
    }

    /**
     * 调用发货单云打印接口。
     *
     * <p>该接口的 settings 与 param 是两个独立的表单字段，不能将 settings 嵌入 param。</p>
     *
     * @param endpoint 接口地址
     * @param method 业务类型
     * @param parameters param主体
     * @param settings 打印纸张及模板设置
     * @return JSON响应
     */
    public JsonNode postBillPrint(String endpoint, String method, Map<String, Object> parameters,
                                  Map<String, Object> settings) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.postBillPrint");
        String param = writeJson(parameters);
        String timestamp = String.valueOf(System.currentTimeMillis());
        Map<String, String> form = new LinkedHashMap<>();
        form.put("method", isBlank(method) ? "billparcels" : method);
        form.put("key", config.getKey());
        form.put("t", timestamp);
        form.put("sign", Kuaidi100SignUtils.md5(param + timestamp + config.getKey() + config.getSecret()));
        form.put("param", param);
        form.put("settings", writeJson(settings));
        putUserId(form, config);
        return postJson(endpoint, form, "Kuaidi100HttpClient.postBillPrint");
    }

    /**
     * 调用仅使用 key 和 param 的接口，例如面单 OCR。
     *
     * @param endpoint 接口地址
     * @param parameters param主体
     * @return JSON响应
     */
    public JsonNode postKeyOnly(String endpoint, Map<String, Object> parameters) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.postKeyOnly");
        Map<String, String> form = new LinkedHashMap<>();
        form.put("key", config.getKey());
        form.put("param", writeJson(parameters));
        return postJson(endpoint, form, "Kuaidi100HttpClient.postKeyOnly");
    }

    /**
     * 调用快递100 multipart 文件接口。
     *
     * @param endpoint 接口地址
     * @param file 上传文件，可为空
     * @param parameters param主体
     * @return JSON响应
     */
    public JsonNode postMultipart(String endpoint, File file, Map<String, Object> parameters) {
        return postMultipart(endpoint, null, file, parameters);
    }

    /**
     * 调用带业务类型的快递100 multipart 文件接口。
     *
     * @param endpoint 接口地址
     * @param method 业务类型，可为空
     * @param file 上传文件，可为空
     * @param parameters param主体
     * @return JSON响应
     */
    public JsonNode postMultipart(String endpoint, String method, File file,
                                  Map<String, Object> parameters) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.postMultipart");
        String param = writeJson(parameters);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String sign = Kuaidi100SignUtils.md5(param + timestamp + config.getKey() + config.getSecret());
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("key", config.getKey())
                .addFormDataPart("t", timestamp)
                .addFormDataPart("sign", sign)
                .addFormDataPart("param", param);
        if (!isBlank(method)) {
            bodyBuilder.addFormDataPart("method", method);
        }
        if (file != null) {
            if (!file.isFile() || !file.canRead()) {
                log.error("Kuaidi100HttpClient.postMultipart 上传文件不可读, file={}", file);
                throw new Kuaidi100ApiException("快递100上传文件不可读", null, null);
            }
            bodyBuilder.addFormDataPart("file", file.getName(),
                    RequestBody.create(file, MediaType.parse("application/octet-stream")));
        }
        Request request = new Request.Builder()
                .url(endpoint)
                .post(bodyBuilder.build())
                .build();
        return execute(request, "Kuaidi100HttpClient.postMultipart");
    }

    /**
     * 调用智能单号识别接口。
     *
     * @param endpoint 接口地址
     * @param number 快递单号
     * @return JSON响应
     */
    public JsonNode getNumberRecognition(String endpoint, String number) {
        Kuaidi100StaticConfig config = getConfig("Kuaidi100HttpClient.getNumberRecognition");
        HttpUrl baseUrl = HttpUrl.parse(endpoint);
        if (baseUrl == null) {
            log.error("Kuaidi100HttpClient.getNumberRecognition 接口地址非法, endpoint={}", endpoint);
            throw new Kuaidi100ApiException("快递100接口地址非法", null, null);
        }
        String url = baseUrl.newBuilder()
                .addQueryParameter("num", number)
                .addQueryParameter("key", config.getKey())
                .build()
                .toString();
        return getJson(url, "Kuaidi100HttpClient.getNumberRecognition");
    }

    /**
     * 序列化 param 参数。
     *
     * @param parameters 参数主体
     * @return JSON字符串
     */
    public String writeJson(Object parameters) {
        try {
            return JsonUtils.writeAsString(parameters == null ? new LinkedHashMap<>() : parameters);
        } catch (RuntimeException exception) {
            log.error("Kuaidi100HttpClient.writeJson 序列化请求参数失败, parameters={}", parameters, exception);
            throw new Kuaidi100ApiException("快递100请求参数序列化失败", null, null);
        }
    }

    /**
     * 解析 JSON 文本。
     *
     * @param json JSON文本
     * @param action 调用动作
     * @return JSON节点
     */
    public JsonNode readJson(String json, String action) {
        try {
            JsonNode node = JsonUtils.getObjectMapper().readTree(json);
            if (node == null) {
                throw new IOException("响应为空");
            }
            return node;
        } catch (IOException | RuntimeException exception) {
            log.error("{} 解析快递100响应失败, responseBody={}", action, abbreviate(json), exception);
            throw new Kuaidi100ApiException("解析快递100响应失败", null, json);
        }
    }

    private JsonNode postJson(String endpoint, Map<String, String> form, String action) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        form.forEach(formBuilder::add);
        Request request = new Request.Builder()
                .url(endpoint)
                .post(formBuilder.build())
                .build();
        return execute(request, action);
    }

    private JsonNode getJson(String endpoint, String action) {
        Request request = new Request.Builder()
                .url(endpoint)
                .get()
                .build();
        return execute(request, action);
    }

    private JsonNode execute(Request request, String action) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} HTTP请求失败, status={}, uri={}, responseBody={}", action,
                        response.code(), request.url(), abbreviate(responseBody));
                throw new Kuaidi100ApiException("快递100 HTTP请求失败", response.code(), responseBody);
            }
            return readJson(responseBody, action);
        } catch (Kuaidi100ApiException exception) {
            throw exception;
        } catch (IOException exception) {
            log.error("{} 调用快递100接口异常, uri={}", action, request.url(), exception);
            throw new Kuaidi100ApiException("调用快递100接口异常", null, null);
        }
    }

    private Kuaidi100StaticConfig getConfig(String action) {
        Kuaidi100StaticConfig config = staticConfigProvider.getConfig();
        if (config == null || isBlank(config.getKey())) {
            log.error("{} 未获取到快递100静态授权配置", action);
            throw new Kuaidi100ApiException("未获取到快递100静态授权配置", null, null);
        }
        if (isBlank(config.getCustomer()) && "Kuaidi100HttpClient.postCustomer".equals(action)) {
            log.error("{} 快递100 customer 未配置", action);
            throw new Kuaidi100ApiException("快递100 customer 未配置", null, null);
        }
        if (isBlank(config.getSecret()) && ("Kuaidi100HttpClient.postEnterprise".equals(action)
                || "Kuaidi100HttpClient.postBillPrint".equals(action)
                || "Kuaidi100HttpClient.postMultipart".equals(action))) {
            log.error("{} 快递100 secret 未配置", action);
            throw new Kuaidi100ApiException("快递100 secret 未配置", null, null);
        }
        return config;
    }

    private void putUserId(Map<String, String> form, Kuaidi100StaticConfig config) {
        if (!isBlank(config.getUserId())) {
            form.put("userid", config.getUserId());
        }
    }

    private String abbreviate(String value) {
        return value == null || value.length() <= 1024 ? value : value.substring(0, 1024);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
