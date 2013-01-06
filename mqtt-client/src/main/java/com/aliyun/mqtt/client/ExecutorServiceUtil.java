package com.aliyun.mqtt.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午8:55
 * To change this template use File | Settings | File Templates.
 */
public class ExecutorServiceUtil {

    private static ExecutorService executor = Executors.newFixedThreadPool(5);

    public static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
