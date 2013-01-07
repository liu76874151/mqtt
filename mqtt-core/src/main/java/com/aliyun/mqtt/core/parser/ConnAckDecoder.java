package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;

public class ConnAckDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		ConnAckMessage message = new ConnAckMessage();
		decodeHeader(message, buffer);
		
		if(message.getRemainLength() != 2 || buffer.remaining() < 2) {
			throw new MQTTException("Protocol error - error data");
		}
		buffer.get();
		message.setAck(buffer.get());
		return message;
	}

}
