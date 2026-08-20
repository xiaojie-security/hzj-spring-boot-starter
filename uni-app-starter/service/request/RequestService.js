import ConfigService from '../config/ConfigService.js'
import DynamicRequestDataProvider from './DynamicRequestDataProvider.js'

/**
 * uni-app HTTP 请求服务。
 */
export default class RequestService {
  /** 默认请求超时时间，单位毫秒。 */
  static get DEFAULT_TIMEOUT() {
    return 10000
  }

  /**
   * 配置全局请求参数。
   *
   * @param {Object} options 全局配置。
   * @param {String} [options.baseUrl] 接口基础地址。
   * @param {Object} [options.header] 默认请求头。
   * @param {Number} [options.timeout] 请求超时时间，单位毫秒。
   */
  static configure(options) {
    this.requireObject(options, 'options')
    const defaultOptions = this.getDefaultOptions()
    this.defaultOptions = {
      ...defaultOptions,
      ...options,
      header: {
        ...(defaultOptions.header || {}),
        ...(options.header || {})
      }
    }
  }

  /**
   * 发起 GET 请求。
   *
   * @param {String} url 请求路径或完整地址。
   * @param {Object} [data] 查询参数。
   * @param {Object} [options] 额外请求参数。
   * @returns {Promise<Object>} uni.request 响应。
   */
  static get(url, data = undefined, options = {}) {
    return this.request({ ...options, url, data, method: 'GET' })
  }

  /**
   * 发起 POST 请求。
   *
   * @param {String} url 请求路径或完整地址。
   * @param {*} [data] 请求体。
   * @param {Object} [options] 额外请求参数。
   * @returns {Promise<Object>} uni.request 响应。
   */
  static post(url, data = undefined, options = {}) {
    return this.request({ ...options, url, data, method: 'POST' })
  }

  /**
   * 发起 PUT 请求。
   *
   * @param {String} url 请求路径或完整地址。
   * @param {*} [data] 请求体。
   * @param {Object} [options] 额外请求参数。
   * @returns {Promise<Object>} uni.request 响应。
   */
  static put(url, data = undefined, options = {}) {
    return this.request({ ...options, url, data, method: 'PUT' })
  }

  /**
   * 发起 DELETE 请求。
   *
   * @param {String} url 请求路径或完整地址。
   * @param {*} [data] 请求参数。
   * @param {Object} [options] 额外请求参数。
   * @returns {Promise<Object>} uni.request 响应。
   */
  static delete(url, data = undefined, options = {}) {
    return this.request({ ...options, url, data, method: 'DELETE' })
  }

  /**
   * 发起 HTTP 请求。
   *
   * @param {Object} options uni.request 参数。
   * @param {String} options.url 请求路径或完整地址。
   * @param {String} [options.method=GET] HTTP 方法。
   * @param {Object} [options.header] 请求头。
   * @param {Number} [options.timeout] 超时时间，单位毫秒。
   * @param {Boolean} [options.validateStatus=true] 是否将非 2xx 响应视为异常。
   * @returns {Promise<Object>} uni.request 响应。
   */
  static async request(options) {
    this.requireObject(options, 'options')
    this.requireText(options.url, 'url')
    if (typeof uni === 'undefined' || typeof uni.request !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.request'))
    }
    const mergedOptions = await this.mergeOptions(options)
    const { success, fail, complete, validateStatus, baseUrl, ...requestOptions } = mergedOptions
    this.logRequest(requestOptions)
    return new Promise((resolve, reject) => {
      uni.request({
        ...requestOptions,
        success: (response) => {
          if (validateStatus && !this.isSuccessStatus(response.statusCode)) {
            const error = new Error(`HTTP 请求失败: ${response.statusCode}`)
            error.response = response
            this.logResponse(requestOptions, response, error)
            fail?.(error)
            reject(error)
            return
          }
          this.logResponse(requestOptions, response)
          success?.(response)
          resolve(response)
        },
        fail: (error) => {
          this.logError(requestOptions, error)
          fail?.(error)
          reject(error)
        },
        complete: (result) => {
          complete?.(result)
        }
      })
    })
  }

  /**
   * 发起请求并直接返回响应业务数据。
   *
   * @param {Object} options uni.request 参数。
   * @returns {Promise<*>} 响应 data 字段。
   */
  static async requestData(options) {
    const response = await this.request(options)
    return response.data
  }

  /**
   * 拼接请求地址。
   *
   * @param {String} url 请求路径或完整地址。
   * @param {String} [baseUrl] 接口基础地址。
   * @returns {String} 完整请求地址。
   */
  static resolveUrl(url, baseUrl = undefined) {
    this.requireText(url, 'url')
    if (/^(https?|wss?):\/\//i.test(url)) {
      return ConfigService.applySslProtocol(url)
    }
    const resolvedBaseUrl = ConfigService.applySslProtocol(
      baseUrl ?? this.getDefaultOptions().baseUrl ?? ConfigService.getRequestBaseUrl()
    )
    const requestPath = this.joinPath(ConfigService.getRequestPrefix(), url)
    if (!resolvedBaseUrl) {
      return requestPath
    }
    return this.joinPath(resolvedBaseUrl, requestPath)
  }

  /**
   * 合并默认请求参数、环境配置和本次请求参数。
   *
   * @param {Object} options 本次请求参数。
   * @returns {Object} 合并后的请求参数。
   */
  static async mergeOptions(options) {
    const defaults = this.getDefaultOptions()
    const timeout = options.timeout ?? defaults.timeout ?? ConfigService.getNumber('REQUEST_TIMEOUT', this.DEFAULT_TIMEOUT)
    const url = this.resolveUrl(options.url, options.baseUrl ?? defaults.baseUrl)
    const method = (options.method || 'GET').toUpperCase()
    const dynamicRequestData = await DynamicRequestDataProvider.getRequestData({
      url,
      method,
      data: options.data
    })
    return {
      ...defaults,
      ...options,
      url,
      method,
      timeout,
      validateStatus: options.validateStatus ?? defaults.validateStatus ?? true,
      header: {
        ...ConfigService.getJson('REQUEST_HEADERS', {}),
        ...(defaults.header || {}),
        ...this.createCommonHeaders(dynamicRequestData),
        ...(options.header || {})
      }
    }
  }

  /**
   * 根据配置的请求头名称组装公共请求头。
   *
   * @param {Object} [values] 公共请求头值。
   * @returns {Object} 公共请求头。
   */
  static createCommonHeaders(values = {}) {
    this.requireObject(values, 'commonHeaderValues')
    const headerNames = ConfigService.getRequestHeaderNames()
    return Object.entries(headerNames).reduce((headers, [key, headerName]) => {
      const value = values[key]
      if (typeof headerName === 'string' && headerName.trim().length > 0 && value !== undefined && value !== null && value !== '') {
        headers[headerName] = value
      }
      return headers
    }, {})
  }

  /**
   * 拼接两个 URL 或路径片段。
   *
   * @param {String} first 第一个片段。
   * @param {String} second 第二个片段。
   * @returns {String} 拼接后的 URL 或路径。
   */
  static joinPath(first, second) {
    const left = typeof first === 'string' ? first.replace(/\/+$/, '') : ''
    const right = typeof second === 'string' ? second.replace(/^\/+/, '') : ''
    if (!left) {
      return second || ''
    }
    if (!right) {
      return left
    }
    return `${left}/${right}`
  }

  /**
   * 记录请求日志。
   *
   * @param {Object} requestOptions 请求参数。
   */
  static logRequest(requestOptions) {
    if (!ConfigService.isRequestLogEnabled() || typeof console === 'undefined') {
      return
    }
    console.info('[Request]', {
      method: requestOptions.method,
      url: requestOptions.url,
      header: this.maskSensitiveHeaders(requestOptions.header)
    })
  }

  /**
   * 记录响应日志。
   *
   * @param {Object} requestOptions 请求参数。
   * @param {Object} response 响应参数。
   * @param {Error} [error] 异常对象。
   */
  static logResponse(requestOptions, response, error = undefined) {
    if (!ConfigService.isRequestLogEnabled() || typeof console === 'undefined') {
      return
    }
    const logMethod = error ? console.warn : console.info
    logMethod('[Response]', {
      method: requestOptions.method,
      url: requestOptions.url,
      statusCode: response.statusCode,
      error: error?.message
    })
  }

  /**
   * 记录请求异常日志。
   *
   * @param {Object} requestOptions 请求参数。
   * @param {*} error 异常对象。
   */
  static logError(requestOptions, error) {
    if (!ConfigService.isRequestLogEnabled() || typeof console === 'undefined') {
      return
    }
    console.warn('[Request Error]', {
      method: requestOptions.method,
      url: requestOptions.url,
      error
    })
  }

  /**
   * 掩码敏感请求头。
   *
   * @param {Object} header 请求头。
   * @returns {Object} 脱敏后的请求头。
   */
  static maskSensitiveHeaders(header = {}) {
    const configuredHeaders = ConfigService.getRequestHeaderNames()
    const sensitiveNames = [configuredHeaders.authorization, configuredHeaders.refreshToken]
      .filter((name) => typeof name === 'string' && name.trim().length > 0)
      .map((name) => name.toLowerCase())
    return Object.entries(header).reduce((result, [name, value]) => {
      const normalizedName = name.toLowerCase()
      result[name] = sensitiveNames.includes(normalizedName) || /authorization|token/i.test(name) ? '***' : value
      return result
    }, {})
  }

  /**
   * 判断 HTTP 状态码是否成功。
   *
   * @param {Number} statusCode HTTP 状态码。
   * @returns {Boolean} 是否成功。
   */
  static isSuccessStatus(statusCode) {
    return typeof statusCode === 'number' && statusCode >= 200 && statusCode < 300
  }

  /**
   * 获取默认请求参数。
   *
   * @returns {Object} 默认请求参数。
   */
  static getDefaultOptions() {
    return this.defaultOptions || {}
  }

  /**
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`请求参数必须为对象: ${name}`)
    }
  }

  /**
   * 校验必填字符串参数。
   *
   * @param {String} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireText(value, name) {
    if (typeof value !== 'string' || value.trim().length === 0) {
      throw new Error(`请求参数不能为空: ${name}`)
    }
  }
}
