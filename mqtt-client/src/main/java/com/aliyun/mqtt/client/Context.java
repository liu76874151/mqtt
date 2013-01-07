package com.aliyun.mqtt.client;

import java.util.concurrent.ExecutorService;

import com.aliyun.mqtt.client.message.MessageHandler;
import com.aliyun.mqtt.client.message.MessageQueue;
import com.aliyun.mqtt.core.parser.MQTTParser;

public class Context {
	private Client client;

    private MessageQueue messageQueue;

    private MessageHandler messageHandler;

    private MQTTParser parser;

	public Context(Client client) {
		this.client = client;
	}

    public void registeMessageQueue(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void registeMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void registeParser(MQTTParser parser) {
        this.parser = parser;
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

	public Client getClient() {
		return client;
	}

	public void stopClient() {
		client.close();
	}
}
