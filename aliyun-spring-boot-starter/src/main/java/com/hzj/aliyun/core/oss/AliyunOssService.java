package com.hzj.aliyun.core.oss;



import com.hzj.aliyun.core.oss.domain.AliyunMediaUploadDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.codehaus.jettison.json.JSONException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface AliyunOssService {


    /**
     * 上传媒体文件并返回媒体详情信息
     *
     * @param originalFilename 原始文件名
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails upload(String originalFilename, InputStream inputStream);

    /**
     * 上传文件到指定存储桶。
     *
     * @param bucket           存储桶名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails upload(String bucket, String originalFilename, InputStream inputStream);

    /**
     * 上传文件到自动选择的存储桶，并使用指定对象名称。
     *
     * @param originalFilename 原始文件名
     * @param objectName       OSS 对象名称
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails uploadByObjectName(String originalFilename, String objectName, InputStream inputStream);

    /**
     * 上传文件到指定存储桶和对象名称。
     *
     * @param bucket           存储桶名称
     * @param objectName       OSS 对象名称
     * @param originalFilename 原始文件名
     * @param inputStream      文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails upload(String bucket, String objectName, String originalFilename, InputStream inputStream);

    /**
     * 使用分片上传文件并返回媒体详情信息
     *
     * @param originFileName 原始文件名
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails multipartUpload(String originFileName, InputStream inputStream);

    /**
     * 使用分片上传文件到指定存储桶。
     *
     * @param bucket         存储桶名称
     * @param originFileName 原始文件名
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails multipartUpload(String bucket, String originFileName, InputStream inputStream);

    /**
     * 使用分片上传文件到自动选择的存储桶，并使用指定对象名称。
     *
     * @param originFileName 原始文件名
     * @param objectName     OSS 对象名称
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails multipartUploadByObjectName(String originFileName, String objectName, InputStream inputStream);

    /**
     * 使用分片上传文件到指定存储桶和对象名称。
     *
     * @param bucket         存储桶名称
     * @param objectName     OSS 对象名称
     * @param originFileName 原始文件名
     * @param inputStream    文件输入流
     * @return 上传后的媒体详情对象
     */
    AliyunMediaUploadDetails multipartUpload(String bucket, String objectName, String originFileName, InputStream inputStream);


    /**
     * 从指定的存储桶下载对象到本地文件系统
     *
     * @param bucket     存储桶名称，指定要从中下载对象的存储桶
     * @param objectName 对象名称，指定要下载的具体对象标识符
     * @param path       本地文件路径，指定对象下载后保存的本地文件路径
     */
    boolean download(String bucket, String objectName, String path);

    /**
     * 删除指定存储桶中的单个对象
     *
     * @param bucket     存储桶名称
     * @param objectName 对象名称
     */
    boolean delete(String bucket, String objectName);

    /**
     * 批量删除指定存储桶中的多个对象
     *
     * @param bucket      存储桶名称
     * @param objectNames 对象名称列表
     */
    boolean delete(String bucket, List<String> objectNames);


    /**
     * 检查指定存储桶中的对象是否存在
     *
     * @param bucket     存储桶名称
     * @param objectName 对象名称
     * @return 如果对象存在返回true，否则返回false
     */
    boolean checkExist(String bucket, String objectName);



    /**
     * 根据当前 OSS 动态配置生成对象访问路径。
     *
     * <p>私有权限返回临时签名 URL；公共读和公共读写权限返回公开访问地址。</p>
     *
     * @param bucket     OSS 存储桶名称
     * @param objectName OSS 对象名称
     * @return 对象访问路径
     */
    String getObjectUrl(String bucket, String objectName);


    /**
     * 生成POST签名，用于上传文件
     * @return 签名信息
     */
    Map<String, String> generatePostSignatureForOssUpload(String accessKeyId,String accessKeySecret, String securityToken) throws JsonProcessingException, JSONException;


    /**
     * 视频单帧截取。
     *
     * <p>基于阿里云 OSS 视频截帧能力，为指定视频对象生成单帧截图访问地址。</p>
     *
     * @param bucket OSS 存储桶名称
     * @param objectName 视频对象名称
     * @param timeInMillis 截帧时间点，单位毫秒
     * @return 视频单帧截图的签名访问地址
     */
    String generateVideoSnapshotPresignedUrl(String bucket, String objectName, Long timeInMillis);

}
