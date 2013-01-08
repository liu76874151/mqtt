package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PubAckMessage extends MessageIDMessage {

	public PubAckMessage() {
		this.type = MQTT.MESSAGE_TYPE_PUBACK;
	}

}
