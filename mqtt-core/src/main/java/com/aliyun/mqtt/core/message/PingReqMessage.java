package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PingReqMessage extends Message {

	public PingReqMessage() {
		this.type = MQTT.MESSAGE_TYPE_PINGREQ;
	}
}
