package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PingRespMessage extends Message {

	public PingRespMessage() {
		this.type = MQTT.MESSAGE_TYPE_PINGRESP;
	}
}
