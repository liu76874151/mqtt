package com.aliyun.mqtt.client;

public interface IMessageCallback {

	public void published(String topic, byte[] payload);
	
}
