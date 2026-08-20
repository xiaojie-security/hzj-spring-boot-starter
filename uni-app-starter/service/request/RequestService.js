import ConfigService from '../config/ConfigService.js'

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
  static request(options) {
    this.requireObject(options, 'options')
    this.requireText(options.url, 'url')
    if (typeof uni === 'undefined' || typeof uni.request !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.request'))
    }
    const mergedOptions = this.mergeOptions(options)
    const { success, fail, complete, validateStatus, baseUrl, ...requestOptions } = mergedOptions
    return new Promise((resolve, reject) => {
      uni.request({
        ...requestOptions,
        success: (response) => {
          if (validateStatus && !this.isSuccessStatus(response.statusCode)) {
            const error = new Error(`HTTP 请求失败: ${response.statusCode}`)
            error.response = response
            fail?.(error)
            reject(error)
            return
          }
          success?.(response)
          resolve(response)
        },
        fail: (error) => {
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
    if (/^(https?:)?\/\//i.test(url)) {
      return url
    }
    const resolvedBaseUrl = baseUrl ?? this.getDefaultOptions().baseUrl ?? ConfigService.get('API_BASE_URL', '')
    if (!resolvedBaseUrl) {
      return url
    }
    return `${resolvedBaseUrl.replace(/\/$/, '')}/${url.replace(/^\//, '')}`
  }

  /**
   * 合并默认请求参数、环境配置和本次请求参数。
   *
   * @param {Object} options 本次请求参数。
   * @returns {Object} 合并后的请求参数。
   */
  static mergeOptions(options) {
    const defaults = this.getDefaultOptions()
    const timeout = options.timeout ?? defaults.timeout ?? ConfigService.getNumber('REQUEST_TIMEOUT', this.DEFAULT_TIMEOUT)
    return {
      ...defaults,
      ...options,
      url: this.resolveUrl(options.url, options.baseUrl ?? defaults.baseUrl),
      method: (options.method || 'GET').toUpperCase(),
      timeout,
      validateStatus: options.validateStatus ?? defaults.validateStatus ?? true,
      header: {
        ...ConfigService.getJson('REQUEST_HEADERS', {}),
        ...(defaults.header || {}),
        ...(options.header || {})
      }
    }
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
