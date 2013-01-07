package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.Message;

public class PingReqEncoder extends Encoder {

	@Override
	public ByteBuffer encode(Message message) {
		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.put(encodeHeader(message));
		buffer.put((byte) 0);
		buffer.flip();
		return buffer;
	}

}
