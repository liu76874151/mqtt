package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午9:54 To change
 * this template use File | Settings | File Templates.
 */
public class ConnAckEncoder extends Encoder {

	@Override
	public ByteBuffer encode(Message msg) {
		ConnAckMessage message = (ConnAckMessage)msg;
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(encodeHeader(message));
		buffer.put((byte)2);
		buffer.put((byte)0);
		buffer.put(message.getAck());
		buffer.flip();
		return buffer;
	}
}
