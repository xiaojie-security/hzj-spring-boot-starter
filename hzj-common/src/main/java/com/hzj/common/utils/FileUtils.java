package com.hzj.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


@Slf4j
public final class FileUtils extends FileUtil {
    /**
     * Tika单例，用于二进制内容识别真实MIME
     */
    private static final Tika TIKA = new Tika();

    // Excel
    public static final String MIME_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIME_XLS = "application/vnd.ms-excel";
    // PDF
    public static final String MIME_PDF = "application/pdf";
    // Word
    public static final String MIME_DOC = "application/msword";
    public static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    // ZIP压缩包
    public static final String MIME_ZIP = "application/zip";
    // 媒体前缀常量
    public static final String PREFIX_IMAGE = "image/";
    public static final String PREFIX_AUDIO = "audio/";
    public static final String PREFIX_VIDEO = "video/";

    /**
     * 格式化文件大小展示
     *
     * @param sizeInBytes 文件字节大小
     * @return 友好格式化字符串 如：1.20MB、256KB
     */
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes >= 1024L * 1024L * 1024L) {
            double sizeInGB = (double) sizeInBytes / (1024L * 1024L * 1024L);
            return String.format("%.2fGB", sizeInGB);
        } else if (sizeInBytes >= 1024L * 1024L) {
            double sizeInMB = (double) sizeInBytes / (1024L * 1024L);
            return String.format("%.2fMB", sizeInMB);
        } else if (sizeInBytes >= 1024L) {
            double sizeInKB = (double) sizeInBytes / (1024L);
            return String.format("%.2fKB", sizeInKB);
        } else {
            return sizeInBytes + "B";
        }
    }

    /**
     * 仅根据文件名推测MIME（仅参考，可被后缀篡改欺骗，不安全）
     *
     * @param fileName 文件名称
     * @return mime类型，空串代表无法识别
     */
    public static String getFileContentTypeByFileName(String fileName) {
        if (StrUtil.isBlank(fileName)) {
            return "";
        }
        return TIKA.detect(fileName);
    }

    /**
     * 获取带点文件后缀
     *
     * @param fileName 文件名称
     * @return .xlsx / .pdf 无后缀返回空字符串
     */
    public static String getFileSuffix(String fileName) {
        if (StrUtil.isEmpty(fileName)) {
            return "";
        }
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return fileName.substring(lastIndexOf);
    }

    /**
     * 根据输入流读取文件二进制魔数，获取真实MIME（最准确）
     * 注意：会消费传入的InputStream，如需重复读取请提前转byte数组
     *
     * @param inputStream 文件输入流
     * @param fileName    原始文件名（辅助Tika识别，可为空）
     * @return 真实mime，异常返回空串
     */
    public static String getRealContentTypeByStream(InputStream inputStream, String fileName) {
        if (inputStream == null) {
            return "";
        }
        Metadata metadata = new Metadata();
        if (StrUtil.isNotBlank(fileName)) {
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);
        }
        try (TikaInputStream tis = TikaInputStream.get(inputStream)) {
            return TIKA.detect(tis, metadata);
        } catch (IOException e) {
            log.error("FilesUtils.getRealContentTypeByStream 流识别文件MIME失败, fileName:{}", fileName, e);
            return "";
        }
    }

    /**
     * 根据字节数组识别真实MIME，可重复读取，无流消耗问题（推荐业务使用）
     *
     * @param fileBytes 文件完整字节数组
     * @param fileName  原始文件名（辅助识别）
     * @return mime类型，空串识别失败
     */
    public static String getRealContentTypeByBytes(byte[] fileBytes, String fileName) {
        if (fileBytes == null || fileBytes.length == 0) {
            return "";
        }
        try (InputStream bis = new ByteArrayInputStream(fileBytes)) {
            return getRealContentTypeByStream(bis, fileName);
        } catch (IOException e) {
            log.error("FilesUtils.getRealContentTypeByBytes 字节数组识别MIME失败, fileName:{}", fileName, e);
            return "";
        }
    }

    /**
     * 判断二进制内容是否为Excel（xls/xlsx）
     *
     * @param fileBytes 文件字节数组
     * @param fileName  文件原始名称
     * @return true=真实Excel文件
     */
    public static boolean isExcel(byte[] fileBytes, String fileName) {
        String mime = getRealContentTypeByBytes(fileBytes, fileName);
        return MIME_XLSX.equals(mime) || MIME_XLS.equals(mime);
    }

    public static boolean isExcel(InputStream inputStream, String fileName) {
        String mime = getRealContentTypeByStream(inputStream, fileName);
        return MIME_XLSX.equals(mime) || MIME_XLS.equals(mime);
    }

    /**
     * 判断是否为PDF
     */
    public static boolean isPdf(byte[] fileBytes, String fileName) {
        return MIME_PDF.equals(getRealContentTypeByBytes(fileBytes, fileName));
    }

    public static boolean isPdf(InputStream inputStream, String fileName) {
        return MIME_PDF.equals(getRealContentTypeByStream(inputStream, fileName));
    }

    /**
     * 判断是否为Word文档 doc/docx
     */
    public static boolean isWord(byte[] fileBytes, String fileName) {
        String mime = getRealContentTypeByBytes(fileBytes, fileName);
        return MIME_DOC.equals(mime) || MIME_DOCX.equals(mime);
    }

    public static boolean isWord(InputStream inputStream, String fileName) {
        String mime = getRealContentTypeByStream(inputStream, fileName);
        return MIME_DOC.equals(mime) || MIME_DOCX.equals(mime);
    }

    /**
     * 判断是否为zip压缩包
     */
    public static boolean isZip(byte[] fileBytes, String fileName) {
        return MIME_ZIP.equals(getRealContentTypeByBytes(fileBytes, fileName));
    }

    public static boolean isZip(InputStream inputStream, String fileName) {
        return MIME_ZIP.equals(getRealContentTypeByStream(inputStream, fileName));
    }

    /**
     * 判断是否为图片（jpg/png/gif/bmp/webp等所有image/*类型）
     */
    public static boolean isImage(byte[] fileBytes, String fileName) {
        return getRealContentTypeByBytes(fileBytes, fileName).startsWith(PREFIX_IMAGE);
    }

    public static boolean isImage(InputStream inputStream, String fileName) {
        return getRealContentTypeByStream(inputStream, fileName).startsWith(PREFIX_IMAGE);
    }

    /**
     * 判断是否为音频（mp3/wav/flac/m4a等 audio/*）
     */
    public static boolean isAudio(byte[] fileBytes, String fileName) {
        return getRealContentTypeByBytes(fileBytes, fileName).startsWith(PREFIX_AUDIO);
    }

    public static boolean isAudio(InputStream inputStream, String fileName) {
        return getRealContentTypeByStream(inputStream, fileName).startsWith(PREFIX_AUDIO);
    }

    /**
     * 判断是否为视频（mp4/mov/avi/mkv等 video/*）
     */
    public static boolean isVideo(byte[] fileBytes, String fileName) {
        return getRealContentTypeByBytes(fileBytes, fileName).startsWith(PREFIX_VIDEO);
    }

    public static boolean isVideo(InputStream inputStream, String fileName) {
        return getRealContentTypeByStream(inputStream, fileName).startsWith(PREFIX_VIDEO);
    }

    /**
     * 判断是否为媒体文件（图片 || 音频 || 视频）
     */
    public static boolean isMedia(byte[] fileBytes, String fileName) {
        String mime = getRealContentTypeByBytes(fileBytes, fileName);
        return mime.startsWith(PREFIX_IMAGE)
                || mime.startsWith(PREFIX_AUDIO)
                || mime.startsWith(PREFIX_VIDEO);
    }

    public static boolean isMedia(InputStream inputStream, String fileName) {
        String mime = getRealContentTypeByStream(inputStream, fileName);
        return mime.startsWith(PREFIX_IMAGE)
                || mime.startsWith(PREFIX_AUDIO)
                || mime.startsWith(PREFIX_VIDEO);
    }
}