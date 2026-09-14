package com.hzj.wechat.core.xcx.subscribe_message;

import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardActivateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardExtUpdateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardStatusQueryRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardStatusResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCategoryRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCategoryResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageKeywordRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageKeywordResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessagePublicTemplateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessagePublicTemplateResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageResult;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageSendRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateAddRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateAddResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateDeleteRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateListRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateListResponse;

/**
 * 微信小程序订阅消息服务。
 * 封装订阅消息模板管理、公共模板查询、订阅消息下发以及服务卡片状态维护能力。
 */
public interface WechatXcxSubscribeMessageService {

    /**
     * 删除模板。
     * 删除私有模板库中的模板。
     *
     * @param request 删除模板请求参数，需包含要删除的模板 id
     * @return 微信接口调用结果
     */
    WechatXcxSubscribeMessageResult deleteTemplate(WechatXcxSubscribeMessageTemplateDeleteRequest request);

    /**
     * 获取类目。
     * 用于查询小程序所属类目，进而查询公共模板。
     *
     * @param request 获取类目请求参数
     * @return 类目列表
     */
    WechatXcxSubscribeMessageCategoryResponse getCategories(WechatXcxSubscribeMessageCategoryRequest request);

    /**
     * 获取类目。
     *
     * @return 类目列表
     */
    default WechatXcxSubscribeMessageCategoryResponse getCategories() {
        return getCategories(new WechatXcxSubscribeMessageCategoryRequest());
    }

    /**
     * 获取模板中的关键词。
     * 用于获取模板标题下的关键词列表。
     *
     * @param request 关键词查询请求参数，需包含模板标题 id
     * @return 关键词列表
     */
    WechatXcxSubscribeMessageKeywordResponse getTemplateKeywords(WechatXcxSubscribeMessageKeywordRequest request);

    /**
     * 获取类目下的公共模板。
     * 可从中选用模板使用。
     *
     * @param request 公共模板查询请求参数，需包含类目 id 与分页参数
     * @return 公共模板标题列表
     */
    WechatXcxSubscribeMessagePublicTemplateResponse getPublicTemplates(
            WechatXcxSubscribeMessagePublicTemplateRequest request);

    /**
     * 获取已有模板列表。
     * 用于获取当前账号下的已有模板列表。
     *
     * @param request 已有模板列表查询请求参数
     * @return 模板列表
     */
    WechatXcxSubscribeMessageTemplateListResponse getTemplateList(
            WechatXcxSubscribeMessageTemplateListRequest request);

    /**
     * 获取已有模板列表。
     *
     * @return 模板列表
     */
    default WechatXcxSubscribeMessageTemplateListResponse getTemplateList() {
        return getTemplateList(new WechatXcxSubscribeMessageTemplateListRequest());
    }

    /**
     * 发送订阅消息。
     *
     * @param request 发送订阅消息请求参数。
     *                其中 {@code miniprogramState} 与 {@code lang} 支持自动注入：
     *                如果请求对象自身已传值，则优先使用请求值；
     *                如果未传，则使用配置提供者的默认值，仍为空时使用正式版与简体中文。
     * @return 微信接口调用结果
     */
    WechatXcxSubscribeMessageResult send(WechatXcxSubscribeMessageSendRequest request);

    /**
     * 选用模板。
     * 从公共模板库中选用模板到私有模板库。
     *
     * @param request 选用模板请求参数，需包含模板标题 id、关键词列表与场景描述
     * @return 选用结果，包含添加至账号下的模板 id
     */
    WechatXcxSubscribeMessageTemplateAddResponse addTemplate(WechatXcxSubscribeMessageTemplateAddRequest request);

    /**
     * 激活与更新服务卡片。
     *
     * @param request 服务卡片激活与更新请求参数
     * @return 微信接口调用结果
     */
    WechatXcxSubscribeMessageResult activateServiceCard(WechatXcxSubscribeMessageCardActivateRequest request);

    /**
     * 更新服务卡片扩展信息。
     *
     * @param request 服务卡片扩展信息更新请求参数
     * @return 微信接口调用结果
     */
    WechatXcxSubscribeMessageResult updateServiceCardExtInfo(WechatXcxSubscribeMessageCardExtUpdateRequest request);

    /**
     * 查询服务卡片状态。
     *
     * @param request 服务卡片状态查询请求参数
     * @return 服务卡片状态
     */
    WechatXcxSubscribeMessageCardStatusResponse getServiceCardStatus(
            WechatXcxSubscribeMessageCardStatusQueryRequest request);
}
