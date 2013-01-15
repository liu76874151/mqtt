package com.aliyun.mqtt.server.message;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.server.ApplicationContext;
import com.aliyun.mqtt.server.client.Session;

public class MessageSender {

	private Logger logger = Logger.getLogger("mqtt-server");
	
	public void send(Session session, Message message) {
		if (message.getQos() == MQTT.QOS_ONCE) {
			
		} else if (message.getQos() == MQTT.QOS_LEAST_ONCE) {
			
		} else {
			message.setQos(MQTT.QOS_MOST_ONCE);
			send0(session, message);
		}
	}
	
	private void send0(Session session, Message message) {
		try {
			logger.info("Send a message of type "
					+ message.getClass().getSimpleName() + " to client " + session.getClientID());
			ByteBuffer buffer = ApplicationContext.getContext().getParser().encode(message);
			if (buffer != null) {
				session.write(buffer);
			}
		} catch (MQTTException e) {
			e.printStackTrace();
		}
	}
	
}
