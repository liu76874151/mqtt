package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PubRecMessage;

public class PubRecDecoder extends Decoder {

	@Override
	public Message decode(ByteBuffer buffer) {
		PubRecMessage message = new PubRecMessage();
		decodeHeader(message, buffer);
		message.setMessageID(decodeLength(buffer));
		return message;
	}

}
