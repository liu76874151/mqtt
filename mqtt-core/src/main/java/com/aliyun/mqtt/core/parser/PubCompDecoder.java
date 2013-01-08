package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PubCompMessage;

public class PubCompDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PubCompMessage message = new PubCompMessage();
		decodeHeader(message, buffer);
		message.setMessageID(decodeLength(buffer));
		return message;
	}

}
