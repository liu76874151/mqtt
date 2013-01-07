package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.Message;

public interface IPublishCallback extends Callback {

	public void published(String topic, byte[] payload);

}
