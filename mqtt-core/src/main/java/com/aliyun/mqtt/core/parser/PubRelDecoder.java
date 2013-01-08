package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PubRelMessage;

public class PubRelDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PubRelMessage message = new PubRelMessage();
		decodeHeader(message, buffer);
		message.setMessageID(decodeLength(buffer));
		return message;
	}

}
