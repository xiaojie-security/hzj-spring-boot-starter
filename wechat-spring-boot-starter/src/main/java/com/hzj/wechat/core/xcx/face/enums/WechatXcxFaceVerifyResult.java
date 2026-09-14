package com.hzj.wechat.core.xcx.face.enums;

import com.google.gson.annotations.JsonAdapter;
import com.hzj.wechat.utils.WechatIntegerEnum;
import com.hzj.wechat.utils.WechatIntegerEnumTypeAdapterFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信人脸核身验证结果（verify_ret）。
 *
 * <p>核身通过的判断条件为 {@code errcode = 0} 且 {@code verify_ret = 10000}。</p>
 */
@JsonAdapter(WechatIntegerEnumTypeAdapterFactory.class)
public enum WechatXcxFaceVerifyResult implements WechatIntegerEnum {

    /**
     * 识别成功。
     */
    SUCCESS(10000, "识别成功"),

    /**
     * 参数错误。
     */
    C10001(10001, "参数错误"),

    /**
     * 人脸特征检测失败。
     */
    C10002(10002, "人脸特征检测失败"),

    /**
     * 身份证号不匹配。
     */
    C10003(10003, "身份证号不匹配"),

    /**
     * 比对人脸信息不匹配。
     */
    C10004(10004, "比对人脸信息不匹配"),

    /**
     * 正在检测中。
     */
    C10005(10005, "正在检测中"),

    /**
     * appid 没有权限。
     */
    C10006(10006, "appid 没有权限"),

    /**
     * 后台获取图片失败。
     */
    C10007(10007, "后台获取图片失败"),

    /**
     * 系统失败。
     */
    C10008(10008, "系统失败"),

    /**
     * 照片质量较低。
     */
    C10010(10010, "照片质量较低"),

    /**
     * 比对验证失败。
     */
    C10012(10012, "比对验证失败"),

    /**
     * 系统错误。
     */
    C10013(10013, "系统错误"),

    /**
     * 系统失败。
     */
    C10014(10014, "系统失败"),

    /**
     * 系统失败。
     */
    C10015(10015, "系统失败"),

    /**
     * 存储用户图片失败。
     */
    C10016(10016, "存储用户图片失败"),

    /**
     * 非法 Id。
     */
    C10017(10017, "非法 Id"),

    /**
     * 用户信息不存在。
     */
    C10018(10018, "用户信息不存在"),

    /**
     * 认证超时。
     */
    C10020(10020, "认证超时"),

    /**
     * 重复的请求，返回上一次的结果。
     */
    C10021(10021, "重复的请求，返回上一次的结果"),

    /**
     * 用户身份数据不在权威源比对库中。
     */
    C10026(10026, "用户身份数据不在权威源比对库中"),

    /**
     * 请求超时。
     */
    C10029(10029, "请求超时"),

    /**
     * 请求数据编码不对，必须是 UTF8 编码。
     */
    C10040(10040, "请求数据编码不对，必须是 UTF8 编码"),

    /**
     * 非法用户。
     */
    C10041(10041, "非法用户"),

    /**
     * 请求过于频繁，稍后再重试。
     */
    C10042(10042, "请求过于频繁，稍后再重试"),

    /**
     * 系统失败。
     */
    C10045(10045, "系统失败"),

    /**
     * 请求超时。
     */
    C10052(10052, "请求超时"),

    /**
     * 未完成核身。
     */
    C10300(10300, "未完成核身"),

    /**
     * 设备不支持人脸检测。
     */
    C90001(90001, "设备不支持人脸检测"),

    /**
     * 用户取消。
     */
    C90002(90002, "用户取消"),

    /**
     * 用户取消。
     */
    C90003(90003, "用户取消"),

    /**
     * 用户取消。
     */
    C90004(90004, "用户取消"),

    /**
     * 用户取消。
     */
    C90005(90005, "用户取消"),

    /**
     * 用户取消。
     */
    C90006(90006, "用户取消"),

    /**
     * 网络错误。
     */
    C90007(90007, "网络错误"),

    /**
     * 相机权限未授权。
     */
    C90008(90008, "相机权限未授权"),

    /**
     * 麦克风权限未授权。
     */
    C90009(90009, "麦克风权限未授权"),

    /**
     * 相机和麦克风权限都未授权。
     */
    C90010(90010, "相机和麦克风权限都未授权"),

    /**
     * 人脸数据采集无效。
     */
    C90011(90011, "人脸数据采集无效"),

    /**
     * 网络错误上传失败。
     */
    C90012(90012, "网络错误上传失败"),

    /**
     * 人脸数据采集无效。
     */
    C90013(90013, "人脸数据采集无效"),

    /**
     * 人脸数据采集无效。
     */
    C90014(90014, "人脸数据采集无效"),

    /**
     * 识别过程超时。
     */
    C90017(90017, "识别过程超时"),

    /**
     * 系统错误。
     */
    C90018(90018, "系统错误"),

    /**
     * 获取人脸配置失败。
     */
    C90104(90104, "获取人脸配置失败"),

    /**
     * 获取确认数据失败。
     */
    C90105(90105, "获取确认数据失败"),

    /**
     * 相机失败。
     */
    C90106(90106, "相机失败"),

    /**
     * 用户检测超时。
     */
    C90107(90107, "用户检测超时"),

    /**
     * 设备不支持人脸检测。
     */
    C90109(90109, "设备不支持人脸检测"),

    /**
     * 获取协议信息失败。
     */
    C90110(90110, "获取协议信息失败"),

    /**
     * 用户系统错误。
     */
    C90199(90199, "用户系统错误");

    private static final Map<Integer, WechatXcxFaceVerifyResult> VALUE_MAP = new HashMap<>();

    static {
        for (WechatXcxFaceVerifyResult result : values()) {
            VALUE_MAP.put(result.value, result);
        }
    }

    private final int value;

    private final String description;

    WechatXcxFaceVerifyResult(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int getValue() {
        return value;
    }

    /**
     * 获取结果描述。
     *
     * @return 结果描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据整型值获取对应的枚举实例。
     *
     * @param value 验证结果值
     * @return 对应的枚举实例，如果未找到则返回 null
     */
    public static WechatXcxFaceVerifyResult of(Integer value) {
        return value == null ? null : VALUE_MAP.get(value);
    }

    /**
     * 判断是否核身通过。
     *
     * @return 为识别成功时返回 true
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
