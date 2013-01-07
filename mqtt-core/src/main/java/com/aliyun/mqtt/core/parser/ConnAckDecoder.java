package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;

public class ConnAckDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		ConnAckMessage message = new ConnAckMessage();
		decodeHeader(message, buffer);
		return message;
	}

}
