package com.hzj.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Locale;

/**
 * 编号生成工具类。
 */
public final class CodeNoUtils {
    /**
     * Base36编码基数。
     */
    private static final int CODE_RADIX = 36;

    /**
     * 编号总长度。
     */
    private static final int CODE_NO_LENGTH = 20;

    /**
     * 业务前缀长度。
     */
    private static final int PREFIX_LENGTH = 3;

    /**
     * 雪花ID Base36 固定长度。
     */
    private static final int SNOWFLAKE_SEGMENT_LENGTH = 13;

    /**
     * 校验段长度。
     */
    private static final int CHECK_SEGMENT_LENGTH = 4;



    private static final String NODE_FINGERPRINT_HEX = buildNodeFingerprintHex();

    private static final String NODE_SEGMENT = NODE_FINGERPRINT_HEX.substring(16, 24).toUpperCase(Locale.ROOT);

    private static final long WORKER_ID = Long.parseLong(NODE_FINGERPRINT_HEX.substring(0, 8), 16) & 31L;

    private static final long DATA_CENTER_ID = Long.parseLong(NODE_FINGERPRINT_HEX.substring(8, 16), 16) & 31L;

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(WORKER_ID, DATA_CENTER_ID);

    private CodeNoUtils() {
    }

    /**
     * 生成编号。
     *
     * <p>结构说明：</p>
     * <p>前缀3位 + Base36雪花ID13位 + 校验段4位 = 20位</p>
     *
     * @param prefix 业务前缀
     * @return 20位编号
     */
    public static String generateCodeNo(String prefix) {
        String normalizedPrefix = normalizePrefix(prefix);
        long snowflakeId = SNOWFLAKE.nextId();
        String snowflakeSegment = buildSnowflakeSegment(snowflakeId);
        String checkSegment = buildCheckSegment(normalizedPrefix, snowflakeSegment);
        String codeNo = normalizedPrefix + snowflakeSegment + checkSegment;
        if (codeNo.length() != CODE_NO_LENGTH) {
            throw new IllegalStateException("编号长度必须为20位，实际长度：" + codeNo.length());
        }
        return codeNo;
    }

    /**
     * 规范化前缀。
     *
     * @param prefix 前缀
     * @return 3位大写前缀
     */
    private static String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new IllegalArgumentException("业务前缀不能为空");
        }
        String normalizedPrefix = prefix.trim().toUpperCase(Locale.ROOT);
        if (normalizedPrefix.length() != 3) {
            throw new IllegalArgumentException("业务前缀长度必须为3位");
        }
        return normalizedPrefix;
    }

    /**
     * 构建雪花ID固定长度片段。
     *
     * @param snowflakeId 雪花ID
     * @return 13位Base36编码
     */
    private static String buildSnowflakeSegment(long snowflakeId) {
        String encodedSnowflakeId = Long.toString(snowflakeId, CODE_RADIX).toUpperCase(Locale.ROOT);
        if (encodedSnowflakeId.length() > SNOWFLAKE_SEGMENT_LENGTH) {
            throw new IllegalStateException("雪花ID编码长度超出限制，实际长度：" + encodedSnowflakeId.length());
        }
        return leftPad(encodedSnowflakeId, SNOWFLAKE_SEGMENT_LENGTH);
    }

    /**
     * 生成固定长度校验段。
     *
     * @param prefix 业务前缀
     * @param snowflakeSegment 雪花ID片段
     * @return 4位校验段
     */
    private static String buildCheckSegment(String prefix, String snowflakeSegment) {
        String source = prefix + "|" + snowflakeSegment + "|" + NODE_SEGMENT;
        return sha256Hex(source).substring(0, CHECK_SEGMENT_LENGTH).toUpperCase(Locale.ROOT);
    }

    /**
     * 构建节点指纹。
     *
     * @return 64位十六进制节点指纹
     */
    private static String buildNodeFingerprintHex() {
        StringBuilder builder = new StringBuilder();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            builder.append(localHost.getHostName()).append('|').append(localHost.getHostAddress()).append('|');
        } catch (Exception ignored) {
            builder.append("unknown-host|");
        }
        builder.append(ManagementFactory.getRuntimeMXBean().getName()).append('|');
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null || hardwareAddress.length == 0) {
                    continue;
                }
                for (byte value : hardwareAddress) {
                    builder.append(String.format("%02x", value));
                }
                builder.append('|');
            }
        } catch (Exception ignored) {
            builder.append("unknown-mac|");
        }
        return sha256Hex(builder.toString());
    }

    /**
     * 对字符串做SHA-256。
     *
     * @param source 原始字符串
     * @return 十六进制摘要
     */
    private static String sha256Hex(String source) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(digest.length * 2);
            for (byte value : digest) {
                builder.append(String.format("%02x", value));
            }
            return builder.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("生成编号摘要失败", exception);
        }
    }

    /**
     * 左侧补零。
     *
     * @param value 原始值
     * @param length 目标长度
     * @return 补零后的字符串
     */
    private static String leftPad(String value, int length) {
        if (value.length() >= length) {
            return value;
        }
        return "0".repeat(length - value.length()) + value;
    }
}
