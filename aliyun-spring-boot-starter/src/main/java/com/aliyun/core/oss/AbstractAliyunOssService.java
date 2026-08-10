package com.aliyun.core.oss;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.core.oss.exception.AliyunOssException;
import com.aliyun.core.oss.domain.AliyunMediaUploadDetails;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tika.Tika;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractAliyunOssService implements AliyunOssService {
    public static final String TEMP_SUFFIX = ".temp";
    protected final int DELETE_TRY_COUNT = 3; // 删除失败重试次数
    protected final long DELETE_TRY_WAIT_TIME = 10000L; // 删除失败重试间隔时间
    protected static final Tika TIKA = new Tika();
    /**
     * 默认媒体类型，用于无法识别文件类型的场景。
     */
    public final String DEFAULT_MEDIA_TYPE = "application/octet-stream";

    /**
     * 上传文件到 OSS（通过文件路径）
     * @param path 本地文件路径
     * @return 上传成功后的媒体文件详情
     * @throws AliyunOssException 当文件路径为空时抛出
     */
    public AliyunMediaUploadDetails upload(String path) {
        if (StrUtil.isEmpty(path)){
            throw AliyunOssException.UPLOAD_FILE_PATH_NOT_NULL;
        }
        return upload(new File(path));
    }

    /**
     * 上传文件到 OSS（通过 File 对象）
     * @param file 要上传的文件对象
     * @return 上传成功后的媒体文件详情
     * @throws AliyunOssException 当文件为空时抛出
     */
    public AliyunMediaUploadDetails upload(File file) {
        if (FileUtil.isEmpty(file)){
            throw AliyunOssException.UPLOAD_FILE_NOT_NULL;
        }
        try {
            String originFileName = file.getName();
            InputStream inputStream = new FileInputStream(file);
            return upload(originFileName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据内容类型获取对应的存储桶名称。
     *
     * @param contentType 文件的内容类型（MIME 类型）
     * @return 存储桶名称
     */
    public abstract String getBucket(String contentType);

    /**
     * 获取当前日期格式化后的目录路径，格式为：年/月/日/
     *
     * @return 当前日期组成的目录字符串
     */
    protected static String getDirectory() {
        LocalDateTime localDateTime = LocalDateTime.now();
        // 获取当前年份
        int year = localDateTime.getYear();
        // 获取当前月份
        int month = localDateTime.getMonthValue();
        // 获取当前日期
        int day = localDateTime.getDayOfMonth();
        return year + "/" + month + "/" + day + "/";
    }

    /**
     * 验证目标文件与原始文件的 MD5 值是否一致以确保完整性。
     *
     * @param originFileMd5  原始文件的 MD5 值
     * @param targetFilePath 目标文件路径
     * @return 若校验成功则返回 MD5 字符串，否则返回 null
     * @throws IOException IO 异常
     */
    protected String validateFileIntegrity(String originFileMd5, String targetFilePath) throws IOException {
        InputStream targetFileInputStream = new FileInputStream(targetFilePath);
        try {
            if (originFileMd5.equals(DigestUtils.md5Hex(targetFileInputStream))) {
                return originFileMd5;
            }
            return null;
        } finally {
            IoUtil.close(targetFileInputStream);
        }
    }

    /**
     * 比较两个文件的 MD5 值来验证其完整性。
     *
     * @param originFile 原始文件对象
     * @param targetFile 目标文件对象
     * @return 若校验成功则返回 MD5 字符串，否则返回 null
     * @throws IOException IO 异常
     */
    protected String validateFileIntegrity(File originFile, File targetFile) throws IOException {
        // 原始文件流
        InputStream originalStream = new FileInputStream(originFile);
        // 目标文件流
        InputStream targetStream = new FileInputStream(targetFile);
        return validateFileIntegrity(originalStream, targetStream);
    }

    /**
     * 比较两个输入流的 MD5 值来验证其完整性。
     *
     * @param originStream 原始数据流
     * @param targetStream 目标数据流
     * @return 若校验成功则返回 MD5 字符串，否则返回 null
     * @throws IOException IO 异常
     */
    protected String validateFileIntegrity(InputStream originStream, InputStream targetStream) throws IOException {
        try {
            String originMd5 = DigestUtils.md5Hex(originStream);
            String targetMd5 = DigestUtils.md5Hex(targetStream);
            if (originMd5.equals(targetMd5)) {
                return originMd5;
            }
            return null;
        } finally {
            IoUtil.close(originStream);
            IoUtil.close(targetStream);
        }
    }

    /**
     * 下载输入流中的内容并保存为临时文件，默认后缀为 .temp。
     *
     * @param inputStream 输入流
     * @return 保存后的文件对象
     */
    protected File download(InputStream inputStream) {
        return download(inputStream, null, TEMP_SUFFIX);
    }

    /**
     * 使用指定文件对象将输入流中的内容写入磁盘。
     *
     * @param inputStream 输入流
     * @param file        要写入的目标文件对象
     * @return 写入完成的文件对象
     */
    protected File download(InputStream inputStream, File file) {
        return download(inputStream, file, TEMP_SUFFIX);
    }

    /**
     * 使用指定后缀名创建临时文件，并将输入流中的内容写入该文件中。
     *
     * @param inputStream 输入流
     * @param suffix      文件后缀名
     * @return 写入完成的文件对象
     */
    protected File download(InputStream inputStream, String suffix) {
        return download(inputStream, null, suffix);
    }

    /**
     * 将输入流中的内容写入到指定文件或新建临时文件中。
     *
     * @param inputStream 输入流
     * @param file        可选的目标文件对象；若为空，则自动创建一个临时文件
     * @param suffix      新建临时文件时使用的后缀名
     * @return 写入完成的文件对象
     */
    protected File download(InputStream inputStream, File file, String suffix) {
        try {
            if (file == null) {
                file = File.createTempFile(UUID.randomUUID().toString(), suffix);
            }
            // 将用户传递的文件写出到本地
            OutputStream outputStream = new FileOutputStream(file);
            IoUtil.copy(inputStream, outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
            return file; // 返回文件信息
        } catch (IOException e) {
            log.error("AbstractMediaService download 文件下载到本地异常：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字节大小转换为 MB 单位表示。
     *
     * @param size 字节数
     * @return 对应的 MB 大小（保留两位小数）
     */
    public double getFileSizeInMB(long size) {
        return size / (1024.0 * 1024.0);
    }


    /**
     * 提取文件名中的后缀部分。
     *
     * @param fileName 完整文件名
     * @return 后缀名（包括点号），如 ".txt"；如果无后缀或为 null 则返回空字符串
     */
    public String getFileSuffix(String fileName) {
        if (fileName == null) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        return fileName.substring(lastIndexOf);
    }


    /**
     * 获取文件的内容类型（MIME类型）
     *
     * @return 文件的内容类型字符串，如果文件不存在则返回octet-stream类型
     */
    public String getFileContentType(InputStream inputStream) {
        try {
            // 使用Tika库检测文件内容类型
            return TIKA.detect(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件的内容类型（MIME类型）
     *
     * @return 文件的内容类型字符串，如果文件不存在则返回octet-stream类型
     */
    public String getFileContentType(File file) {
        try (FileInputStream contentInputStream = new FileInputStream(file)) {
            return getFileContentType(contentInputStream);
        } catch (IOException e) {
            throw AliyunOssException.FILE_READ_ERROR;
        }
    }

    /**
     * 根据文件名获取文件的内容类型
     *
     * @param fileName 文件名
     * @return 文件的内容类型字符串
     */
    public String getFileContentTypeByFileName(String fileName) {
        return TIKA.detect(fileName);
    }


    /**
     * 获取多媒体文件的时长
     *
     * @param path 多媒体文件路径
     * @return 文件时长（毫秒），获取失败时返回null
     */
    public Long getDuration(File path) {
        // 创建多媒体对象
        MultimediaObject multimediaObject = new MultimediaObject(path);
        try {
            // 获取多媒体信息并返回时长
            MultimediaInfo multimediaInfo = multimediaObject.getInfo();
            return multimediaInfo.getDuration();
        } catch (EncoderException e) {
            log.error("AbstractMediaService getDuration 获取文件时长失败 原因:{} 文件路径:{}", e.getMessage(), path.getAbsolutePath());
            return null;
        }
    }


    /**
     * 删除指定路径的文件
     *
     * @param path 文件路径
     */
    public boolean deleteFile(String path) {
        if (StrUtil.isEmptyIfStr(path)) {
            log.error("AbstractMediaService deleteFile 文件路径为空");
            return true;
        }
        File file = new File(path);
        return retryDeleteFile(file);
    }

    /**
     * 删除指定文件
     *
     * @param file 要删除的文件对象，如果为null或文件不存在则不会执行删除操作
     */
    public boolean deleteFile(File file) {
        // 检查文件是否存在且不为null
        if (file == null || !file.exists()) {
            log.error("AbstractMediaService deleteFile 文件不存在或为空");
            return true;
        }
        // 执行文件删除操作
        return retryDeleteFile(file);
    }

    /**
     * 创建临时文件路径
     *
     * @return 返回完整的临时文件路径字符串，格式为：临时目录路径 + 随机文件名.tmp
     */
    public String createTempPath(){
        return createTempPath(TEMP_SUFFIX);
    }

    public String createTempPath(String suffix){
        // 1：得到临时目录
        String property = System.getProperty("java.io.tmpdir");
        // 2：构建文件名称
        String fileName = UUID.randomUUID() + suffix;
        return property + fileName;
    }



    /**
     * 删除指定文件，支持重试机制
     *
     * @param file 要删除的文件对象
     */
    private boolean retryDeleteFile(File file) {
        boolean deleted = file.delete();
        if (deleted) {
            log.info("AbstractMediaService deleteFile 删除文件成功 文件路径:{}", file.getAbsolutePath());
            return true;
        } else {
            CompletableFuture.runAsync(() -> {
                int retryCount = 0;
                // 循环重试删除文件，直到成功或超过最大重试次数
                while (retryCount < DELETE_TRY_COUNT) {
                    boolean res = file.delete();
                    if (!res) {
                        retryCount++;
                        log.warn("AbstractMediaService deleteFile 删除文件失败 正在进行第{}次重试 文件路径:{}", retryCount, file.getAbsolutePath());
                        try {
                            Thread.sleep(DELETE_TRY_WAIT_TIME * retryCount); // 递增延迟
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    } else {
                        log.info("AbstractMediaService deleteFile 删除文件成功 文件路径:{}", file.getAbsolutePath());
                        return;
                    }
                }
                log.warn("AbstractMediaService deleteFile 删除文件失败 超过最大重试次数 文件路径:{}", file.getAbsolutePath());
                file.deleteOnExit();
            });
            return false;
        }
    }

    /**
     * 通过指定有效的时长（秒）生成过期时间。
     * @param seconds 有效时长（秒）。
     * @return ISO8601 时间字符串，如："2014-12-01T12:00:00.000Z"。
     */
    public  String generateExpiration(long seconds) {
        // 获取当前时间戳（以秒为单位）
        long now = Instant.now().getEpochSecond();
        // 计算过期时间的时间戳
        long expirationTime = now + seconds;
        // 将时间戳转换为Instant对象，并格式化为ISO8601格式
        Instant instant = Instant.ofEpochSecond(expirationTime);
        // 定义时区为UTC
        ZoneId zone = ZoneOffset.UTC;
        // 将 Instant 转换为 ZonedDateTime
        ZonedDateTime zonedDateTime = instant.atZone(zone);
        // 定义日期时间格式，例如2023-12-03T13:00:00.000Z
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        // 格式化日期时间
        // 输出结果
        return zonedDateTime.format(formatter);
    }


}
