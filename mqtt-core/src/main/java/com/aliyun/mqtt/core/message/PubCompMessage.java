package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PubCompMessage extends MessageIDMessage {

	public PubCompMessage() {
		this.type = MQTT.MESSAGE_TYPE_PUBCOMP;
	}

}
