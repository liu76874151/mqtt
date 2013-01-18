package com.aliyun.mqtt.client.message;

import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MessageQueue {

	private LinkedList<ByteBuffer> messageQueue = new LinkedList<ByteBuffer>();

	public MessageQueue() {
	}

	public boolean add(ByteBuffer buffer) {
		synchronized (messageQueue) {
			return messageQueue.add(buffer);
		}
	}
	
	public boolean addFirst(ByteBuffer buffer) {
		synchronized (messageQueue) {
			messageQueue.addFirst(buffer);
			return true;
		}
	}

	public ByteBuffer get() {
		synchronized (messageQueue) {
			return messageQueue.poll();
		}
	}

	public void clear() {
		synchronized (messageQueue) {
			messageQueue.clear();
		}
	}

}
