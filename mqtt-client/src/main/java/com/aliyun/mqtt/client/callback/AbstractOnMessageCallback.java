package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.PublishMessage;

public abstract class AbstractOnMessageCallback implements Callback<PublishMessage> {

	public void onSuccess(PublishMessage result) {
		PublishMessage publishMessage = (PublishMessage) result;
		onMessage(publishMessage.getTopic(), publishMessage.getPayload());
	}
	
	public void onFailure(Throwable throwable) {
		
	}

	public abstract void onMessage(String topic, byte[] payload);
}
