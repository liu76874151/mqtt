package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class ConnAckMessage extends Message {
	
	public ConnAckMessage() {
		this.type = MQTT.MESSAGE_TYPE_CONNACK;
	}
	
	private byte ack;

	public byte getAck() {
		return ack;
	}

	public void setAck(byte ack) {
		this.ack = ack;
	}
	
}
