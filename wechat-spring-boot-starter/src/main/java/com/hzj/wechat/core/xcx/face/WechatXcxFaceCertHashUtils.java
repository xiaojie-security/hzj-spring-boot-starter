package com.hzj.wechat.core.xcx.face;

import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceCertInfo;
import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceCertType;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 微信人脸核身证件信息摘要（cert_hash）计算工具。
 *
 * <p>查询人脸核身结果（queryVerifyInfo）时需要根据 getVerifyId 传入的证件信息生成 cert_hash，
 * 计算规则如下：</p>
 * <ol>
 *     <li>对 cert_info 中的 cert_type、cert_name、cert_no 字段内容做标准 base64
 *     （若存在中文等 Unicode 字符，需先进行 UTF-8 编码）；</li>
 *     <li>按顺序拼成 {@code cert_type=xxx&cert_name=xxx&cert_no=xxx}；</li>
 *     <li>对拼接串做 SHA256，输出十六进制小写结果。</li>
 * </ol>
 *
 * <p>官方示例：cert_info 为
 * {@code {"cert_type":"IDENTITY_CARD","cert_name":"张三","cert_no":"310101199801011234"}} 时，
 * 拼接串为
 * {@code cert_type=SURFTlRJVFlfQ0FSRA==&cert_name=5byg5LiJ&cert_no=MzEwMTAxMTk5ODAxMDExMjM0}，
 * cert_hash 为 {@code 3c241f7ff324977aeb91f173bb2a7b06569e6fd784d5573db34a636d8671108b}。</p>
 */
public final class WechatXcxFaceCertHashUtils {

    /**
     * 拼接模板。
     */
    private static final String TEMPLATE = "cert_type=%s&cert_name=%s&cert_no=%s";

    private WechatXcxFaceCertHashUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 根据证件信息计算 cert_hash。
     *
     * @param certInfo 证件信息
     * @return cert_hash，十六进制小写字符串
     */
    public static String build(WechatXcxFaceCertInfo certInfo) {
        if (certInfo == null) {
            throw new IllegalArgumentException("certInfo 不能为空");
        }
        WechatXcxFaceCertType certType = certInfo.certType;
        return build(certType == null ? null : certType.getValue(), certInfo.certName, certInfo.certNo);
    }

    /**
     * 根据证件类型、姓名与号码计算 cert_hash。
     *
     * @param certType 证件类型
     * @param certName 证件姓名
     * @param certNo   证件号码
     * @return cert_hash，十六进制小写字符串
     */
    public static String build(WechatXcxFaceCertType certType, String certName, String certNo) {
        return build(certType == null ? null : certType.getValue(), certName, certNo);
    }

    /**
     * 根据证件类型、姓名与号码计算 cert_hash。
     *
     * @param certType 证件类型字符串，如 IDENTITY_CARD
     * @param certName 证件姓名
     * @param certNo   证件号码
     * @return cert_hash，十六进制小写字符串
     */
    public static String build(String certType, String certName, String certNo) {
        if (certType == null || certType.isEmpty()) {
            throw new IllegalArgumentException("certType 不能为空");
        }
        if (certName == null || certName.isEmpty()) {
            throw new IllegalArgumentException("certName 不能为空");
        }
        if (certNo == null || certNo.isEmpty()) {
            throw new IllegalArgumentException("certNo 不能为空");
        }
        String joined = String.format(TEMPLATE, base64(certType), base64(certName), base64(certNo));
        return sha256Hex(joined);
    }

    private static String base64(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16));
                hex.append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 JVM 不支持 SHA-256 算法", e);
        }
    }
}
