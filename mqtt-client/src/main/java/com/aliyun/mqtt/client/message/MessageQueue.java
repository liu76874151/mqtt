package com.aliyun.mqtt.client.message;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {
	
	private Queue<ByteBuffer> messageQueue = new LinkedList<ByteBuffer>();
	
	private static MessageQueue instance = null;
	
	private MessageQueue() {
	}
	
	public static MessageQueue open() {
		if(instance == null) {
			instance = new MessageQueue();
		}
		return instance;
	}
	
	public boolean add(ByteBuffer buffer) {
		synchronized (messageQueue) {
			return messageQueue.add(buffer);
		}
	}
	
	public ByteBuffer get() {
		synchronized (messageQueue) {
			if(messageQueue.size() > 0) {
				return messageQueue.remove();
			}
			return null;
		}
	}

}
