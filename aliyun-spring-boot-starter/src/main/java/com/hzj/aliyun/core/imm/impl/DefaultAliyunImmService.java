package com.hzj.aliyun.core.imm.impl;

import com.hzj.aliyun.core.imm.AliyunImmService;
import com.hzj.aliyun.core.imm.exception.AliyunImmException;
import com.aliyun.imm20200930.models.CreateMediaConvertTaskResponseBody;
import com.hzj.aliyun.core.imm.domain.AliyunTransCodeResult;
import com.hzj.aliyun.provider.aliyun.imm.AliyunImmConfigProvider;
import com.hzj.aliyun.provider.aliyun.imm.entity.AliyunImmConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * 阿里云 IMM智能媒体服务
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunImmService implements AliyunImmService {
    private final AliyunImmConfigProvider configProvider;
    private static final String VIDEO = "_transcoding";
    private final com.aliyun.imm20200930.Client client;


    /**
     * 同步执行视频转码任务。
     *
     **
     * @param originObjectName            原视频在 OSS 中的objectName（包含后缀）
     * @return 转码结果封装对象 {@link AliyunTransCodeResult}，
     *         包含转码视频及封面图的 OSS 信息；若失败返回 {@code null}
     */
    @Override
    public String transcode(String bucket, String originObjectName) {
        try {
            AliyunImmConfig imm = configProvider.getConfig();
            String objectName = getObjectName(originObjectName);
            String videoObjectName = objectName + getTranscodeVideoSuffix();
            com.aliyun.imm20200930.models.TargetVideo.TargetVideoTranscodeVideo targets0TargetVideoTranscodeVideo = new com.aliyun.imm20200930.models.TargetVideo.TargetVideoTranscodeVideo()
                    .setCodec(imm.getCodec());
            com.aliyun.imm20200930.models.TargetVideo targets0TargetVideo = new com.aliyun.imm20200930.models.TargetVideo()
                    .setTranscodeVideo(targets0TargetVideoTranscodeVideo);
            com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest.CreateMediaConvertTaskRequestTargets targets0 = new com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest.CreateMediaConvertTaskRequestTargets()
                    .setURI(imm.getUri() + bucket + "/" + videoObjectName)
                    .setContainer(imm.getContainer())
                    .setSpeed(1F)
                    .setVideo(targets0TargetVideo);
            com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest.CreateMediaConvertTaskRequestSources sources0 = new com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest.CreateMediaConvertTaskRequestSources()
                    .setURI(imm.getUri() + bucket + "/" + originObjectName)
                    .setStartTime(null)
                    .setDuration(null);
            com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest createMediaConvertTaskRequest = new com.aliyun.imm20200930.models.CreateMediaConvertTaskRequest()
                    .setProjectName(imm.getProjectName())
                    .setSources(Collections.singletonList(
                            sources0
                    ))
                    .setTargets(Collections.singletonList(
                            targets0
                    ));
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            com.aliyun.imm20200930.models.CreateMediaConvertTaskResponse resp = client.createMediaConvertTaskWithOptions(createMediaConvertTaskRequest, runtime);
            Integer statusCode = resp.getStatusCode();
            CreateMediaConvertTaskResponseBody body = resp.getBody();
            if (statusCode != 200) {
                log.error("AliyunImmMediaService start error bucket: {} originObjectName: {},  statusCode: {}, body:{}",
                        bucket,originObjectName, statusCode, body);
                return null;
            }
            log.info("AliyunImmMediaService start success bucket: {} originObjectName: {},  statusCode: {}, body:{}",
                    bucket, originObjectName, statusCode, body);

            return videoObjectName;

        } catch (com.aliyun.tea.TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            log.error("AliyunImmMediaService start com.aliyun.tea.TeaException message: {} bucket: {} originObjectName: {},  诊断地址: {}",
                    error.getMessage(),bucket, originObjectName, error.getData().get("Recommend"));
            return null;
        } catch (Exception _error) {
            log.error("AliyunImmMediaService start Exception _error: {}", _error.getMessage(),_error);
            return null;
        }
    }


    /**
     * 根据完整文件名获取不包含后缀的对象名称。
     *
     * <p>示例：</p>
     * <pre>
     * input:  video/test.mp4
     * output: video/test
     * </pre>
     *
     * @param originObjectName 原始objectName
     * @return 去除后缀后的文件名
     * @throws RuntimeException 当文件名格式错误（不包含 "."）时抛出异常
     */
    private static @NotNull String getObjectName(String originObjectName) {
        // 截取文件名称以及文件后缀
        int lastIndexOf = originObjectName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            log.error("AliyunImmMediaService getObjectName 文件名称错误 originObjectName:{}", originObjectName);
            throw AliyunImmException.FILE_NAME_ERROR;
        }
        return originObjectName.substring(0, lastIndexOf);
    }

    private String getTranscodeVideoSuffix(){
        return VIDEO + "." + configProvider.getConfig().getContainer();
    }

}
