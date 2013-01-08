package com.aliyun.mqtt.client.callback;

public interface IPublishCallback extends Callback {

	public void published(String topic, byte[] payload);

}
