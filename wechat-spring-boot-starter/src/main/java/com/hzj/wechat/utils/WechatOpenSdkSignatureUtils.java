package com.hzj.wechat.utils;

import com.hzj.wechat.core.mobile.share.enums.WechatOpenSdkSignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * 微信 OpenSDK 分享签名密钥工具。
 */
@Slf4j
public final class WechatOpenSdkSignatureUtils {

    private WechatOpenSdkSignatureUtils() {
    }

    /**
     * 根据 PEM 文件加载 OpenSDK 分享签名私钥。
     *
     * @param privateKeyPath 私钥文件路径
     * @param signatureAlgorithm OpenSDK 分享签名算法
     * @return 私钥对象
     */
    public static PrivateKey loadPrivateKeyFromPath(String privateKeyPath,
                                                    WechatOpenSdkSignatureAlgorithm signatureAlgorithm) {
        try {
            return loadPrivateKey(Files.readString(Path.of(privateKeyPath)), signatureAlgorithm);
        } catch (IOException exception) {
            log.error("WechatOpenSdkSignatureUtils.loadPrivateKeyFromPath 读取OpenSDK分享私钥失败, privateKeyPath={}",
                    privateKeyPath, exception);
            throw new UncheckedIOException("读取OpenSDK分享私钥失败，路径：" + privateKeyPath, exception);
        }
    }

    /**
     * 根据 PEM 内容加载 OpenSDK 分享签名私钥。
     *
     * @param privateKeyContent PKCS#8 PEM 私钥内容
     * @param signatureAlgorithm OpenSDK 分享签名算法
     * @return 私钥对象
     */
    public static PrivateKey loadPrivateKey(String privateKeyContent,
                                            WechatOpenSdkSignatureAlgorithm signatureAlgorithm) {
        if (privateKeyContent == null || privateKeyContent.isBlank()) {
            throw new IllegalArgumentException("OpenSDK分享私钥内容不能为空");
        }
        if (signatureAlgorithm == null) {
            throw new IllegalArgumentException("OpenSDK分享签名算法不能为空");
        }
        try {
            byte[] encoded = Base64.getDecoder().decode(privateKeyContent
                    .replaceAll("-----BEGIN [^-]+-----", "")
                    .replaceAll("-----END [^-]+-----", "")
                    .replaceAll("\\s+", ""));
            return switch (signatureAlgorithm) {
                case RSA_WITH_SHA256 -> KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(encoded));
                case SM2_WITH_SM3 -> getBouncyCastleKeyFactory()
                        .generatePrivate(new PKCS8EncodedKeySpec(encoded));
            };
        } catch (IllegalArgumentException exception) {
            log.error("WechatOpenSdkSignatureUtils.loadPrivateKey OpenSDK分享私钥不是合法Base64内容", exception);
            throw new IllegalArgumentException("OpenSDK分享私钥内容不是合法的Base64编码", exception);
        } catch (NoSuchAlgorithmException | NoSuchProviderException exception) {
            log.error("WechatOpenSdkSignatureUtils.loadPrivateKey 当前Java环境不支持OpenSDK签名算法, signatureAlgorithm={}",
                    signatureAlgorithm, exception);
            throw new UnsupportedOperationException("当前Java环境不支持OpenSDK分享签名算法：" + signatureAlgorithm,
                    exception);
        } catch (InvalidKeySpecException exception) {
            log.error("WechatOpenSdkSignatureUtils.loadPrivateKey OpenSDK分享私钥格式不合法, signatureAlgorithm={}",
                    signatureAlgorithm, exception);
            throw new IllegalArgumentException("OpenSDK分享私钥格式不正确，请确认使用PKCS#8格式私钥", exception);
        }
    }

    /**
     * 获取 Bouncy Castle EC 密钥工厂。
     *
     * @return EC 密钥工厂
     * @throws NoSuchAlgorithmException 当前环境不支持 EC 算法时抛出
     */
    private static KeyFactory getBouncyCastleKeyFactory() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        return KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
    }
}
