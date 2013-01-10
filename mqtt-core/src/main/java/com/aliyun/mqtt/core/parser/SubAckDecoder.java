package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.SubAckMessage;

public class SubAckDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		SubAckMessage message = new SubAckMessage();
		decodeHeader(message, buffer);
		int oldpos = buffer.position();
		message.setMessageID(decodeLength(buffer));
		int pos = buffer.position();
		for (int i = pos - oldpos; i < message.getRemainLength(); i++) {
			message.addQos(buffer.get());
		}
		return message;
	}

}
