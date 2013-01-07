package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PingRespMessage;

public class PingRespDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PingRespMessage message = new PingRespMessage();
		decodeHeader(message, buffer);
		return message;
	}

}
