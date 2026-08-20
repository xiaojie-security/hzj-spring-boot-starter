/**
 * uni-app Vite 环境配置服务。
 * <p>配置项应定义在 .env、.env.development 或 .env.production 中，并使用 VITE_ 前缀。</p>
 */
export default class ConfigService {
  /**
   * 获取配置。
   *
   * @param {String} key 配置键，支持传入 API_BASE_URL 或 VITE_API_BASE_URL。
   * @param {*} [defaultValue] 未配置时的默认值。
   * @returns {*} 配置值。
   */
  static get(key, defaultValue = undefined) {
    const configKey = this.resolveKey(key)
    const runtimeValue = this.getRuntimeConfig()[configKey]
    if (runtimeValue !== undefined && runtimeValue !== null && runtimeValue !== '') {
      return runtimeValue
    }
    const environmentValue = import.meta.env?.[configKey]
    return environmentValue === undefined || environmentValue === null || environmentValue === ''
      ? defaultValue
      : environmentValue
  }

  /**
   * 获取布尔配置。
   *
   * @param {String} key 配置键。
   * @param {Boolean} [defaultValue=false] 默认值。
   * @returns {Boolean} 布尔配置值。
   */
  static getBoolean(key, defaultValue = false) {
    const value = this.get(key)
    if (value === undefined) {
      return defaultValue
    }
    if (typeof value === 'boolean') {
      return value
    }
    if (typeof value === 'string') {
      const normalizedValue = value.trim().toLowerCase()
      if (normalizedValue === 'true' || normalizedValue === '1') {
        return true
      }
      if (normalizedValue === 'false' || normalizedValue === '0') {
        return false
      }
    }
    return defaultValue
  }

  /**
   * 获取数值配置。
   *
   * @param {String} key 配置键。
   * @param {Number} [defaultValue] 默认值。
   * @returns {Number|undefined} 数值配置值。
   */
  static getNumber(key, defaultValue = undefined) {
    const value = this.get(key)
    if (value === undefined) {
      return defaultValue
    }
    const numberValue = Number(value)
    return Number.isFinite(numberValue) ? numberValue : defaultValue
  }

  /**
   * 获取 JSON 配置。
   *
   * @param {String} key 配置键。
   * @param {*} [defaultValue] 默认值。
   * @returns {*} 解析后的 JSON 配置值。
   */
  static getJson(key, defaultValue = undefined) {
    const value = this.get(key)
    if (value === undefined) {
      return defaultValue
    }
    if (typeof value === 'object') {
      return value
    }
    try {
      return JSON.parse(value)
    } catch (error) {
      return defaultValue
    }
  }

  /**
   * 获取当前运行环境编码。
   *
   * @returns {String|undefined} 运行环境编码。
   */
  static getOperatingEnvironment() {
    return this.get('OPERATING_ENVIRONMENT')
  }

  /**
   * 判断当前运行环境配置是否合法。
   *
   * @returns {Boolean} 当前运行环境是否为受支持的环境编码。
   */
  static isOperatingEnvironmentValid() {
    return isValidOperatingEnvironment(this.getOperatingEnvironment())
  }

  /**
   * 校验当前运行环境配置。
   *
   * @returns {String} 已校验的运行环境编码。
   */
  static requireOperatingEnvironment() {
    const operatingEnvironment = this.getOperatingEnvironment()
    if (!isValidOperatingEnvironment(operatingEnvironment)) {
      throw new Error(`不支持的运行环境: ${operatingEnvironment}`)
    }
    return operatingEnvironment
  }

  /**
   * 是否记录请求日志。
   *
   * @returns {Boolean} 是否记录请求日志。
   */
  static isRequestLogEnabled() {
    return this.getBoolean('REQUEST_LOG_ENABLED', false)
  }

  /**
   * 是否启用 SSL。
   * <p>启用后，HTTP 地址会升级为 HTTPS，WebSocket 地址会升级为 WSS。</p>
   *
   * @returns {Boolean} 是否启用 SSL。
   */
  static isRequestSslEnabled() {
    return this.getBoolean('REQUEST_SSL_ENABLED', false)
  }

  /**
   * 获取请求基础地址。
   *
   * @returns {String} 请求基础地址。
   */
  static getRequestBaseUrl() {
    const baseUrl = this.get('REQUEST_BASE_URL', this.get('API_BASE_URL', ''))
    if (typeof baseUrl !== 'string' || baseUrl.trim().length === 0) {
      return ''
    }
    const normalizedBaseUrl = baseUrl.trim()
    if (/^(https?|wss?):\/\//i.test(normalizedBaseUrl)) {
      return this.applySslProtocol(normalizedBaseUrl)
    }
    if (normalizedBaseUrl.startsWith('//')) {
      return `${this.isRequestSslEnabled() ? 'https:' : 'http:'}${normalizedBaseUrl}`
    }
    return `${this.isRequestSslEnabled() ? 'https' : 'http'}://${normalizedBaseUrl}`
  }

  /**
   * 获取请求公共前缀。
   *
   * @returns {String} 请求公共前缀。
   */
  static getRequestPrefix() {
    const prefix = this.get('REQUEST_PREFIX', '')
    if (typeof prefix !== 'string' || prefix.trim().length === 0) {
      return ''
    }
    return `/${prefix.trim().replace(/^\/+|\/+$/g, '')}`
  }

  /**
   * 获取公共请求头名称。
   * <p>空配置不会携带对应请求头。</p>
   *
   * @returns {Object} 请求头名称映射。
   */
  static getRequestHeaderNames() {
    return {
      timestamp: this.get('REQUEST_TIMESTAMP_HEADER', ''),
      authorization: this.get('REQUEST_AUTHORIZATION_HEADER', ''),
      nonce: this.get('REQUEST_NONCE_HEADER', ''),
      deviceId: this.get('REQUEST_DEVICE_ID_HEADER', ''),
      refreshToken: this.get('REQUEST_REFRESH_TOKEN_HEADER', '')
    }
  }

  /**
   * 根据 SSL 开关升级 URL 协议。
   *
   * @param {String} url 原始地址。
   * @returns {String} 处理后的地址。
   */
  static applySslProtocol(url) {
    if (typeof url !== 'string' || url.trim().length === 0 || !this.isRequestSslEnabled()) {
      return url
    }
    return url.trim()
      .replace(/^http:/i, 'https:')
      .replace(/^ws:/i, 'wss:')
  }

  /**
   * 设置运行时配置。运行时配置优先级高于构建环境配置。
   *
   * @param {Object} config 配置对象。
   */
  static configure(config) {
    this.requireObject(config, 'config')
    Object.entries(config).forEach(([key, value]) => {
      this.getRuntimeConfig()[this.resolveKey(key)] = value
    })
  }

  /**
   * 移除运行时配置，恢复使用构建环境配置。
   *
   * @param {String} key 配置键。
   */
  static removeRuntimeConfig(key) {
    delete this.getRuntimeConfig()[this.resolveKey(key)]
  }

  /**
   * 清空全部运行时配置。
   */
  static clearRuntimeConfig() {
    this.runtimeConfig = Object.create(null)
  }

  /**
   * 获取运行时配置容器。
   *
   * @returns {Object} 运行时配置容器。
   */
  static getRuntimeConfig() {
    if (!this.runtimeConfig) {
      this.runtimeConfig = Object.create(null)
    }
    return this.runtimeConfig
  }

  /**
   * 将业务键标准化为 Vite 客户端变量名。
   *
   * @param {String} key 配置键。
   * @returns {String} Vite 环境变量名。
   */
  static resolveKey(key) {
    if (typeof key !== 'string' || key.trim().length === 0) {
      throw new Error('配置键不能为空')
    }
    const normalizedKey = key.trim()
    return normalizedKey.startsWith('VITE_') ? normalizedKey : `VITE_${normalizedKey}`
  }

  /**
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`配置参数必须为对象: ${name}`)
    }
  }
}
import { isValidOperatingEnvironment } from './OperatingEnvironment.js'
