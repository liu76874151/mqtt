package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午9:37 To change
 * this template use File | Settings | File Templates.
 */
public abstract class Decoder {

	public void decodeHeader(Message message, ByteBuffer buffer) {
		if (buffer.remaining() < 2) {
			throw new MQTTException("Protocol error - no data");
		}
		byte fixedHeader = buffer.get();
		message.setDup(((fixedHeader & 0x08) == 0x08));
		message.setQos((byte) ((fixedHeader & 0x06) >> 1));
		message.setRetain((fixedHeader & 0x01) != 0);
		message.setRemainLength(decodeRemainingLenght(buffer));
		if (message.getRemainLength() == -1) {
			throw new MQTTException("Protocol error - no data");
		}
	}

	protected int decodeRemainingLenght(ByteBuffer buffer) {
		int multiplier = 1;
		int value = 0;
		byte digit;
		do {
			if (buffer.remaining() < 1) {
				return -1;
			}
			digit = buffer.get();
			value += (digit & 0x7F) * multiplier;
			multiplier *= 128;
		} while ((digit & 0x80) != 0);
		return value;
	}

	protected int decodeLength(ByteBuffer buffer) {
		if (buffer.remaining() < 2) {
			throw new MQTTException("Protocol error - error length");
		}
		int msb = buffer.get() & 0xFF;
		int lsb = buffer.get() & 0xFF;
		return (msb << 8) | lsb;
	}

	public abstract Message decode(ByteBuffer buffer);
}
