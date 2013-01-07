package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.SubAckMessage;

public class SubAckDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		SubAckMessage message = new SubAckMessage();
		decodeHeader(message, buffer);
		message.setMessageID(decodeLength(buffer));
		return message;
	}

}
