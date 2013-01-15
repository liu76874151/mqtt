package com.aliyun.mqtt.server;

import com.aliyun.mqtt.core.parser.MQTTParser;
import com.aliyun.mqtt.server.message.MessageHandler;
import com.aliyun.mqtt.server.message.MessageSender;

public class ApplicationContext {
	
	private static ApplicationContext context = null;
	
	private ApplicationContext() {}

	public static ApplicationContext getContext() {
		if (context == null) {
			context = new ApplicationContext();
		}
		return context;
	}
	
	private MQTTParser parser = null;
	
	public void registeParser(MQTTParser parser) {
		this.parser = parser;
	}
	
	public MQTTParser getParser() {
		return this.parser;
	}
	
	private MessageHandler messageHandler = null;
	
	public void registeMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	
	public MessageHandler getMessageHandler() {
		return this.messageHandler;
	}
	
	private MessageSender messageSender = null;
	
	public void registeMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	
	public MessageSender getMessageSender() {
		return this.messageSender;
	}
	
	public void clear() {
	}
	
}
