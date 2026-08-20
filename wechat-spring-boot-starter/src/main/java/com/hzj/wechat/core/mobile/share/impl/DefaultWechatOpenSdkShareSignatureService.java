package com.hzj.wechat.core.mobile.share.impl;

import com.hzj.wechat.core.mobile.share.WechatOpenSdkShareSignatureException;
import com.hzj.wechat.core.mobile.share.WechatOpenSdkShareSignatureService;
import com.hzj.wechat.core.mobile.share.domain.WechatOpenSdkShareSignatureRequest;
import com.hzj.wechat.core.mobile.share.domain.WechatOpenSdkShareSignatureResponse;
import com.hzj.wechat.core.mobile.share.enums.WechatOpenSdkSignatureAlgorithm;
import com.hzj.wechat.provider.wechat.mobile.share.WechatOpenSdkShareConfigProvider;
import com.hzj.wechat.provider.wechat.mobile.share.entity.WechatOpenSdkShareConfig;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 微信 OpenSDK 分享签名默认实现。
 */
@Slf4j
public class DefaultWechatOpenSdkShareSignatureService implements WechatOpenSdkShareSignatureService {

    /** RSA-PSS 固定使用 SHA-256、MGF1 SHA-256 和 32 字节 salt。 */
    private static final PSSParameterSpec RSA_PSS_PARAMETER_SPEC = new PSSParameterSpec(
            "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, PSSParameterSpec.TRAILER_FIELD_BC);

    /** OpenSDK 分享动态配置提供者。 */
    private final WechatOpenSdkShareConfigProvider configProvider;

    /**
     * 创建微信 OpenSDK 分享签名服务。
     *
     * @param configProvider OpenSDK 分享动态配置提供者
     */
    public DefaultWechatOpenSdkShareSignatureService(WechatOpenSdkShareConfigProvider configProvider) {
        this.configProvider = Objects.requireNonNull(configProvider, "WechatOpenSdkShareConfigProvider 不能为空");
    }

    /**
     * 根据微信官方字段规则生成 OpenSDK 分享签名。
     *
     * @param request 分享签名请求
     * @return 分享签名响应
     */
    @Override
    public WechatOpenSdkShareSignatureResponse sign(WechatOpenSdkShareSignatureRequest request) {
        validateRequest(request);
        WechatOpenSdkShareConfig config = requireConfig();
        String signContent = buildSignContent(request, config.getAppid());
        String msgSignature = sign(signContent, config.getSignatureAlgorithm(), config.getPrivateKey());
        return new WechatOpenSdkShareSignatureResponse(
                config.getAppid(), msgSignature, config.getSignatureAlgorithm());
    }

    /**
     * 计算二进制内容的 SHA-256 小写十六进制值。
     *
     * @param content 二进制内容
     * @return SHA-256 小写十六进制值
     */
    @Override
    public String calculateSha256(byte[] content) {
        if (content == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.calculateSha256 二进制内容不能为空");
            throw new WechatOpenSdkShareSignatureException("二进制内容不能为空");
        }
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(content);
            StringBuilder value = new StringBuilder(hash.length * 2);
            for (byte item : hash) {
                value.append(String.format("%02x", item));
            }
            return value.toString();
        } catch (NoSuchAlgorithmException exception) {
            log.error("DefaultWechatOpenSdkShareSignatureService.calculateSha256 当前Java环境不支持SHA-256", exception);
            throw new WechatOpenSdkShareSignatureException("当前Java环境不支持 SHA-256", exception);
        }
    }

    /**
     * 获取并校验一次动态配置快照。
     *
     * @return OpenSDK 分享配置
     */
    private WechatOpenSdkShareConfig requireConfig() {
        WechatOpenSdkShareConfig config = configProvider.getConfig();
        if (config == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requireConfig 未获取到微信移动应用OpenSDK配置");
            throw new WechatOpenSdkShareSignatureException("未获取到微信移动应用 OpenSDK 配置");
        }
        if (isBlank(config.getAppid())) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requireConfig 微信移动应用AppID不能为空");
            throw new WechatOpenSdkShareSignatureException("微信移动应用 AppID 不能为空");
        }
        if (config.getSignatureAlgorithm() == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requireConfig OpenSDK分享签名算法不能为空");
            throw new WechatOpenSdkShareSignatureException("OpenSDK 分享签名算法不能为空");
        }
        if (config.getPrivateKey() == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requireConfig OpenSDK分享私钥不能为空");
            throw new WechatOpenSdkShareSignatureException("OpenSDK 分享私钥不能为空");
        }
        return config;
    }

    /**
     * 构建官方要求的字段字典序 Base64 签名串。
     *
     * @param request 分享签名请求
     * @param appid 微信开放平台应用 ID
     * @return 待签名内容
     */
    private String buildSignContent(WechatOpenSdkShareSignatureRequest request, String appid) {
        Map<String, String> fields = new TreeMap<>();
        fields.put("appid", appid);
        switch (request.getMessageType()) {
            case TEXT -> fields.put("text", required(request.getText(), "text"));
            case IMAGE -> fields.put("imgDataHash", requiredHash(request.getImgDataHash(), "imgDataHash"));
            case VIDEO -> {
                fields.put("description", value(request.getDescription()));
                fields.put("thumbDataHash", hashValue(request.getThumbDataHash(), "thumbDataHash"));
                fields.put("title", value(request.getTitle()));
                fields.put("videoLowBandUrl", value(request.getVideoLowBandUrl()));
                fields.put("videoUrl", requiredOneOf(request.getVideoUrl(), request.getVideoLowBandUrl(), "videoUrl", "videoLowBandUrl"));
            }
            case WEBPAGE -> {
                fields.put("description", value(request.getDescription()));
                fields.put("thumbDataHash", hashValue(request.getThumbDataHash(), "thumbDataHash"));
                fields.put("title", value(request.getTitle()));
                fields.put("webpageUrl", required(request.getWebpageUrl(), "webpageUrl"));
            }
            case MINI_PROGRAM -> {
                fields.put("description", value(request.getDescription()));
                fields.put("path", required(request.getPath(), "path"));
                fields.put("thumbDataHash", hashValue(request.getThumbDataHash(), "thumbDataHash"));
                fields.put("title", value(request.getTitle()));
                fields.put("userName", required(request.getUserName(), "userName"));
            }
            case MUSIC_VIDEO -> {
                fields.put("albumName", value(request.getAlbumName()));
                fields.put("description", value(request.getDescription()));
                fields.put("duration", required(request.getDuration(), "duration").toString());
                fields.put("hdAlbumThumbFileHash", hashValue(
                        request.getHdAlbumThumbFileHash(), "hdAlbumThumbFileHash"));
                fields.put("identification", value(request.getIdentification()));
                fields.put("issueDate", request.getIssueDate() == null ? "" : request.getIssueDate().toString());
                fields.put("musicDataUrl", required(request.getMusicDataUrl(), "musicDataUrl"));
                fields.put("musicGenre", value(request.getMusicGenre()));
                fields.put("musicUrl", required(request.getMusicUrl(), "musicUrl"));
                fields.put("singerName", required(request.getSingerName(), "singerName"));
                fields.put("thumbDataHash", hashValue(request.getThumbDataHash(), "thumbDataHash"));
                fields.put("title", value(request.getTitle()));
            }
        }
        return fields.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + Base64.getEncoder()
                        .encodeToString(entry.getValue().getBytes(StandardCharsets.UTF_8)))
                .collect(Collectors.joining("&"));
    }

    /**
     * 使用配置算法对待签名内容签名。
     *
     * @param content 待签名内容
     * @param signatureAlgorithm 签名算法
     * @param privateKey 私钥
     * @return Base64 编码的签名结果
     */
    private String sign(String content, WechatOpenSdkSignatureAlgorithm signatureAlgorithm,
                        PrivateKey privateKey) {
        try {
            Signature signature = switch (signatureAlgorithm) {
                case RSA_WITH_SHA256 -> Signature.getInstance("RSASSA-PSS");
                case SM2_WITH_SM3 -> getSm2Signature();
            };
            if (signatureAlgorithm == WechatOpenSdkSignatureAlgorithm.RSA_WITH_SHA256) {
                signature.setParameter(RSA_PSS_PARAMETER_SPEC);
            }
            signature.initSign(privateKey, new SecureRandom());
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (NoSuchAlgorithmException | NoSuchProviderException exception) {
            log.error("DefaultWechatOpenSdkShareSignatureService.sign 当前Java环境不支持OpenSDK分享签名算法, signatureAlgorithm={}",
                    signatureAlgorithm, exception);
            throw new WechatOpenSdkShareSignatureException("当前Java环境不支持 OpenSDK 分享签名算法：" + signatureAlgorithm,
                    exception);
        } catch (InvalidAlgorithmParameterException exception) {
            log.error("DefaultWechatOpenSdkShareSignatureService.sign 设置RSA-PSS参数失败", exception);
            throw new WechatOpenSdkShareSignatureException("设置 OpenSDK RSA-PSS 签名参数失败", exception);
        } catch (InvalidKeyException exception) {
            log.error("DefaultWechatOpenSdkShareSignatureService.sign OpenSDK分享私钥不可用, signatureAlgorithm={}",
                    signatureAlgorithm, exception);
            throw new WechatOpenSdkShareSignatureException("OpenSDK 分享私钥不可用", exception);
        } catch (SignatureException exception) {
            log.error("DefaultWechatOpenSdkShareSignatureService.sign 生成OpenSDK分享签名失败, signatureAlgorithm={}",
                    signatureAlgorithm, exception);
            throw new WechatOpenSdkShareSignatureException("生成 OpenSDK 分享签名失败", exception);
        }
    }

    /**
     * 获取 SM2 签名器并保证 Bouncy Castle Provider 已注册。
     *
     * @return SM2 签名器
     * @throws NoSuchAlgorithmException 当前环境不支持 SM2 时抛出
     * @throws NoSuchProviderException Bouncy Castle Provider 不可用时抛出
     */
    private Signature getSm2Signature() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        return Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
    }

    /**
     * 校验请求对象本身。
     *
     * @param request 分享签名请求
     */
    private void validateRequest(WechatOpenSdkShareSignatureRequest request) {
        if (request == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.validateRequest 分享签名请求不能为空");
            throw new WechatOpenSdkShareSignatureException("分享签名请求不能为空");
        }
        if (request.getMessageType() == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.validateRequest 分享消息类型不能为空");
            throw new WechatOpenSdkShareSignatureException("分享消息类型不能为空");
        }
    }

    /**
     * 获取可选字段值，空字段按官方签名规则使用空字符串参与签名。
     *
     * @param fieldValue 字段值
     * @return 非空字段值
     */
    private String value(String fieldValue) {
        return fieldValue == null ? "" : fieldValue;
    }

    /**
     * 校验必填字符串字段。
     *
     * @param fieldValue 字段值
     * @param fieldName 字段名称
     * @return 字段值
     */
    private String required(String fieldValue, String fieldName) {
        if (isBlank(fieldValue)) {
            log.error("DefaultWechatOpenSdkShareSignatureService.required 分享签名字段不能为空, fieldName={}", fieldName);
            throw new WechatOpenSdkShareSignatureException("分享签名字段不能为空：" + fieldName);
        }
        return fieldValue;
    }

    /**
     * 校验必填数字字段。
     *
     * @param fieldValue 字段值
     * @param fieldName 字段名称
     * @return 字段值
     */
    private Long required(Long fieldValue, String fieldName) {
        if (fieldValue == null) {
            log.error("DefaultWechatOpenSdkShareSignatureService.required 分享签名字段不能为空, fieldName={}", fieldName);
            throw new WechatOpenSdkShareSignatureException("分享签名字段不能为空：" + fieldName);
        }
        return fieldValue;
    }

    /**
     * 校验必填 SHA-256 小写十六进制哈希字段。
     *
     * @param fieldValue 哈希值
     * @param fieldName 字段名称
     * @return 哈希值
     */
    private String requiredHash(String fieldValue, String fieldName) {
        String value = required(fieldValue, fieldName);
        if (!value.matches("[0-9a-f]{64}")) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requiredHash 分享签名哈希字段格式不正确, fieldName={}", fieldName);
            throw new WechatOpenSdkShareSignatureException("分享签名哈希字段必须是64位小写SHA-256十六进制值：" + fieldName);
        }
        return value;
    }

    /**
     * 获取可选 SHA-256 小写十六进制哈希字段值。
     *
     * @param fieldValue 哈希值
     * @param fieldName 字段名称
     * @return 空字符串或合法哈希值
     */
    private String hashValue(String fieldValue, String fieldName) {
        return isBlank(fieldValue) ? "" : requiredHash(fieldValue, fieldName);
    }

    /**
     * 校验两个备选字段至少存在一个。
     *
     * @param preferredValue 首选字段值
     * @param alternativeValue 备选字段值
     * @param preferredFieldName 首选字段名称
     * @param alternativeFieldName 备选字段名称
     * @return 首选字段值或空字符串
     */
    private String requiredOneOf(String preferredValue, String alternativeValue,
                                 String preferredFieldName, String alternativeFieldName) {
        if (isBlank(preferredValue) && isBlank(alternativeValue)) {
            log.error("DefaultWechatOpenSdkShareSignatureService.requiredOneOf 分享签名字段不能同时为空, fields={},{}",
                    preferredFieldName, alternativeFieldName);
            throw new WechatOpenSdkShareSignatureException(
                    "分享签名字段不能同时为空：" + preferredFieldName + "、" + alternativeFieldName);
        }
        return value(preferredValue);
    }

    /**
     * 判断字符串是否为空白。
     *
     * @param value 待判断字符串
     * @return 是否为空白
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
