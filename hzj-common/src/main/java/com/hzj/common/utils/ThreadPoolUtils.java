package com.hzj.common.utils;

import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池构建工具类
 * <p>
 * 统一封装业务分类线程池创建逻辑，分为核心业务线程池、批量任务线程池、IO密集型线程池；
 * 基于Spring {@link ThreadPoolTaskExecutor}，支持任务上下文装饰器、核心线程预热配置。
 */
public class ThreadPoolUtils {

    /**
     * 当前机器CPU核心数量
     */
    private static final int cpuCores = Runtime.getRuntime().availableProcessors();

    /**
     * 创建【核心业务线程池】
     * <p>适用场景：订单、支付等强一致性关键链路任务</p>
     * <p>特性：CPU密集型配置；有界队列；拒绝策略AbortPolicy快速失败；允许核心线程超时回收</p>
     *
     * @param prestartAllCoreThreads 是否预热创建全部核心线程
     * @param decorator             任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createCoreBusinessExecutor(boolean prestartAllCoreThreads, TaskDecorator decorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：CPU密集型，核心数+1
        executor.setCorePoolSize(cpuCores + 1);
        // 最大线程数：允许临时扩容
        executor.setMaxPoolSize(cpuCores * 2);
        // 空闲存活时间：60秒回收非核心线程
        executor.setKeepAliveSeconds(60);
        // 队列容量：使用有界队列，防止任务无限堆积
        executor.setQueueCapacity(200);
        // 线程名前缀，便于日志排查线程归属
        executor.setThreadNamePrefix("core-biz-");
        // 拒绝策略：直接抛出异常，快速失败，让调用方感知流量超限
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 允许核心线程超时回收，低流量场景释放资源
        executor.setAllowCoreThreadTimeOut(true);
        // 是否启动时预热所有核心线程
        executor.setPrestartAllCoreThreads(prestartAllCoreThreads);

        if (decorator != null) {
            executor.setTaskDecorator(decorator);
        }
        executor.initialize();
        return executor;
    }

    /**
     * 创建【批量任务线程池】
     * <p>适用场景：报表导出、邮件推送、数据同步等非核心、可容忍延迟任务</p>
     * <p>特性：队列容量较大；拒绝策略CallerRunsPolicy，提交线程降级执行</p>
     *
     * @param decorator 任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createBatchExecutor(TaskDecorator decorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：数量偏少，常态节约资源
        executor.setCorePoolSize(5);
        // 最大线程数：适度扩容
        executor.setMaxPoolSize(20);
        // 空闲存活时间：30秒回收空闲线程
        executor.setKeepAliveSeconds(30);
        // 队列容量：较大，支持短时任务堆积
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("batch-");
        // 拒绝策略：调用者线程执行任务，实现简单限流降级
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 允许核心线程超时回收
        executor.setAllowCoreThreadTimeOut(true);

        if (decorator != null) {
            executor.setTaskDecorator(decorator);
        }
        executor.initialize();
        return executor;
    }

    /**
     * 创建【IO密集型线程池】
     * <p>适用场景：大量外部HTTP调用、数据库查询、RPC请求等阻塞等待型任务</p>
     * <p>特性：线程数量配置较高，充分利用IO等待时隙；有界队列；拒绝策略快速失败</p>
     *
     * @param decorator 任务装饰器，用于传递MDC上下文等，可为null
     * @return 初始化完成的 ThreadPoolTaskExecutor
     */
    public static ThreadPoolTaskExecutor createIoIntensiveExecutor(TaskDecorator decorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数：IO密集型，CPU核心数 * 4
        executor.setCorePoolSize(cpuCores * 4);
        // 最大线程数：CPU核心数 * 8
        executor.setMaxPoolSize(cpuCores * 8);
        // 空闲存活时间：60秒
        executor.setKeepAliveSeconds(60);
        // 队列容量：适中，避免过大导致OOM
        executor.setQueueCapacity(300);
        executor.setThreadNamePrefix("io-");
        // 拒绝策略：直接抛出异常，防止任务持续堆积
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 允许核心线程超时回收
        executor.setAllowCoreThreadTimeOut(true);

        if (decorator != null) {
            executor.setTaskDecorator(decorator);
        }
        executor.initialize();
        return executor;
    }
}
