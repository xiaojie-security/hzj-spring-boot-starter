package com.hzj.wechat.core.xcx.face;

import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceCertInfo;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceGetVerifyIdRequest;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceGetVerifyIdResponse;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceQueryVerifyInfoRequest;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceQueryVerifyInfoResponse;
import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceCertType;

/**
 * 微信小程序人脸验证服务。
 *
 * <p>封装微信人脸核身的两个服务端接口，典型流程为：</p>
 * <ol>
 *     <li>后端按「姓名 + 身份证」调用 {@link #getVerifyId} 拿到 {@code verifyId}；</li>
 *     <li>将 {@code verifyId} 交给小程序前端调用 {@code wx.requestFacialVerify}；</li>
 *     <li>前端核身完成后，后端调用 {@link #queryVerifyInfo} 查询真实验证结果。</li>
 * </ol>
 *
 * <p>核身通过的判断条件为 {@code errcode = 0} 且 {@code verifyRet = 10000}。</p>
 */
public interface WechatXcxFaceVerifyService {

    /**
     * 获取用户人脸核身会话唯一标识。
     *
     * @param request 获取人脸核身会话唯一标识请求参数
     * @return 会话唯一标识与有效期
     */
    WechatXcxFaceGetVerifyIdResponse getVerifyId(WechatXcxFaceGetVerifyIdRequest request);

    /**
     * 获取用户人脸核身会话唯一标识。
     *
     * @param outSeqNo 业务方系统内部流水号，5-32 个字符，同一个 appid 下唯一
     * @param certInfo 用户身份信息
     * @param openid   用户身份标识
     * @return 会话唯一标识与有效期
     */
    WechatXcxFaceGetVerifyIdResponse getVerifyId(String outSeqNo, WechatXcxFaceCertInfo certInfo, String openid);

    /**
     * 获取用户人脸核身会话唯一标识。
     * certType 未设置时使用配置提供者的默认值，仍为空时使用「身份证」。
     *
     * @param outSeqNo 业务方系统内部流水号，5-32 个字符，同一个 appid 下唯一
     * @param certType 证件类型
     * @param certName 证件姓名
     * @param certNo   证件号码
     * @param openid   用户身份标识
     * @return 会话唯一标识与有效期
     */
    WechatXcxFaceGetVerifyIdResponse getVerifyId(String outSeqNo, WechatXcxFaceCertType certType,
                                                 String certName, String certNo, String openid);

    /**
     * 查询用户人脸核身真实验证结果。
     *
     * @param request 查询人脸核身真实验证结果请求参数
     * @return 验证结果
     */
    WechatXcxFaceQueryVerifyInfoResponse queryVerifyInfo(WechatXcxFaceQueryVerifyInfoRequest request);

    /**
     * 查询用户人脸核身真实验证结果。
     * cert_hash 由 {@link WechatXcxFaceCertHashUtils} 根据 getVerifyId 传入的证件信息计算。
     *
     * @param verifyId  人脸核身会话唯一标识
     * @param outSeqNo  业务方系统外部流水号，必须和 getVerifyId 传入的一致
     * @param certHash  证件信息摘要
     * @param openid    用户身份标识，必须和 getVerifyId 传入的一致
     * @return 验证结果
     */
    WechatXcxFaceQueryVerifyInfoResponse queryVerifyInfo(String verifyId, String outSeqNo, String certHash,
                                                         String openid);
}
