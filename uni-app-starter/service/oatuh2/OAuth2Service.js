/**
 * uni-app OAuth 登录提供商。
 */
export const OAuth2Provider = Object.freeze({
  /** 微信登录。 */
  WECHAT: 'weixin',
  /** QQ 登录。 */
  QQ: 'qq',
  /** Google 登录。 */
  GOOGLE: 'google',
  /** uni 一键登录。 */
  PHONE_NUMBER: 'univerify'
})

/**
 * uni-app OAuth 登录服务。
 * <p>微信、QQ 与 Google 登录依赖对应平台的 OAuth 配置；手机号一键登录仅支持已配置
 * univerify 服务的 App 端。登录成功后的 code 或 authResult 应提交给服务端完成用户登录。</p>
 */
export default class OAuth2Service {
  /**
   * 获取当前环境已配置的 OAuth 登录提供商。
   *
   * @returns {Promise<String[]>} 已配置的 OAuth 提供商编码。
   */
  static getAvailableProviders() {
    if (typeof uni === 'undefined' || typeof uni.getProvider !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.getProvider'))
    }
    return new Promise((resolve, reject) => {
      uni.getProvider({
        service: 'oauth',
        success: (result) => resolve(Array.isArray(result.provider) ? result.provider : []),
        fail: reject
      })
    })
  }

  /**
   * 判断指定 OAuth 登录提供商是否可用。
   *
   * @param {String} provider OAuth 登录提供商。
   * @returns {Promise<Boolean>} 是否已配置且可用。
   */
  static async isProviderAvailable(provider) {
    this.requireProvider(provider)
    return (await this.getAvailableProviders()).includes(provider)
  }

  /**
   * 发起微信登录。
   * <p>固定使用 onlyAuthorize，服务端应使用返回的 code 或 authResult 换取并管理用户凭据。</p>
   *
   * @param {Object} [options] 登录参数。
   * @param {Number} [options.timeout] 登录超时时间，单位毫秒。
   * @param {Function} [options.success] 登录成功回调。
   * @param {Function} [options.fail] 登录失败回调。
   * @param {Function} [options.complete] 登录完成回调。
   * @returns {Promise<Object>} uni.login 成功结果。
   */
  static loginByWechat(options = {}) {
    return this.login(OAuth2Provider.WECHAT, {
      ...options,
      onlyAuthorize: true
    })
  }

  /**
   * 发起 QQ 登录。
   *
   * @param {Object} [options] 登录参数。
   * @param {Number} [options.timeout] 登录超时时间，单位毫秒。
   * @param {Function} [options.success] 登录成功回调。
   * @param {Function} [options.fail] 登录失败回调。
   * @param {Function} [options.complete] 登录完成回调。
   * @returns {Promise<Object>} uni.login 成功结果。
   */
  static loginByQq(options = {}) {
    return this.login(OAuth2Provider.QQ, options)
  }

  /**
   * 发起 Google 登录。
   *
   * @param {Object} [options] 登录参数。
   * @param {Number} [options.timeout] 登录超时时间，单位毫秒。
   * @param {Function} [options.success] 登录成功回调。
   * @param {Function} [options.fail] 登录失败回调。
   * @param {Function} [options.complete] 登录完成回调。
   * @returns {Promise<Object>} uni.login 成功结果。
   */
  static loginByGoogle(options = {}) {
    return this.login(OAuth2Provider.GOOGLE, options)
  }

  /**
   * 发起手机号一键登录。
   * <p>调用前会执行 uni.preLogin 预取号；请在登录成功后将 authResult 中的登录凭据交给服务端校验。</p>
   *
   * @param {Object} [options] 登录参数。
   * @param {Object} [options.preLoginOptions] 预取号参数。
   * @param {Number} [options.timeout] 登录超时时间，单位毫秒。
   * @param {Function} [options.success] 登录成功回调。
   * @param {Function} [options.fail] 登录失败回调。
   * @param {Function} [options.complete] 登录完成回调。
   * @returns {Promise<Object>} uni.login 成功结果。
   */
  static async loginByPhoneNumber(options = {}) {
    this.requireObject(options, 'options')
    const { preLoginOptions, ...loginOptions } = options
    await this.preLogin(preLoginOptions || {})
    return this.login(OAuth2Provider.PHONE_NUMBER, loginOptions)
  }

  /**
   * 执行手机号一键登录预取号。
   *
   * @param {Object} [options] 预取号参数。
   * @param {Number} [options.timeout] 预取号超时时间，单位毫秒。
   * @param {Function} [options.success] 预取号成功回调。
   * @param {Function} [options.fail] 预取号失败回调。
   * @param {Function} [options.complete] 预取号完成回调。
   * @returns {Promise<Object>} uni.preLogin 成功结果。
   */
  static preLogin(options = {}) {
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.preLogin !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持手机号一键登录'))
    }
    const { success, fail, complete, ...requestOptions } = options
    return new Promise((resolve, reject) => {
      uni.preLogin({
        ...requestOptions,
        provider: OAuth2Provider.PHONE_NUMBER,
        success: (result) => {
          success?.(result)
          resolve(result)
        },
        fail: (error) => {
          fail?.(error)
          reject(error)
        },
        complete: (result) => complete?.(result)
      })
    })
  }

  /**
   * 使用指定 OAuth 提供商发起登录。
   *
   * @param {String} provider OAuth 登录提供商，使用 OAuth2Provider 常量。
   * @param {Object} [options] 登录参数。
   * @param {Number} [options.timeout] 登录超时时间，单位毫秒。
   * @param {Boolean} [options.onlyAuthorize] 是否仅授权，微信登录由 loginByWechat 固定开启。
   * @param {Function} [options.success] 登录成功回调。
   * @param {Function} [options.fail] 登录失败回调。
   * @param {Function} [options.complete] 登录完成回调。
   * @returns {Promise<Object>} uni.login 成功结果。
   */
  static login(provider, options = {}) {
    this.requireProvider(provider)
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.login !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.login'))
    }
    const { success, fail, complete, ...requestOptions } = options
    return new Promise((resolve, reject) => {
      uni.login({
        ...requestOptions,
        provider,
        success: (result) => {
          success?.(result)
          resolve(result)
        },
        fail: (error) => {
          fail?.(error)
          reject(error)
        },
        complete: (result) => complete?.(result)
      })
    })
  }

  /**
   * 校验 OAuth 提供商。
   *
   * @param {String} provider OAuth 提供商。
   */
  static requireProvider(provider) {
    if (!Object.values(OAuth2Provider).includes(provider)) {
      throw new Error(`不支持的 OAuth 登录提供商: ${provider}`)
    }
  }

  /**
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`OAuth 登录参数必须为对象: ${name}`)
    }
  }
}
