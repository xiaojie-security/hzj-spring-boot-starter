package com.hzj.kuaidi100.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * 快递100签名工具。
 */
public final class Kuaidi100SignUtils {

    private Kuaidi100SignUtils() {
    }

    /**
     * 计算 MD5 签名并转为大写十六进制字符串。
     *
     * @param content 待签名内容
     * @return 32位大写MD5
     */
    public static String md5(String content) {
        return digest("MD5", content);
    }

    /**
     * 计算 SHA-256 签名并转为大写十六进制字符串。
     *
     * @param content 待签名内容
     * @return 大写SHA-256
     */
    public static String sha256(String content) {
        return digest("SHA-256", content);
    }

    /**
     * 计算指定算法的签名。
     *
     * @param content 待签名内容
     * @param signType 签名类型
     * @return 大写签名
     */
    public static String sign(String content, String signType) {
        String normalizedType = signType == null || signType.trim().isEmpty()
                ? "MD5" : signType.trim().toUpperCase(Locale.ROOT);
        if ("MD5".equals(normalizedType)) {
            return md5(content);
        }
        if ("SHA256".equals(normalizedType) || "SHA-256".equals(normalizedType)) {
            return sha256(content);
        }
        throw new IllegalArgumentException("当前运行环境暂不支持快递100签名类型: " + signType);
    }

    private static String digest(String algorithm, String content) {
        if (content == null) {
            throw new IllegalArgumentException("签名内容不能为空");
        }
        try {
            byte[] digest = MessageDigest.getInstance(algorithm)
                    .digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                builder.append(String.format(Locale.ROOT, "%02x", value));
            }
            return builder.toString().toUpperCase(Locale.ROOT);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("JDK不支持签名算法: " + algorithm, exception);
        }
    }
}
