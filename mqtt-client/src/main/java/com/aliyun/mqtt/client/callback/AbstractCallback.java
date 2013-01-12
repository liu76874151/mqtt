package com.aliyun.mqtt.client.callback;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午9:56 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractCallback implements Callback {

	public void callback(Object result) {
		result(result != null);
	}

	public abstract void result(boolean result);
}