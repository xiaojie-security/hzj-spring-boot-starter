package com.hzj.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * PEM密钥解析器
 * 支持：RSA PKCS‑8私钥、RSA X509公钥
 * 配合 ConfigStringLoader，支持 classpath:/ file:/ 本地绝对路径、直接传入PEM文本
 */
@Slf4j
public final class PemKeyParser {

    private static final String RSA_ALGORITHM = "RSA";

    private PemKeyParser() {
    }

    /**
     * 加载并解析RSA PEM私钥（PKCS‑8）
     * @param source 配置源（文件路径/classpath/pem直接文本）
     * @return PrivateKey
     */
    public static PrivateKey loadRsaPrivateKey(String source) {
        String pemRawText = ConfigStringLoader.load(source);
        String rawBase64 = cleanPemToRawBase64(pemRawText);
        return parsePkcs8PrivateKey(rawBase64);
    }

    /**
     * 加载并解析RSA PEM公钥（X509）
     * @param source 配置源
     * @return PublicKey
     */
    public static PublicKey loadRsaPublicKey(String source) {
        String pemRawText = ConfigStringLoader.load(source);
        String rawBase64 = cleanPemToRawBase64(pemRawText);
        return parseX509PublicKey(rawBase64);
    }

    /**
     * 读取PEM来源，清洗后得到【裸Base64密钥字符串】，可直接传入 CryptoUtil 的rsaEncrypt/rsaDecrypt
     * @param source 配置源
     * @return 去掉头尾标记、换行、空白的裸base64
     */
    public static String loadPemToRawBase64(String source) {
        String pemRawText = ConfigStringLoader.load(source);
        return cleanPemToRawBase64(pemRawText);
    }


    /**
     * 清洗PEM字符串，移除标记行、换行、空格、制表符
     * @param pemContent 完整PEM文本（包含-----BEGIN xxx----- / -----END xxx-----）
     * @return 裸base64字符串
     */
    public static String cleanPemToRawBase64(String pemContent) {
        if (StrUtil.isBlank(pemContent)) {
            throw new IllegalArgumentException("PEM内容不能为空");
        }
        return pemContent
                // 删除 BEGIN 标记行
                .replaceAll("(?m)^-----BEGIN[^-]+-----$", "")
                // 删除 END 标记行
                .replaceAll("(?m)^-----END[^-]+-----$", "")
                // 删除所有空白字符：换行、\r、\t、空格
                .replaceAll("\\s+", "");
    }

    /**
     * 裸base64 → RSA PKCS‑8私钥对象
     * @param rawBase64 清洗完的base64
     * @return PrivateKey
     */
    public static PrivateKey parsePkcs8PrivateKey(String rawBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(rawBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            log.error("PemKeyParser 解析PKCS8 RSA私钥失败", e);
            throw new RuntimeException("解析RSA私钥失败", e);
        }
    }

    /**
     * 裸base64 → RSA X509公钥对象
     * @param rawBase64 清洗完的base64
     * @return PublicKey
     */
    public static PublicKey parseX509PublicKey(String rawBase64) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(rawBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("PemKeyParser 解析X509 RSA公钥失败", e);
            throw new RuntimeException("解析RSA公钥失败", e);
        }
    }
}
