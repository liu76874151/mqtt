package com.aliyun.mqtt.nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:55 To change
 * this template use File | Settings | File Templates.
 */
public class ExecutorServiceHolder {

	private ExecutorService executor = Executors.newFixedThreadPool(5);

	public ExecutorServiceHolder() {
	}

	public void execute(Runnable runnable) {
		executor.execute(runnable);
	}

	public void shutdown() {
		executor.shutdown();
	}

	public void shutdownNow() {
		executor.shutdownNow();
	}
}
