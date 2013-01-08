package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

public class PubRecMessage extends MessageIDMessage {

	public PubRecMessage() {
		this.type = MQTT.MESSAGE_TYPE_PUBREC;
		setQos(MQTT.QOS_LEAST_ONCE);
	}

}
