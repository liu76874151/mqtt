package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PubRelMessage extends MessageIDMessage {

	public PubRelMessage() {
		this.type = MQTT.MESSAGE_TYPE_PUBREL;
		setQos(MQTT.QOS_LEAST_ONCE);
	}

}
