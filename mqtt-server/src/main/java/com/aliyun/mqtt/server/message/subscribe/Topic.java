package com.aliyun.mqtt.server.message.subscribe;

public class Topic {

	public String name;
	
	private byte qos;
	
	public Topic(String name, byte qos) {
		super();
		this.name = name;
		this.qos = qos;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getQos() {
		return qos;
	}

	public void setQos(byte qos) {
		this.qos = qos;
	}
	
}
