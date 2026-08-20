import ConfigService from '../config/ConfigService.js'
import RequestService from '../request/RequestService.js'

/**
 * uni-app 网络文件服务。
 */
export default class FileService {
  /** 默认文件上传字段名。 */
  static get DEFAULT_UPLOAD_NAME() {
    return 'file'
  }

  /**
   * 上传单个文件。
   *
   * @param {String} url 上传路径或完整地址。
   * @param {String} filePath 本地文件路径。
   * @param {Object} [options] 上传参数。
   * @param {String} [options.name=file] 服务端文件字段名。
   * @param {Object} [options.formData] 额外表单字段。
   * @param {Object} [options.header] 请求头。
   * @param {Number} [options.timeout] 超时时间，单位毫秒。
   * @returns {Promise<Object>} uni.uploadFile 响应，响应数据位于 data 字段。
   */
  static upload(url, filePath, options = {}) {
    this.requireText(url, 'url')
    this.requireText(filePath, 'filePath')
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.uploadFile !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.uploadFile'))
    }
    const { success, fail, complete, header, timeout, name, baseUrl, ...uploadOptions } = options
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        ...uploadOptions,
        url: RequestService.resolveUrl(url, baseUrl),
        filePath,
        name: name || this.DEFAULT_UPLOAD_NAME,
        header: this.mergeHeaders(header),
        timeout: timeout ?? ConfigService.getNumber('FILE_UPLOAD_TIMEOUT', ConfigService.getNumber('REQUEST_TIMEOUT', 10000)),
        success: (response) => {
          if (!RequestService.isSuccessStatus(response.statusCode)) {
            const error = new Error(`文件上传失败: ${response.statusCode}`)
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
   * 下载文件。
   *
   * @param {String} url 下载路径或完整地址。
   * @param {Object} [options] 下载参数。
   * @param {Object} [options.header] 请求头。
   * @param {String} [options.filePath] 指定下载保存路径。
   * @param {Number} [options.timeout] 超时时间，单位毫秒。
   * @returns {Promise<Object>} uni.downloadFile 响应，临时路径位于 tempFilePath 字段。
   */
  static download(url, options = {}) {
    this.requireText(url, 'url')
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.downloadFile !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.downloadFile'))
    }
    const { success, fail, complete, header, timeout, baseUrl, ...downloadOptions } = options
    return new Promise((resolve, reject) => {
      uni.downloadFile({
        ...downloadOptions,
        url: RequestService.resolveUrl(url, baseUrl),
        header: this.mergeHeaders(header),
        timeout: timeout ?? ConfigService.getNumber('FILE_DOWNLOAD_TIMEOUT', ConfigService.getNumber('REQUEST_TIMEOUT', 10000)),
        success: (response) => {
          if (!RequestService.isSuccessStatus(response.statusCode)) {
            const error = new Error(`文件下载失败: ${response.statusCode}`)
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
   * 下载并持久化保存文件。
   *
   * @param {String} url 下载路径或完整地址。
   * @param {Object} [options] 下载参数。
   * @returns {Promise<Object>} 下载响应与 savedFilePath。
   */
  static async downloadAndSave(url, options = {}) {
    const response = await this.download(url, options)
    const savedFilePath = await this.save(response.tempFilePath)
    return {
      ...response,
      savedFilePath
    }
  }

  /**
   * 预览图片。
   *
   * @param {Array<String>} urls 图片地址或本地路径集合。
   * @param {String|Number} [current] 当前展示的图片地址或下标。
   * @param {Object} [options] uni.previewImage 额外参数。
   * @returns {Promise<Object>} uni.previewImage 回调结果。
   */
  static previewImage(urls, current = undefined, options = {}) {
    if (!Array.isArray(urls) || urls.length === 0 || urls.some((url) => typeof url !== 'string' || url.trim().length === 0)) {
      throw new Error('图片预览参数 urls 必须是非空字符串数组')
    }
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.previewImage !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.previewImage'))
    }
    return this.invoke((requestOptions) => uni.previewImage(requestOptions), {
      ...options,
      urls,
      current
    })
  }

  /**
   * 使用系统应用打开文档。
   *
   * @param {String} filePath 本地文档路径。
   * @param {Object} [options] uni.openDocument 额外参数。
   * @returns {Promise<Object>} uni.openDocument 回调结果。
   */
  static openDocument(filePath, options = {}) {
    this.requireText(filePath, 'filePath')
    this.requireObject(options, 'options')
    if (typeof uni === 'undefined' || typeof uni.openDocument !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.openDocument'))
    }
    return this.invoke((requestOptions) => uni.openDocument(requestOptions), {
      ...options,
      filePath
    })
  }

  /**
   * 持久化保存临时文件。
   *
   * @param {String} tempFilePath 临时文件路径。
   * @returns {Promise<String>} 持久化文件路径。
   */
  static save(tempFilePath) {
    this.requireText(tempFilePath, 'tempFilePath')
    if (typeof uni === 'undefined' || typeof uni.saveFile !== 'function') {
      return Promise.reject(new Error('当前运行环境不支持 uni.saveFile'))
    }
    return new Promise((resolve, reject) => {
      uni.saveFile({
        tempFilePath,
        success: (result) => resolve(result.savedFilePath),
        fail: reject
      })
    })
  }

  /**
   * 合并文件请求头。
   *
   * @param {Object} [header] 本次请求头。
   * @returns {Object} 合并后的请求头。
   */
  static mergeHeaders(header = {}) {
    return {
      ...ConfigService.getJson('REQUEST_HEADERS', {}),
      ...(RequestService.getDefaultOptions().header || {}),
      ...header
    }
  }

  /**
   * 以 Promise 方式调用 uni-app API。
   *
   * @param {Function} api uni-app API。
   * @param {Object} options API 参数。
   * @returns {Promise<Object>} API 成功回调结果。
   */
  static invoke(api, options) {
    const { success, fail, complete, ...apiOptions } = options
    return new Promise((resolve, reject) => {
      api({
        ...apiOptions,
        success: (result) => {
          success?.(result)
          resolve(result)
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
   * 校验对象参数。
   *
   * @param {Object} value 参数值。
   * @param {String} name 参数名称。
   */
  static requireObject(value, name) {
    if (value === null || typeof value !== 'object' || Array.isArray(value)) {
      throw new Error(`文件参数必须为对象: ${name}`)
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
      throw new Error(`文件参数不能为空: ${name}`)
    }
  }
}
