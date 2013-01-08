package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PublishMessage;

public class PublishDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PublishMessage message = new PublishMessage();
		decodeHeader(message, buffer);
		int position = buffer.position();
		message.setTopic(decodeString(buffer));
		if (message.getQos() == MQTT.QOS_LEAST_ONCE || message.getQos() == MQTT.QOS_ONCE) {
			message.setMessageID(decodeLength(buffer));
		}
		int payloadLength = message.getRemainLength() - (buffer.position() - position);
		if (payloadLength < 0 || payloadLength > buffer.remaining()) {
			throw new MQTTException("Protocol error - error data remaining length");
		}
		byte[] payload = new byte[payloadLength];
		buffer.get(payload);
		message.setPayload(payload);
		buffer.clear();
		return message;
	}

}
