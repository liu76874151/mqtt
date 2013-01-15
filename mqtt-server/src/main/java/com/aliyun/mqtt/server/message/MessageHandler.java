package com.aliyun.mqtt.server.message;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.ConnectMessage;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PingReqMessage;
import com.aliyun.mqtt.core.message.PingRespMessage;
import com.aliyun.mqtt.server.ApplicationContext;
import com.aliyun.mqtt.server.client.Session;

public class MessageHandler {
	
	private static Logger logger = Logger.getLogger("mqtt-server");
	
	public void handle(Session session, ByteBuffer buffer) {
		Message message = ApplicationContext.getContext().getParser().decode(buffer);
		if (message != null) {
			logger.info("Received a message of type "
					+ message.getClass().getSimpleName() + " from client " + session.getClientID());
			switch (message.getType()) {
			case MQTT.MESSAGE_TYPE_CONNECT: // 0x01
				handleConnect(session, (ConnectMessage)message);
				break;
			case MQTT.MESSAGE_TYPE_PINGREQ:
				handlePingReq(session, (PingReqMessage)message);
				break;
			}
		}
	}
	
	protected void handleConnect(Session session, ConnectMessage message) {
		ConnAckMessage m = new ConnAckMessage();
		m.setAck(message.getAck());
		ApplicationContext.getContext().getMessageSender().send(session, m);
		
		if (message.isCleanSession()) {
			//clean session
		}
		session.connectCallback(message);
	}
	
	protected void handlePingReq(Session session, PingReqMessage message) {
		PingRespMessage m = new PingRespMessage();
		ApplicationContext.getContext().getMessageSender().send(session, m);
	}
}
