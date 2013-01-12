package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.PublishMessage;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午9:56 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractOnMessageCallback implements Callback {

	public void callback(Object result) {
		PublishMessage publishMessage = (PublishMessage) result;
		onMessage(publishMessage.getTopic(), publishMessage.getPayload());
	}

	public abstract void onMessage(String topic, byte[] payload);
}