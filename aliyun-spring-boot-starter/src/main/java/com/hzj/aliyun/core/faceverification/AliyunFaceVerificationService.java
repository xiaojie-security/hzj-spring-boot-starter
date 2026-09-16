package com.hzj.aliyun.core.faceverification;

import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastResult;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationDescribeParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationDescribeResult;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitResult;

/**
 * 阿里云金融级实人认证服务。
 */
public interface AliyunFaceVerificationService {

    /**
     * 发起 H5 或 APP 实人认证。
     *
     * @param param 发起认证参数
     * @return 认证链接和认证 ID
     */
    AliyunFaceVerificationInitResult initFaceVerify(AliyunFaceVerificationInitParam param);

    /**
     * 获取 H5 或 APP 实人认证结果。
     *
     * @param param 查询认证结果参数
     * @return 认证结果
     */
    AliyunFaceVerificationDescribeResult describeFaceVerify(AliyunFaceVerificationDescribeParam param);

    /**
     * 执行照片实人认证。
     *
     * @param param 照片认证参数
     * @return 认证结果
     */
    AliyunFaceVerificationContrastResult contrastFaceVerifyPhoto(
            AliyunFaceVerificationContrastParam param);

    /**
     * 执行视频实人认证。
     *
     * <p>设置视频 URL 时使用普通接口，设置本地视频输入流时使用 SDK 高级上传接口。</p>
     *
     * @param param 视频认证参数
     * @return 认证结果
     */
    AliyunFaceVerificationContrastResult contrastFaceVerifyVideo(
            AliyunFaceVerificationContrastParam param);

    /**
     * 根据参数中的视频字段自动选择照片或视频实人认证接口。
     *
     * @param param 实人认证参数
     * @return 认证结果
     */
    AliyunFaceVerificationContrastResult contrastFaceVerify(
            AliyunFaceVerificationContrastParam param);
}
