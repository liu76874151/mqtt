package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PingRespMessage;

public class PingRespDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PingRespMessage message = new PingRespMessage();
		decodeHeader(message, buffer);
		return message;
	}

	@Override
	public boolean doDecodable(ByteBuffer buffer) {
		if (buffer.remaining() < 2) {
			return false;
		}
		buffer.get();
		int remainingLength = decodeRemainingLenght(buffer);
		if (remainingLength != 0) {
			throw new MQTTException("Protocol error - error data");
		}
		return true;
	}
}
