# aliyun-spring-boot-starter

一个面向 Spring Boot 3 的阿里云能力 Starter，将 OSS、IMM、STS、短信、PNS 和支付宝能力做成自动装配。

- OSS 对象存储上传、下载、删除、分片上传、签名访问
- IMM 媒体转码
- STS 临时凭证获取
- 短信发送
- PNS 号码认证与短信验证码校验
- 支付宝 APP 支付、扫码支付、资金转账、OAuth2 授权

## 配置总则

- 所有配置都放在 `aliyun` 根前缀下。
- 各个服务分开配置，不再把所有信息聚合到一起。
- 支付宝只保留一组全局配置 `aliyun.pay`，APP 支付、扫码支付、资金转账、OAuth2 共用同一套参数。
- 支付宝功能开关按 `payment`、`transfer`、`oauth2` 分开控制，其中 `payment` 同时承载 APP 支付与网站支付。
- 阿里云客户端凭证由全局 Provider 统一管理；每个服务独立配置自己的 `ram-role-arn`。

### 全局访问凭证

```yaml
aliyun:
  credential-mode: sts # sts 或 ak
  access-key-id: your-access-key-id
  access-key-secret: your-access-key-secret
```

- `credential-mode: sts`：使用全局 AK 扮演各服务自己的 `ram-role-arn` 获取临时凭证。
- `credential-mode: ak`：所有客户端直接使用全局 `access-key-id` 和 `access-key-secret`。

## 各服务配置

### STS 临时凭证

```yaml
aliyun:
  sts:
    enable: true
    endpoint: sts.cn-hangzhou.aliyuncs.com
    expire: 3600
    ram-role-arn: acs:ram::xxxx:role/sts-role
```

- 必填：`enable`、`endpoint`
- 可选：`expire`、`ram-role-arn`

### OSS 对象存储

```yaml
aliyun:
  oss:
    enable: true
    endpoint: oss-cn-hangzhou.aliyuncs.com
    region: cn-hangzhou
    default-bucket: your-default-bucket
    permission: private
    https: true
    domain: example.com
    expire: 900
    ram-role-arn: acs:ram::xxxx:role/your-role
    callback: https://your-domain.com/api/oss/callback
    buckets:
      image: your-image-bucket
      video: your-video-bucket
```

- 必填：`enable`、`endpoint`、`region`、`default-bucket`
- 建议：`permission`、`https`、`domain`、`expire`、`ram-role-arn`、`callback`、`buckets`
- `permission` 支持 `private`、`public_read`、`public_read_write`，默认值为 `private`。

### IMM 媒体转码

```yaml
aliyun:
  imm:
    enable: true
    project-name: your-imm-project
    region: cn-hangzhou
    codec: H.264
    endpoint-override: imm.cn-hangzhou.aliyuncs.com
    container: mp4
    uri: oss://
    ram-role-arn: acs:ram::xxxx:role/your-role
```

- 必填：`enable`、`project-name`、`region`、`codec`、`endpoint-override`、`container`、`uri`、`ram-role-arn`

### 短信服务

```yaml
aliyun:
  sms:
    enable: true
    endpoint: dysmsapi.aliyuncs.com
    region: cn-hangzhou
    default-sign-name: 示例签名
    sign-names:
      login_register: 示例签名
    ram-role-arn: acs:ram::xxxx:role/your-role
```

- 必填：`enable`、`endpoint`、`region`、`default-sign-name`
- 可选：`sign-names`、`ram-role-arn`

### PNS 号码认证

```yaml
aliyun:
  pns:
    enable: true
    sign-name: 示例签名
    endpoint: dypnsapi.aliyuncs.com
    region: cn-hangzhou
    ram-role-arn: acs:ram::xxxx:role/your-role
```

- 必填：`enable`、`sign-name`、`endpoint`、`region`
- 可选：`ram-role-arn`

### 支付宝配置

- 全局配置前缀：`aliyun.pay`
- 功能开关前缀：`aliyun.pay.payment`、`aliyun.pay.transfer`、`aliyun.pay.oauth2`

#### 全局配置

```yaml
aliyun:
  pay:
    app-id: your-app-id
    gate-way: https://openapi.alipay.com/gateway.do
    private-key: your-private-key
    public-key: your-public-key
    certificates: false
    app-cert-path: cert/appCertPublicKey.crt
    alipay-public-cert-path: cert/alipayCertPublicKey_RSA2.crt
    root-cert-path: cert/alipayRootCert.crt
    seller-id: your-seller-id
    validity-time: 1800000
    payment-notify-url: https://your-domain.com/api/alipay/notify

    payment:
      enable: true
    transfer:
      enable: true
    oauth2:
      enable: true
```

- 必填：`gate-way`、`private-key`
- 二选一：`public-key` 或者证书模式的三个证书路径

#### 支付能力

```yaml
aliyun:
  pay:
    payment:
      enable: true
```

#### 资金转账

```yaml
aliyun:
  pay:
    transfer:
      enable: true
```

#### OAuth2 授权

```yaml
aliyun:
  pay:
    oauth2:
      enable: true
```

## 使用示例

### OSS

```java
import com.aliyun.core.oss.AliyunOssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class OssDemoController {

    private final AliyunOssService aliyunOssService;

    @PostMapping("/demo/upload")
    public Object upload(@RequestParam("file") MultipartFile file) throws Exception {
        return aliyunOssService.upload(file.getOriginalFilename(), file.getInputStream());
    }
}
```

### OAuth2

```java
import com.aliyun.core.alipay.oauth2.AliPayOAuth2Service;
import com.aliyun.core.alipay.oauth2.domain.AliPaySystemOauthDetails;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlipayOAuthDemoController {

    private final AliPayOAuth2Service aliPayOAuth2Service;

    @GetMapping("/demo/alipay/oauth/token")
    public AliPaySystemOauthDetails token(@RequestParam String code) {
        return aliPayOAuth2Service.getAccessTokenByCode(code);
    }

    @GetMapping("/demo/alipay/oauth/user")
    public AlipayUserInfoShareResponse user(@RequestParam String accessToken) {
        return aliPayOAuth2Service.queryUserInfoShare(accessToken);
    }
}
```

## 构建

```bash
mvn clean package
```

- 要求：编译成功并产生 `target/aliyun-spring-boot-starter-1.0.0.jar`

## 注意事项

- 不要提交真实的 `accessKeyId`、`accessKeySecret`、私钥和证书文件
- OSS 回调地址要能被业务系统公网访问
- 支付宝证书路径建议由业务项目外部配置
