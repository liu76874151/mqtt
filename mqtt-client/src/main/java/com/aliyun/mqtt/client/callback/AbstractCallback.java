package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.Message;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午9:56 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractCallback implements Callback<Message> {

	public void onSuccess(Message message) {
		result(true);
	}
	
	public void onFailure(Throwable throwable) {
		result(false);
	}

	public abstract void result(boolean result);
}
