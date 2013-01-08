package com.aliyun.mqtt.core.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PublishMessage;

public class PublishEncoder extends Encoder {

	@Override
	public ByteBuffer encode(Message msg) {
		ByteArrayOutputStream out = null;
		ByteArrayOutputStream data = null;
		try {
			out = new ByteArrayOutputStream();
			data = new ByteArrayOutputStream();
			PublishMessage message = (PublishMessage) msg;
			/* variable header */
			/* topic */
			encodeString(message.getTopic(), data);
			/* message id */
			if(message.getQos() == MQTT.QOS_LEAST_ONCE || message.getQos() == MQTT.QOS_ONCE) {
				encodeLength(message.getMessageID(), data);
			}
			/* payload */
			data.write(message.getPayload());
			message.setRemainLength(data.size());

			/* fixed header */
			out.write(encodeHeader(message));
			/* remain length */
			encodeRemainLength(message.getRemainLength(), out);
			/* data */
			data.writeTo(out);
			return ByteBuffer.wrap(out.toByteArray());
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (data != null) {
					data.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
