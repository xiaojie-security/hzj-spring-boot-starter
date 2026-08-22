package com.hzj.aliyun.core.oss.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.io.BoundedInputStream;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.sdk.service.oss2.OSSClient;
import com.aliyun.sdk.service.oss2.models.*;
import com.aliyun.sdk.service.oss2.transport.BinaryData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzj.aliyun.core.oss.AbstractAliyunOssService;
import com.hzj.aliyun.core.oss.domain.AliyunMediaUploadDetails;
import com.hzj.aliyun.core.oss.domain.AliyunPostUploadSignature;
import com.hzj.aliyun.core.oss.exception.AliyunOssException;
import com.hzj.aliyun.provider.aliyun.oss.AliyunOssConfigProvider;
import com.hzj.aliyun.provider.aliyun.oss.entity.AliyunOssConfig;
import com.hzj.aliyun.provider.aliyun.oss.enums.AliyunOssPermission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 阿里云 存储服务
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunOssService extends AbstractAliyunOssService {
    public static final long EXPIRE_TIME = 60 * 15L;
    public static final long FILE_SIZE_MB = 500L;
    private final OSSClient ossV2Client;
    private final OSS ossClient;
    private final AliyunOssConfigProvider configProvider;



    /**
     * 上传文件对象
     *
     * @param inputStream      文件流
     * @param originalFilename 文件原始名称
     */
    @Override
    public AliyunMediaUploadDetails upload(String originalFilename, InputStream inputStream) {
        return upload(null, null, originalFilename, inputStream, false);
    }

    /**
     * 上传文件到指定存储桶。
     *
     * @param bucket           存储桶名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails upload(String bucket, String originalFilename, InputStream inputStream) {
        return upload(bucket, null, originalFilename, inputStream, false);
    }

    /**
     * 上传文件到自动选择的存储桶，并使用指定对象名称。
     *
     * @param originalFilename 原始文件名
     * @param objectName       OSS 对象名称
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails uploadByObjectName(String originalFilename, String objectName,
                                                       InputStream inputStream) {
        return upload(null, objectName, originalFilename, inputStream, false);
    }

    /**
     * 上传文件到指定存储桶和对象名称。
     *
     * @param bucket           存储桶名称
     * @param objectName       OSS 对象名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails upload(String bucket, String objectName, String originalFilename,
                                           InputStream inputStream) {
        return upload(bucket, objectName, originalFilename, inputStream, false);
    }

    /**
     * 上传文件对象
     *
     * @param originalFilename 文件原始名称
     * @param inputStream      文件流
     * @param isValidate       是否进行文件 MD5校验
     * @return 文件信息对象
     */
    public AliyunMediaUploadDetails upload(String originalFilename, InputStream inputStream, boolean isValidate) {
        return upload(null, null, originalFilename, inputStream, isValidate);
    }

    /**
     * 执行普通上传的统一实现。
     *
     * @param bucket           可选的存储桶名称
     * @param objectName       可选的 OSS 对象名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @param isValidate       是否进行文件 MD5 校验
     * @return 上传后的媒体详情对象
     */
    private AliyunMediaUploadDetails upload(String bucket, String objectName, String originalFilename,
                                             InputStream inputStream, boolean isValidate) {
        AliyunMediaUploadDetails mediaUploadDetail = new AliyunMediaUploadDetails();
        // 0. 下载文件 避免单向流导致流空
        String suffix = getFileSuffix(originalFilename);
        File downloaded = download(inputStream, suffix);
        // 1. 初始化oss配置
        String contentType = getFileContentType(downloaded);
        String targetBucket = StrUtil.isBlank(bucket) ? getBucket(contentType) : bucket;
        // 判断bucket是否存在
        ensureBucketExists(targetBucket);
        // 2. 构建新的文件名称
        String fileName = StrUtil.isBlank(objectName) ? UUID.randomUUID() + suffix : FileUtil.getName(objectName);
        // 3. 构建objectName
        String targetObjectName = StrUtil.isBlank(objectName) ? getDirectory() + fileName : objectName;
        // 4. 上传文件
        try {
            PutObjectResult result = this.uploadOss(targetBucket, targetObjectName, downloaded);
            String contentMd5 = result.contentMd5();
            // 5. 是否需要校验文件完整性
            if (isValidate) {
                // 5.1 将文件下载到本地
                String path = createTempPath();
                boolean download = download(targetBucket, targetObjectName, path);
                // 5.2 文件下载失败 删除文件
                if (!download) {
                    delete(targetBucket, targetObjectName);
                    throw AliyunOssException.FILE_DOWNLOAD_ERROR;
                }
                // 5.3 校验文件完整性
                String validateMd5 = validateFileIntegrity(contentMd5, path);
                if (StrUtil.isEmpty(validateMd5)) {
                    log.error("AliyunOssMediaService upload 文件校验失败 objectName: {} ", targetObjectName);
                    delete(targetBucket, targetObjectName);
                    throw AliyunOssException.FILE_HASH_ERROR;
                }
                CompletableFuture.runAsync(() -> FileUtil.del(path));
            }
            mediaUploadDetail.setMd5(contentMd5);
            mediaUploadDetail.setBucket(targetBucket);
            mediaUploadDetail.setObjectName(targetObjectName);
            mediaUploadDetail.setOriginFileName(originalFilename);
            mediaUploadDetail.setFinalFileName(fileName);
            mediaUploadDetail.setContentType(contentType);
            AliyunOssConfig config = configProvider.getConfig();
            mediaUploadDetail.setEndpoint(config.getEndpoint());
            mediaUploadDetail.setRegion(config.getRegion());
            mediaUploadDetail.setUri(getObjectUrl(targetBucket, targetObjectName));
            return mediaUploadDetail;
        } catch (Exception e) {
            log.error("AliyunOssMediaService upload 文件上传失败 objectName: {} ", targetObjectName);
            delete(targetBucket, targetObjectName);
            return null;
        } finally {
            FileUtil.del(downloaded);
        }
    }

    @Override
    public AliyunMediaUploadDetails multipartUpload(String originalFilename, InputStream inputStream) {
        return multipartUploadInternal(null, null, originalFilename, inputStream);
    }

    /**
     * 使用分片上传文件到指定存储桶。
     *
     * @param bucket         存储桶名称
     * @param originalFilename 原始文件名
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails multipartUpload(String bucket, String originalFilename, InputStream inputStream) {
        return multipartUploadInternal(bucket, null, originalFilename, inputStream);
    }

    /**
     * 使用分片上传文件到自动选择的存储桶，并使用指定对象名称。
     *
     * @param originalFilename 原始文件名
     * @param objectName       OSS 对象名称
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails multipartUploadByObjectName(String originalFilename, String objectName,
                                                                InputStream inputStream) {
        return multipartUploadInternal(null, objectName, originalFilename, inputStream);
    }

    /**
     * 使用分片上传文件到指定存储桶和对象名称。
     *
     * @param bucket           存储桶名称
     * @param objectName       OSS 对象名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    @Override
    public AliyunMediaUploadDetails multipartUpload(String bucket, String objectName, String originalFilename,
                                                    InputStream inputStream) {
        return multipartUploadInternal(bucket, objectName, originalFilename, inputStream);
    }

    /**
     * 执行分片上传的统一实现。
     *
     * @param bucket           可选的存储桶名称
     * @param objectName       可选的 OSS 对象名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    private AliyunMediaUploadDetails multipartUploadInternal(String bucket, String objectName,
                                                              String originalFilename, InputStream inputStream) {
        AliyunMediaUploadDetails mediaUploadDetail = new AliyunMediaUploadDetails();
        // 0. 下载文件 避免单向流导致流空
        String suffix = getFileSuffix(originalFilename);
        File downloaded = download(inputStream, suffix);
        // 1. 初始化oss配置
        String contentType = getFileContentType(downloaded);
        String targetBucket = StrUtil.isBlank(bucket) ? getBucket(contentType) : bucket;
        // 判断bucket是否存在
        ensureBucketExists(targetBucket);
        // 2. 构建新的文件名称
        String fileName = StrUtil.isBlank(objectName) ? UUID.randomUUID() + suffix : FileUtil.getName(objectName);
        // 3. 构建objectName
        String targetObjectName = StrUtil.isBlank(objectName) ? getDirectory() + fileName : objectName;
        // 4. 初始化分片上传请求
        InitiateMultipartUploadRequest multipartUploadRequest = InitiateMultipartUploadRequest.newBuilder()
                .bucket(targetBucket)
                .key(targetObjectName)
                .build();
        InitiateMultipartUploadResult initiateResult = ossV2Client.initiateMultipartUpload(multipartUploadRequest);
        String uploadId = initiateResult.initiateMultipartUpload().uploadId();

        // 5. 分片上传
        long fileSize = downloaded.length();
        long partSize = 100 * 1024; // 100KB per part
        int partNumber = 1;
        List<Part> uploadParts = new ArrayList<>();

        for (long start = 0; start < fileSize; start += partSize) {
            long curPartSize = Math.min(partSize, fileSize - start);

            try (InputStream is = new FileInputStream(downloaded)) {
                is.skip(start);
                BoundedInputStream boundedInputStream = new BoundedInputStream(is, curPartSize);

                // 5.1 上传每一个小分片
                UploadPartRequest uploadPartRequest = UploadPartRequest.newBuilder()
                        .bucket(targetBucket)
                        .key(targetObjectName)
                        .uploadId(uploadId)
                        .partNumber((long) partNumber)
                        .body(BinaryData.fromStream(boundedInputStream))
                        .build();

                UploadPartResult partResult = ossV2Client.uploadPart(uploadPartRequest);

                log.info("AliyunOssMediaService multipartUpload status code: {}, request id: {}, part number: {}, etag: {}",
                        partResult.statusCode(), partResult.requestId(), partNumber, partResult.eTag());

                uploadParts.add(Part.newBuilder()
                        .partNumber((long) partNumber)
                        .eTag(partResult.eTag())
                        .build());
            } catch (IOException e) {
                log.error("AliyunOssMediaService multipartUpload 上传分片失败");
                abortMultipartUpload(targetBucket, targetObjectName, uploadId);
                throw AliyunOssException.UPLOAD_ERROR;
            }
            partNumber++;
        }
        // 6. 分片排序
        uploadParts.sort(Comparator.comparing(Part::partNumber));

        // 7. 完成分片上传
        CompleteMultipartUpload completeMultipartUpload = CompleteMultipartUpload.newBuilder()
                .parts(uploadParts)
                .build();
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.newBuilder()
                .bucket(targetBucket)
                .key(targetObjectName)
                .uploadId(uploadId)
                .completeMultipartUpload(completeMultipartUpload)
                .build();
        CompleteMultipartUploadResult completeResult = ossV2Client.completeMultipartUpload(completeMultipartUploadRequest);

        log.info("AliyunOssMediaService multipartUpload 分片上传完成 status code:{}, request id:{}, bucket:{}, objectName:{}, location:{}, etag:{}",
                completeResult.statusCode(), completeResult.requestId(), completeResult.completeMultipartUpload().bucket(),
                completeResult.completeMultipartUpload().key(), completeResult.completeMultipartUpload().location(),
                completeResult.completeMultipartUpload().eTag());

        mediaUploadDetail.setMd5(completeResult.hashCRC64());
        mediaUploadDetail.setBucket(targetBucket);
        mediaUploadDetail.setObjectName(targetObjectName);
        mediaUploadDetail.setOriginFileName(originalFilename);
        mediaUploadDetail.setFinalFileName(fileName);
        mediaUploadDetail.setContentType(contentType);
        AliyunOssConfig config = configProvider.getConfig();
        mediaUploadDetail.setEndpoint(config.getEndpoint());
        mediaUploadDetail.setRegion(config.getRegion());
        mediaUploadDetail.setUri(getObjectUrl(targetBucket, targetObjectName));
        return mediaUploadDetail;
    }

    /**
     * 取消（中止）正在进行的分片上传任务，并清理已上传的分片碎片。
     *
     * @param bucket     目标存储空间名称 (Bucket Name)
     * @param objectName 目标对象完整路径 (Object Key)，即初始化分片上传时指定的文件名
     * @param uploadId   分片上传任务的唯一标识符 (Upload ID)，由初始化接口返回
     */
    private void abortMultipartUpload(String bucket, String objectName, String uploadId) {
        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.newBuilder()
                .bucket(bucket)
                .key(objectName)
                .uploadId(uploadId)
                .build();
        AbortMultipartUploadResult result = ossV2Client.abortMultipartUpload(abortMultipartUploadRequest);
        log.info("AliyunOssMediaService abortMultipartUpload status code:{}, request id:{}", result.statusCode(), result.requestId());
    }

    /**
     * 下载 OSS 文件到本地指定路径（通过路径字符串）
     *
     * @param bucket     OSS 存储桶名称
     * @param objectName 文件在 OSS 中的完整路径（不包含桶名）
     * @param path       本地保存文件的路径
     * @throws AliyunOssException 当保存路径为空时抛出
     */
    @Override
    public boolean download(String bucket, String objectName, String path) {
        if (StrUtil.isEmptyIfStr(path)) {
            throw AliyunOssException.SAVE_FILE_PATH_NOT_NULL;
        }
        return download(bucket, objectName, new File(path));
    }


    /**
     * 下载文件到本地 指定路径
     *
     * @param bucket     指定要下载文件的桶
     * @param objectName 指定要下载的文件全路径 不包含桶
     * @param file       指定要下载的文件
     */
    public boolean download(String bucket, String objectName, File file) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.newBuilder()
                    .bucket(bucket)
                    .key(objectName)
                    .build();
            GetObjectResult object = ossV2Client.getObject(objectRequest);
            InputStream inputStream = object.body();
            download(inputStream, file);
            log.info("AliyunOssMediaService download 文件下载成功: path: {}", file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            log.error("AliyunOssMediaService download 文件下载失败: path: {} 错误原因: {}", file.getAbsolutePath(), e.getMessage());
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param bucket     指定要删除文件的桶
     * @param objectName 指定要删除的文件全路径 不包含桶
     */
    @Override
    public boolean delete(String bucket, String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.newBuilder()
                .bucket(bucket)
                .key(objectName)
                .build();
        DeleteObjectResult result = ossV2Client.deleteObject(deleteObjectRequest);
        log.info("AliyunOssMediaService delete 删除指定文件成功：bucket:{} object:{} statusCode:{},",
                bucket, objectName, result.statusCode());
        return true;
    }

    @Override
    public boolean delete(String bucket, List<String> objectNames) {

        List<ObjectIdentifier> identifiers = objectNames.stream()
                .map(objectName -> ObjectIdentifier.newBuilder().key(objectName).build())
                .toList();

        Delete delete = Delete.newBuilder()
                .quiet(false)
                .objects(identifiers)
                .build();

        DeleteMultipleObjectsRequest deleteMultipleObjectsRequest = DeleteMultipleObjectsRequest.newBuilder()
                .bucket(bucket)
                .delete(delete)
                .build();

        DeleteMultipleObjectsResult result = ossV2Client.deleteMultipleObjects(deleteMultipleObjectsRequest);

        log.info("AliyunOssMediaService delete 删除指定文件成功：bucket:{} objectNames:{} statusCode:{},",
                bucket, objectNames, result.statusCode());
        return false;
    }

    @Override
    public boolean checkExist(String bucket, String objectName) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.newBuilder()
                .bucket(bucket)
                .key(objectName)
                .build();
        HeadObjectResult result = ossV2Client.headObject(headObjectRequest);
        log.info("AliyunOssMediaService checkExist bucket:{} objectName:{} statusCode:{}",
                bucket, objectName, result.statusCode());
        return true;
    }

    /**
     * 当bucket为私有时 生成临时签名URL访问资源
     *
     * @param bucket     桶
     * @param objectName 文件存储路径
     * @return 签名URL
     */
    private String generatePresignedUrl(String bucket, String objectName) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectName);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * configProvider.getConfig().getExpire()));
        request.setMethod(com.aliyun.oss.HttpMethod.GET);
        // 全新v2版本未提供生成临时签名的方法 使用老版本兼容先
        URL url = ossClient.generatePresignedUrl(request);
        AliyunOssConfig config = configProvider.getConfig();
        if (StrUtil.isBlank(config.getDomain())) {
            return url.toString();
        }
        String scheme = Boolean.TRUE.equals(config.getHttps()) ? "https" : "http";
        String domain = trimTrailingSlash(removeScheme(config.getDomain()));
        String query = StrUtil.isBlank(url.getQuery()) ? "" : "?" + url.getQuery();
        return scheme + "://" + domain + url.getPath() + query;
    }

    /**
     * 根据当前 OSS 动态配置生成对象访问路径。
     *
     * @param bucket     OSS 存储桶名称
     * @param objectName OSS 对象名称
     * @return 对象访问路径
     */
    @Override
    public String getObjectUrl(String bucket, String objectName) {
        if (StrUtil.hasBlank(bucket, objectName)) {
            log.error("AliyunOssMediaService getObjectUrl 参数错误 bucket:{} objectName:{}", bucket, objectName);
            throw AliyunOssException.FILE_NAME_ERROR;
        }
        AliyunOssConfig config = configProvider.getConfig();
        AliyunOssPermission permission = config.getPermission();
        if (permission == null || AliyunOssPermission.PRIVATE == permission) {
            return generatePresignedUrl(bucket, objectName);
        }
        return buildPublicObjectUrl(config, bucket, objectName);
    }

    /**
     * 构建公共权限对象访问地址。
     *
     * @param config     当前 OSS 配置
     * @param bucket     OSS 存储桶名称
     * @param objectName OSS 对象名称
     * @return 公共对象访问地址
     */
    private String buildPublicObjectUrl(AliyunOssConfig config, String bucket, String objectName) {
        String scheme = Boolean.TRUE.equals(config.getHttps()) ? "https" : "http";
        String domain = StrUtil.isBlank(config.getDomain())
                ? bucket + "." + removeScheme(config.getEndpoint())
                : removeScheme(config.getDomain());
        return scheme + "://" + trimTrailingSlash(domain) + "/" + trimLeadingSlash(objectName);
    }

    /**
     * 移除域名中的协议前缀。
     *
     * @param value 域名
     * @return 不含协议的域名
     */
    private String removeScheme(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceFirst("^https?://", "");
    }

    /**
     * 移除字符串尾部的斜线。
     *
     * @param value 原始字符串
     * @return 处理后的字符串
     */
    private String trimTrailingSlash(String value) {
        return value.replaceAll("/+\\z", "");
    }

    /**
     * 移除字符串开头的斜线。
     *
     * @param value 原始字符串
     * @return 处理后的字符串
     */
    private String trimLeadingSlash(String value) {
        return value.replaceFirst("^/+", "");
    }

    /**
     * 生成视频单帧截图的签名访问地址。
     *
     * <p>根据阿里云 OSS 官方视频截帧能力，使用 {@code x-oss-process=video/snapshot} 实时处理视频并返回截图地址。</p>
     *
     * @param bucket       OSS 存储桶名称
     * @param objectName   视频对象名称
     * @param timeInMillis 截帧时间点，单位毫秒
     * @return 视频单帧截图签名访问地址
     */
    @Override
    public String generateVideoSnapshotPresignedUrl(String bucket, String objectName, Long timeInMillis) {
        if (StrUtil.hasBlank(bucket, objectName) || timeInMillis == null || timeInMillis < 0) {
            log.error("AliyunOssMediaService generateVideoSnapshotPresignedUrl 参数错误 bucket:{} objectName:{} timeInMillis:{}",
                    bucket, objectName, timeInMillis);
            throw AliyunOssException.FILE_NAME_ERROR;
        }
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, objectName);
        request.setExpiration(new Date(System.currentTimeMillis() + 1000L * configProvider.getConfig().getExpire()));
        request.setMethod(com.aliyun.oss.HttpMethod.GET);
        // 默认输出 jpg，并开启 fast 模式，满足大多数视频预览场景
        request.setProcess(buildVideoSnapshotProcess(timeInMillis));
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }


    @Override
    public AliyunPostUploadSignature generatePostSignatureForOssUpload(String accessKeyId, String accessKeySecret, String securityToken) throws JsonProcessingException, JSONException {
        if (StrUtil.isEmpty(accessKeyId) || StrUtil.isEmpty(accessKeySecret) || StrUtil.isEmpty(securityToken)) {
            log.error("AliyunOssMediaService generatePostSignatureForOssUpload 参数错误 accessKeyId:{} accessKeySecret:{} securityToken:{}",
                    accessKeyId, accessKeySecret, securityToken);
            throw AliyunOssException.SYSTEM_ERROR;
        }
        AliyunOssConfig config = configProvider.getConfig();
        String region = config.getRegion();
        String endpoint = config.getEndpoint();
        String directory = getDirectory();
        String bucket = getBucket();
        String callback = config.getCallback();

        //获取x-oss-credential里的date，当前日期，格式为yyyyMMdd
        ZonedDateTime today = ZonedDateTime.now().withZoneSameInstant(java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String date = today.format(formatter);
        //获取x-oss-date
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(java.time.ZoneOffset.UTC);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String x_oss_date = now.format(formatter2);

        // 步骤1：创建policy。
        String x_oss_credential = accessKeyId + "/" + date + "/" + region + "/oss/aliyun_v4_request";

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> policy = new HashMap<>();
        policy.put("expiration", generateExpiration(EXPIRE_TIME));

        List<Object> conditions = new ArrayList<>();

        Map<String, String> bucketCondition = new HashMap<>();

        bucketCondition.put("bucket", bucket);
        conditions.add(bucketCondition);

        Map<String, String> securityTokenCondition = new HashMap<>();
        securityTokenCondition.put("x-oss-security-token", securityToken);
        conditions.add(securityTokenCondition);

        Map<String, String> signatureVersionCondition = new HashMap<>();
        signatureVersionCondition.put("x-oss-signature-version", "OSS4-HMAC-SHA256");
        conditions.add(signatureVersionCondition);

        Map<String, String> credentialCondition = new HashMap<>();
        credentialCondition.put("x-oss-credential", x_oss_credential);
        conditions.add(credentialCondition);

        Map<String, String> dateCondition = new HashMap<>();
        dateCondition.put("x-oss-date", x_oss_date);
        conditions.add(dateCondition);

        conditions.add(Arrays.asList("content-length-range", 1, FILE_SIZE_MB * 1024 * 1024));
        conditions.add(Arrays.asList("eq", "$success_action_status", "200"));
        conditions.add(Arrays.asList("starts-with", "$key", directory));

        policy.put("conditions", conditions);

        String jsonPolicy = mapper.writeValueAsString(policy);

        // 步骤2：构造待签名字符串（StringToSign）。
        String stringToSign = new String(Base64.encodeBase64(jsonPolicy.getBytes()));

        // 步骤3：计算SigningKey。
        byte[] dateKey = hmacsha256(("aliyun_v4" + accessKeySecret).getBytes(), date);
        byte[] dateRegionKey = hmacsha256(dateKey, region);
        byte[] dateRegionServiceKey = hmacsha256(dateRegionKey, "oss");
        byte[] signingKey = hmacsha256(dateRegionServiceKey, "aliyun_v4_request");

        // 步骤4：计算Signature。
        byte[] result = hmacsha256(signingKey, stringToSign);
        String signature = BinaryUtil.toHex(result);

        // 步骤5：设置回调。
        JSONObject jasonCallback = new JSONObject();
        jasonCallback.put("callbackUrl", callback);
        // 客户端再提交表单时需要传递这些自定义属性
        jasonCallback.put("callbackBody","bucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}" +
                "&contentMd5=${contentMd5}&ossId=${x:oss_id}&originalName=${x:original_name}&fileName=${x:file_name}");
        jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
        String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());


        AliyunPostUploadSignature uploadSignature = new AliyunPostUploadSignature();
        uploadSignature.setVersion("OSS4‑HMAC‑SHA256");
        uploadSignature.setPolicy(stringToSign);
        uploadSignature.setXOssCredential(x_oss_credential);
        uploadSignature.setXOssDate(x_oss_date);
        uploadSignature.setSignature(signature);
        uploadSignature.setSecurityToken(securityToken);
        uploadSignature.setDir(directory);
        uploadSignature.setHost("https://" + bucket + "." + endpoint);
        uploadSignature.setCallback(base64CallbackBody);
        return uploadSignature;
    }


    /**
     * 构建 OSS 视频截帧处理参数。
     *
     * @param timeInMillis 截帧时间点，单位毫秒
     * @return OSS 视频截帧处理字符串
     */
    private String buildVideoSnapshotProcess(Long timeInMillis) {
        return "video/snapshot,t_" + timeInMillis + ",f_jpg,m_fast";
    }

    /**
     * 根据文件类型获取对应的bucket
     *
     * @return bucket
     */
    public String getBucket() {
        return getBucket(null);
    }

    /**
     * 根据文件类型获取对应的bucket
     *
     * @param contentType 文件类型
     * @return bucket
     */
    public String getBucket(String contentType) {
        if (StrUtil.isEmpty(contentType)) {
            return configProvider.getConfig().getDefaultBucket();
        }
        if (!contentType.contains("/")) {
            return configProvider.getConfig().getDefaultBucket();
        }
        // 获取文件类型的前缀
        String prefix = contentType.split("/")[0];
        AliyunOssConfig config = configProvider.getConfig();
        Map<String, String> bucketMap = config.getBuckets();
        return CollUtil.isNotEmpty(bucketMap) && bucketMap.containsKey(prefix) ? bucketMap.get(prefix) : config.getDefaultBucket();
    }

    /**
     * 确保指定的 Bucket 存在，如果不存在则创建
     *
     * @param bucket Bucket 名称
     */
    private void ensureBucketExists(String bucket) {
        if (!ossV2Client.doesBucketExist(bucket)) {
            PutBucketRequest putBucketRequest = PutBucketRequest.newBuilder()
                    .bucket(bucket)
                    .build();
            ossV2Client.putBucket(putBucketRequest);
        }
    }

    /**
     * 上传文件到 OSS
     *
     * @param bucket     Bucket 名称
     * @param objectName OSS 对象名称
     * @param file       要上传的文件
     */
    private PutObjectResult uploadOss(String bucket, String objectName, File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return uploadOss(bucket, objectName, inputStream);
        } catch (IOException e) {
            throw AliyunOssException.FILE_READ_ERROR;
        }
    }

    private PutObjectResult uploadOss(String bucket, String objectName, InputStream inputStream) {
        try {
            return ossV2Client.putObject(
                    PutObjectRequest.newBuilder()
                            .bucket(bucket)
                            .key(objectName)
                            .body(BinaryData.fromStream(inputStream))
                            .build()
            );
        } catch (Exception e) {
            throw AliyunOssException.FILE_READ_ERROR;
        }
    }

    public static byte[] hmacsha256(byte[] key, String data) {
        try {
            // 初始化HMAC密钥规格，指定算法为HMAC-SHA256并使用提供的密钥。
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");

            // 获取Mac实例，并通过getInstance方法指定使用HMAC-SHA256算法。
            Mac mac = Mac.getInstance("HmacSHA256");
            // 使用密钥初始化Mac对象。
            mac.init(secretKeySpec);

            // 执行HMAC计算，通过doFinal方法接收需要计算的数据并返回计算结果的数组。

            return mac.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA256", e);
        }
    }


}
