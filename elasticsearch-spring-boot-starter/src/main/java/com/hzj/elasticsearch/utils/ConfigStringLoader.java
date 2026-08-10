package com.hzj.elasticsearch.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;

/**
 * 配置字符串加载工具，支持 classpath/file/普通文件路径/直接文本内容。
 * 增强版：支持获取文件绝对路径、路径类型识别、缓存等功能。
 */
@Slf4j
public final class ConfigStringLoader {

    /**
     * 路径类型枚举
     */
    public enum PathType {
        CLASSPATH,      // classpath: 前缀
        FILE,           // file: 前缀
        ABSOLUTE_PATH,  // 绝对路径
        RELATIVE_PATH,  // 相对路径
        DIRECT_TEXT     // 直接文本内容
    }

    private ConfigStringLoader() {
    }

    /**
     * 解析配置来源并返回文本内容。
     *
     * @param source 配置来源
     * @return 文本内容
     */
    public static String load(String source) {
        validateSource(source);
        String trimmedSource = source.trim();

        if (isClasspathSource(trimmedSource)) {
            return loadFromResource(trimmedSource);
        }
        if (isFileSource(trimmedSource)) {
            return loadFromFilePath(trimmedSource);
        }
        if (isFilePath(trimmedSource)) {
            return loadFromFilePath(trimmedSource);
        }
        return trimmedSource;
    }

    /**
     * 获取配置来源的绝对路径（如果存在）。
     *
     * @param source 配置来源
     * @return 绝对路径的 Optional，如果无法获取则返回 empty
     */
    public static Optional<Path> getAbsolutePath(String source) {
        validateSource(source);
        String trimmedSource = source.trim();

        try {
            if (isClasspathSource(trimmedSource)) {
                return getClasspathAbsolutePath(trimmedSource);
            }
            if (isFileSource(trimmedSource)) {
                return getFileAbsolutePath(trimmedSource);
            }
            if (isFilePath(trimmedSource)) {
                Path path = Paths.get(trimmedSource);
                if (Files.exists(path)) {
                    return Optional.of(path.toAbsolutePath().normalize());
                }
            }
        } catch (Exception e) {
            log.debug("ConfigStringLoader.getAbsolutePath 无法获取绝对路径，source={}", trimmedSource, e);
        }
        return Optional.empty();
    }

    /**
     * 获取配置来源的绝对路径字符串（如果存在）。
     *
     * @param source 配置来源
     * @return 绝对路径字符串，如果无法获取则返回 null
     */
    public static String getAbsolutePathString(String source) {
        return getAbsolutePath(source)
                .map(Path::toString)
                .orElse(null);
    }

    /**
     * 判断配置来源是否为有效的文件路径。
     *
     * @param source 配置来源
     * @return true 表示是文件路径
     */
    public static boolean isFilePathSource(String source) {
        if (!StringUtils.hasText(source)) {
            return false;
        }
        String trimmedSource = source.trim();
        return isFileSource(trimmedSource) ||
                isClasspathSource(trimmedSource) ||
                (isFilePath(trimmedSource) && Files.exists(Paths.get(trimmedSource)));
    }

    /**
     * 获取配置来源的路径类型。
     *
     * @param source 配置来源
     * @return 路径类型
     */
    public static PathType getPathType(String source) {
        if (!StringUtils.hasText(source)) {
            return PathType.DIRECT_TEXT;
        }
        String trimmedSource = source.trim();

        if (isClasspathSource(trimmedSource)) {
            return PathType.CLASSPATH;
        }
        if (isFileSource(trimmedSource)) {
            return PathType.FILE;
        }
        if (isFilePath(trimmedSource)) {
            Path path = Paths.get(trimmedSource);
            if (path.isAbsolute()) {
                return PathType.ABSOLUTE_PATH;
            }
            return PathType.RELATIVE_PATH;
        }
        return PathType.DIRECT_TEXT;
    }

    /**
     * 检查配置来源是否指向存在的文件。
     *
     * @param source 配置来源
     * @return true 表示文件存在
     */
    public static boolean exists(String source) {
        if (!StringUtils.hasText(source)) {
            return false;
        }
        String trimmedSource = source.trim();
        try {
            if (isClasspathSource(trimmedSource)) {
                Resource resource = new DefaultResourceLoader().getResource(trimmedSource);
                return resource.exists();
            }
            if (isFileSource(trimmedSource)) {
                String path = trimmedSource.substring(ResourceUtils.FILE_URL_PREFIX.length());
                return Files.exists(Paths.get(path));
            }
            if (isFilePath(trimmedSource)) {
                return Files.exists(Paths.get(trimmedSource));
            }
        } catch (Exception e) {
            log.debug("检查文件存在性失败，source={}", trimmedSource, e);
        }
        return false;
    }


    private static void validateSource(String source) {
        if (!StringUtils.hasText(source)) {
            throw new IllegalArgumentException("配置项不能为空");
        }
    }

    private static boolean isClasspathSource(String source) {
        return source.startsWith(ResourceUtils.CLASSPATH_URL_PREFIX);
    }

    private static boolean isFileSource(String source) {
        return source.startsWith(ResourceUtils.FILE_URL_PREFIX);
    }

    private static boolean isFilePath(String source) {
        // 检查是否为文件路径：包含路径分隔符或者以文件扩展名结尾
        return source.contains("/") ||
                source.contains("\\") ||
                source.matches(".*\\.[a-zA-Z0-9]+$");
    }

    private static Optional<Path> getClasspathAbsolutePath(String location) {
        try {
            Resource resource = new DefaultResourceLoader().getResource(location);
            if (resource.exists()) {
                URL url = resource.getURL();
                if (ResourceUtils.FILE_URL_PREFIX.equals(url.getProtocol())) {
                    return Optional.of(Paths.get(url.toURI()).toAbsolutePath().normalize());
                }
                // 对于 jar 包内的资源，尝试获取文件系统中的路径
                if ("jar".equals(url.getProtocol())) {
                    String file = url.getFile();
                    if (file.contains("!")) {
                        String jarPath = file.substring(0, file.indexOf("!"));
                        return Optional.of(Paths.get(new URI(jarPath)).toAbsolutePath().normalize());
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            log.debug("获取 classpath 资源绝对路径失败，location={}", location, e);
        }
        return Optional.empty();
    }

    private static Optional<Path> getFileAbsolutePath(String location) {
        try {
            String path = location.substring(ResourceUtils.FILE_URL_PREFIX.length());
            Path p = Paths.get(path);
            if (Files.exists(p)) {
                return Optional.of(p.toAbsolutePath().normalize());
            }
        } catch (Exception e) {
            log.debug("获取 file: 资源绝对路径失败，location={}", location, e);
        }
        return Optional.empty();
    }

    private static String loadFromResource(String location) {
        try {
            Resource resource = new DefaultResourceLoader().getResource(location);
            if (!resource.exists()) {
                throw new IllegalArgumentException("资源不存在：" + location);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("ConfigStringLoader.loadFromResource 读取资源失败，location={}", location, e);
            throw new UncheckedIOException("读取资源失败：" + location, e);
        }
    }

    private static String loadFromFilePath(String path) {
        try {
            String actualPath = path;
            if (isFileSource(path)) {
                actualPath = path.substring(ResourceUtils.FILE_URL_PREFIX.length());
            }
            return Files.readString(Paths.get(actualPath));
        } catch (IOException e) {
            log.error("ConfigStringLoader.loadFromFilePath 读取文件失败 path={}", path, e);
            throw new UncheckedIOException("读取文件失败：" + path, e);
        }
    }
}
