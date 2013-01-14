package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTTException;
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
	
	@Override
	public boolean doDecodable(ByteBuffer buffer) {
		if (buffer.remaining() < 2) {
			return false;
		}
		buffer.get();
		int remainingLength = decodeRemainingLenght(buffer);
		if (remainingLength != 2) {
			throw new MQTTException("Protocol error - error data");
		}
		if (buffer.remaining() < remainingLength) {
			return false;
		}
		return true;
	}

}
