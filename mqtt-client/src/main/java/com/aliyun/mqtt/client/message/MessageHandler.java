package com.aliyun.mqtt.client.message;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.Context;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PubAckMessage;
import com.aliyun.mqtt.core.message.PubCompMessage;
import com.aliyun.mqtt.core.message.PubRecMessage;
import com.aliyun.mqtt.core.message.PubRelMessage;
import com.aliyun.mqtt.core.message.PublishMessage;
import com.aliyun.mqtt.core.message.SubAckMessage;

public class MessageHandler {

	private static Logger logger = Logger.getLogger("mqtt-client");

	private Context context = null;

	public MessageHandler(Context context) {
		this.context = context;
	}

	public ByteBuffer handle(ByteBuffer buffer) {
		Message message = context.getParser().decode(buffer);
		if (message != null) {
			logger.info("Received a message of type "
					+ message.getClass().getSimpleName());
			switch (message.getType()) {
			case MQTT.MESSAGE_TYPE_CONNACK: // 0x02
				handleConnAck(message);
				break;
			case MQTT.MESSAGE_TYPE_PUBLISH: // 0x03
				handlePublish(message);
				break;
			case MQTT.MESSAGE_TYPE_PUBACK: // 0x04
				handlePubAck(message);
				break;
			case MQTT.MESSAGE_TYPE_PUBREC: // 0x05
				handlePubRec(message);
				break;
			case MQTT.MESSAGE_TYPE_PUBREL: // 0x06
				handlePubRel(message);
				break;
			case MQTT.MESSAGE_TYPE_PUBCOMP: // 0x07
				handlePubComp(message);
				break;
			case MQTT.MESSAGE_TYPE_SUBACK: // 0x09
				handleSubAck(message);
				break;
			case MQTT.MESSAGE_TYPE_PINGRESP: // 0x0d
				handlePingResp(message);
				break;
			default:
				logger.info("Handler not exist");
				break;
			}
			if (buffer.position() > 0 && buffer.remaining() >= 2) {
				/* if the buffer has another message */
				return buffer.slice();
			}
			return null;
		}
		return null;
	}

	protected void handleConnAck(Message message) {
		context.getClient().connAckCallback((ConnAckMessage) message);
	}

	protected void handleSubAck(Message message) {
		SubAckMessage m = (SubAckMessage) message;
		context.getSender().sendQosAck("SUBSCRIBE", m.getMessageID());
		context.getClient().subAckCallback(m);
	}

	protected void handlePublish(Message message) {
		PublishMessage m = (PublishMessage) message;
		if (message.getQos() == MQTT.QOS_MOST_ONCE) {
			context.getClient().onPublished(m);
		} else if (message.getQos() == MQTT.QOS_LEAST_ONCE) {
			context.getClient().onPublished(m);
			PubAckMessage ack = new PubAckMessage();
			ack.setMessageID(m.getMessageID());
			context.getSender().send(ack);
		} else if (message.getQos() == MQTT.QOS_ONCE) {
			context.getMessageStore().qos2("ONPUBLISH_" + m.getMessageID(), m);
			PubRecMessage rec = new PubRecMessage();
			rec.setMessageID(m.getMessageID());
			context.getSender().send(rec);
		}
	}

	protected void handlePubAck(Message message) {
		PubAckMessage m = (PubAckMessage) message;
		context.getSender().sendQosAck("PUBLISH", m.getMessageID());
	}

	protected void handlePubRec(Message message) {
		PubRecMessage m = (PubRecMessage) message;
		context.getSender().sendQosAck("PUBLISH", m.getMessageID());

		PubRelMessage pubRelMessage = new PubRelMessage();
		pubRelMessage.setMessageID(m.getMessageID());
		context.getSender().send(pubRelMessage);
	}

	protected void handlePubRel(Message message) {
		PubRelMessage m = (PubRelMessage) message;
		context.getSender().sendQosAck("PUBREC", m.getMessageID());

		/* publish to Application */
		PublishMessage publishMessage = (PublishMessage) context
				.getMessageStore().getQos2("ONPUBLISH_" + m.getMessageID());
		if (publishMessage != null) {
			context.getClient().onPublished(publishMessage);
		}

		/* send comp message */
		PubCompMessage pubCompMessage = new PubCompMessage();
		pubCompMessage.setMessageID(m.getMessageID());
		context.getSender().send(pubCompMessage);
	}

	protected void handlePubComp(Message message) {
		PubCompMessage m = (PubCompMessage) message;
		context.getSender().sendQosAck("PUBREL", m.getMessageID());
	}

	protected void handlePingResp(Message message) {
	}

}
