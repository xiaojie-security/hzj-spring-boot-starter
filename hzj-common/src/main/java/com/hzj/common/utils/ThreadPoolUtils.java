package com.hzj.common.utils;

import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池构建工具类
 */
public class ThreadPoolUtils {

    /**
     * 当前机器CPU核心数量
     */
    private static final int cpuCores = Runtime.getRuntime().availableProcessors();

    // ========================================== 核心业务线程池 ==========================================
    /**
     * 创建【核心业务线程池】，全部使用默认配置
     * <p>适用场景：订单、支付等强一致性关键链路任务</p>
     * <p>特性：CPU密集型配置；有界队列；拒绝策略AbortPolicy快速失败；允许核心线程超时回收</p>
     * <ul>
     *     <li>线程前缀：core-biz-</li>
     *     <li>不预热核心线程</li>
     *     <li>无任务装饰器</li>
     * </ul>
     *
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createCoreBusinessExecutor() {
        return createCoreBusinessExecutor("core-biz-");
    }

    /**
     * 创建【核心业务线程池】，自定义线程名称前缀
     *
     * @param threadNamePrefix 自定义线程名前缀
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createCoreBusinessExecutor(String threadNamePrefix) {
        return createCoreBusinessExecutor(threadNamePrefix, false);
    }

    /**
     * 创建【核心业务线程池】，自定义线程前缀 + 是否预热核心线程
     *
     * @param threadNamePrefix       自定义线程名前缀
     * @param prestartAllCoreThreads 是否预热创建全部核心线程
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createCoreBusinessExecutor(String threadNamePrefix, boolean prestartAllCoreThreads) {
        return createCoreBusinessExecutor(threadNamePrefix, prestartAllCoreThreads, null);
    }

    /**
     * 创建【核心业务线程池】完整参数重载
     *
     * @param threadNamePrefix       自定义线程名前缀
     * @param prestartAllCoreThreads 是否预热创建全部核心线程
     * @param decorator              任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createCoreBusinessExecutor(String threadNamePrefix, boolean prestartAllCoreThreads, TaskDecorator decorator) {
        return createExecutor(
                cpuCores + 1,
                cpuCores * 2,
                60,
                200,
                threadNamePrefix,
                new ThreadPoolExecutor.AbortPolicy(),
                true,
                prestartAllCoreThreads,
                decorator
        );
    }

    // ========================================== 批量任务线程池 ==========================================
    /**
     * 创建【批量任务线程池】，全部使用默认配置
     * <p>适用场景：报表导出、邮件推送、数据同步等非核心、可容忍延迟任务</p>
     * <p>特性：队列容量较大；拒绝策略CallerRunsPolicy，提交线程降级执行</p>
     * <ul>
     *     <li>线程前缀：batch-</li>
     *     <li>不预热核心线程</li>
     *     <li>无任务装饰器</li>
     * </ul>
     *
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createBatchExecutor() {
        return createBatchExecutor("batch-");
    }

    /**
     * 创建【批量任务线程池】，自定义线程名称前缀
     *
     * @param threadNamePrefix 自定义线程名前缀
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createBatchExecutor(String threadNamePrefix) {
        return createBatchExecutor(threadNamePrefix, null);
    }

    /**
     * 创建【批量任务线程池】完整参数重载
     *
     * @param threadNamePrefix 自定义线程名前缀
     * @param decorator        任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createBatchExecutor(String threadNamePrefix, TaskDecorator decorator) {
        return createExecutor(
                5,
                20,
                30,
                500,
                threadNamePrefix,
                new ThreadPoolExecutor.CallerRunsPolicy(),
                true,
                false,
                decorator
        );
    }

    // ========================================== IO密集型线程池 ==========================================
    /**
     * 创建【IO密集型线程池】，全部使用默认配置
     * <p>适用场景：大量外部HTTP调用、数据库查询、RPC请求等阻塞等待型任务</p>
     * <p>特性：线程数量配置较高，充分利用IO等待时隙；有界队列；拒绝策略快速失败</p>
     * <ul>
     *     <li>线程前缀：io-</li>
     *     <li>不预热核心线程</li>
     *     <li>无任务装饰器</li>
     * </ul>
     *
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createIoIntensiveExecutor() {
        return createIoIntensiveExecutor("io-");
    }

    /**
     * 创建【IO密集型线程池】，自定义线程名称前缀
     *
     * @param threadNamePrefix 自定义线程名前缀
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createIoIntensiveExecutor(String threadNamePrefix) {
        return createIoIntensiveExecutor(threadNamePrefix, null);
    }

    /**
     * 创建【IO密集型线程池】完整参数重载
     *
     * @param threadNamePrefix 自定义线程名前缀
     * @param decorator        任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createIoIntensiveExecutor(String threadNamePrefix, TaskDecorator decorator) {
        return createExecutor(
                cpuCores * 4,
                cpuCores * 8,
                60,
                300,
                threadNamePrefix,
                new ThreadPoolExecutor.AbortPolicy(),
                true,
                false,
                decorator
        );
    }

    // ========================================== 私有底层统一构造器 ==========================================
    /**
     * 通用线程池构建底层方法
     *
     * @param corePoolSize           核心线程数
     * @param maxPoolSize            最大线程数
     * @param keepAliveSeconds       空闲线程存活时间（秒）
     * @param queueCapacity          阻塞队列容量
     * @param threadNamePrefix       线程名称前缀
     * @param rejectedHandler        拒绝策略
     * @param allowCoreThreadTimeOut 是否允许核心线程超时回收
     * @param prestartAllCoreThreads 是否预热启动所有核心线程
     * @param decorator              任务装饰器，可为null
     * @return 初始化完毕的 ThreadPoolTaskExecutor
     */
    private static ThreadPoolTaskExecutor createExecutor(int corePoolSize,
                                                         int maxPoolSize,
                                                         int keepAliveSeconds,
                                                         int queueCapacity,
                                                         String threadNamePrefix,
                                                         RejectedExecutionHandler rejectedHandler,
                                                         boolean allowCoreThreadTimeOut,
                                                         boolean prestartAllCoreThreads,
                                                         TaskDecorator decorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.setRejectedExecutionHandler(rejectedHandler);
        executor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        executor.setPrestartAllCoreThreads(prestartAllCoreThreads);

        if (decorator != null) {
            executor.setTaskDecorator(decorator);
        }
        executor.initialize();
        return executor;
    }

    // ========================================== 通用自定义线程池入口 ==========================================
    /**
     * 通用自定义线程池创建（对外开放，用于非常规业务线程池）
     *
     * @param corePoolSize           核心线程数
     * @param maxPoolSize            最大线程数
     * @param keepAliveSeconds       空闲存活秒数
     * @param queueCapacity          队列容量
     * @param threadNamePrefix       线程前缀
     * @param rejectedHandler        拒绝策略
     * @param allowCoreThreadTimeOut 是否允许核心线程超时
     * @param prestartAllCoreThreads 是否预热核心线程
     * @param decorator              任务装饰器
     * @return ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor newExecutor(int corePoolSize,
                                                     int maxPoolSize,
                                                     int keepAliveSeconds,
                                                     int queueCapacity,
                                                     String threadNamePrefix,
                                                     RejectedExecutionHandler rejectedHandler,
                                                     boolean allowCoreThreadTimeOut,
                                                     boolean prestartAllCoreThreads,
                                                     TaskDecorator decorator) {
        return createExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, queueCapacity,
                threadNamePrefix, rejectedHandler, allowCoreThreadTimeOut, prestartAllCoreThreads, decorator);
    }

    /**
     * 安全关闭线程池，默认等待30秒
     * <p>先执行shutdown，等待已有任务完成；超时未结束则调用shutdownNow中断任务</p>
     *
     * @param executor 需要关闭的 ThreadPoolTaskExecutor
     */
    public static void safeShutdown(ThreadPoolTaskExecutor executor) {
        safeShutdown(executor, 30, TimeUnit.SECONDS);
    }

    /**
     * 安全关闭线程池，自定义等待时长
     *
     * @param executor 需要关闭的 ThreadPoolTaskExecutor
     * @param waitTime 最大等待时长
     * @param unit     时间单位
     */
    public static void safeShutdown(ThreadPoolTaskExecutor executor, long waitTime, TimeUnit unit) {
        if (executor == null) {
            return;
        }
        ThreadPoolExecutor nativeExecutor = executor.getThreadPoolExecutor();
        safeShutdown(nativeExecutor, waitTime, unit);
    }

    /**
     * 安全关闭原生 ThreadPoolExecutor，默认等待30秒
     *
     * @param executor 原生线程池实例
     */
    public static void safeShutdown(ThreadPoolExecutor executor) {
        safeShutdown(executor, 30, TimeUnit.SECONDS);
    }

    /**
     * 底层通用安全关闭实现
     *
     * @param executor 原生线程池
     * @param waitTime 最大等待时间
     * @param unit     时间单位
     */
    public static void safeShutdown(ThreadPoolExecutor executor, long waitTime, TimeUnit unit) {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        executor.shutdown();
        try {
            // 等待任务执行完成
            if (!executor.awaitTermination(waitTime, unit)) {
                // 超时强制关闭
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            // 收到中断信号，立刻强制终止
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
