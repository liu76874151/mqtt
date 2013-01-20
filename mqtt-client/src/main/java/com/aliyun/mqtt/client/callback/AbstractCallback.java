package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.Message;

public abstract class AbstractCallback implements Callback<Message> {

	public void onSuccess(Message message) {
		result(true);
	}
	
	public void onFailure(Throwable throwable) {
		result(false);
	}

	public abstract void result(boolean result);
}
