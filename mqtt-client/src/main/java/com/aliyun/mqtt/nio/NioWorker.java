package com.aliyun.mqtt.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.aliyun.mqtt.client.Context;
import com.aliyun.mqtt.core.MQTTException;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:19 To change
 * this template use File | Settings | File Templates.
 */
public class NioWorker implements Runnable {

	private SocketChannel socketChannel = null;
	private Selector selector = null;

	private Context context = null;

	private byte[] array = new byte[1024];

	public NioWorker(SocketChannel socketChannel, Selector selector,
			Context context) {
		this.socketChannel = socketChannel;
		this.selector = selector;
		this.context = context;
	}

	public void run() {
		try {
			while (true) {
				if (!socketChannel.isOpen()) {
					break;
				}
				if (selector.select(0) > 0) {
					doSelector();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			stopClient();
		}
	}

	public void doSelector() throws IOException {
		for (SelectionKey key : selector.selectedKeys()) {
			selector.selectedKeys().remove(key);
			if (!key.isValid()) {
				continue;
			}
			doKeys(key);
		}
	}

	public void doKeys(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		if (key.isReadable()) {
			readResponse(channel);
		}
		if (key.isWritable()) {
			sendRequest(channel);
		}
	}

	private void readResponse(SocketChannel channel) throws IOException {
		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		int count = channel.read(byteBuffer);
		if (count > 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(count);
			while (count > 0) {
				byteBuffer.flip();
				out.write(byteBuffer.array(), 0, byteBuffer.remaining());
				byteBuffer.clear();
				count = channel.read(byteBuffer);
			}
			ByteBuffer returnBuffer = ByteBuffer.allocate(out.size());
			returnBuffer.clear();
			returnBuffer.put(out.toByteArray());
			out.close();
			returnBuffer.flip();
			try {
				ByteBuffer b = context.getMessageHandler().handle(returnBuffer);
				while (b != null && b.remaining() >= 2) {
					b = context.getMessageHandler().handle(b);
				}
			} catch (MQTTException e) {
				e.printStackTrace();
			}

		} else if (count < 0) {
			stopClient();
		}
	}

	public void sendRequest(SocketChannel channel) throws IOException {
		ByteBuffer message = context.getMessageQueue().get();
		if (message != null) {
			channel.write(message);
		}
	}

	public void stopClient() {
		context.stopClient();
	}
}
