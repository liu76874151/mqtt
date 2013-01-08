package com.aliyun.mqtt.client;

import java.util.concurrent.ScheduledExecutorService;

import com.aliyun.mqtt.client.message.MessageHandler;
import com.aliyun.mqtt.client.message.MessageIDGenerator;
import com.aliyun.mqtt.client.message.MessageQueue;
import com.aliyun.mqtt.client.message.MessageSender;
import com.aliyun.mqtt.client.message.MessageStore;
import com.aliyun.mqtt.core.parser.MQTTParser;

public class Context {
	private Client client;

	private MessageQueue messageQueue = new MessageQueue();

	private MessageHandler messageHandler = new MessageHandler(this);

	private MQTTParser parser;

	private ScheduledExecutorService scheduler;

	private MessageSender sender = new MessageSender(this);

	private MessageIDGenerator messageIDGenerator = new MessageIDGenerator();

	private MessageStore messageStore = new MessageStore();

	public Context(Client client) {
		this.client = client;
	}

	public int nextMessageID() {
		return messageIDGenerator.next();
	}

	public void registeParser(MQTTParser parser) {
		this.parser = parser;
	}

	public void registeScheduler(ScheduledExecutorService scheduler) {
		this.scheduler = scheduler;
	}

	public MessageQueue getMessageQueue() {
		return messageQueue;
	}

	public MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public MQTTParser getParser() {
		return parser;
	}

	public ScheduledExecutorService getScheduler() {
		return scheduler;
	}

	public MessageSender getSender() {
		return sender;
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public Client getClient() {
		return client;
	}

	public void stopClient() {
		client.close();
	}

	public void clear() {
		messageQueue.clear();
		messageStore.clear();
	}
}
