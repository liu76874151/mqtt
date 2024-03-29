package com.aliyun.mqtt.server.message;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {

	private Queue<ByteBuffer> messageQueue = new LinkedList<ByteBuffer>();

	public MessageQueue() {
	}

	public boolean add(ByteBuffer buffer) {
		synchronized (messageQueue) {
			return messageQueue.add(buffer);
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
