package com.aliyun.mqtt.client;

import java.util.concurrent.ExecutorService;

import com.aliyun.mqtt.client.message.MessageHandler;
import com.aliyun.mqtt.client.message.MessageQueue;
import com.aliyun.mqtt.core.parser.MQTTParser;

public class Context {
	private Client client;

	public Context(Client client) {
		this.client = client;
	}

	public MessageQueue getMessageQueue() {
		return client.getMessageQueue();
	}

	public MessageHandler getMessageHandler() {
		return client.getMessageHandler();
	}

	public MQTTParser getParser() {
		return client.getMQTTParser();
	}
	
	public ExecutorService getExecutor() {
		return client.getExecutor();
	}

	public Client getClient() {
		return client;
	}
	
	public boolean execute(Runnable runnable) {
		ExecutorService executor = getExecutor();
		if (!executor.isShutdown() && !executor.isTerminated()) {
			executor.execute(runnable);
			return true;
		}
		return false;
	}

	public void stopClient() {
		client.close();
	}
}
