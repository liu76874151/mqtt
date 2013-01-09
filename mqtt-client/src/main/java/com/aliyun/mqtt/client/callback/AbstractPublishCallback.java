package com.aliyun.mqtt.client.callback;


/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午9:56 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractPublishCallback implements Callback {

	public void callback(Object result) {
		published(true);
	}
	
	public abstract void published(boolean result);
}
