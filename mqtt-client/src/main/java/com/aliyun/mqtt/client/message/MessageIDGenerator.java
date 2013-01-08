package com.aliyun.mqtt.client.message;

public class MessageIDGenerator {
	
	int current = -1;
	
	public int next() {
		current = (++current) & 0xFFFF;
		return current;
	}
	
	public void reset() {
		current = -1;
	}
	
}
