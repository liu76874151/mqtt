package com.aliyun.mqtt.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.Context;
import com.aliyun.mqtt.core.MQTTException;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:19 To change
 * this template use File | Settings | File Templates.
 */
public class NioWorker implements Runnable {

	private static Logger logger = Logger.getLogger("mqtt-client");

	private SocketChannel socketChannel = null;
	private Selector selector = null;

	private Context context = null;

	private ByteBuffer buffer = null;
	
	private byte[] array = new byte[256];

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
				if (selector.select(30) > 0) {
					doSelector();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			stopClient();
		}
	}

	public void doSelector() throws IOException {
		Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
		while(iter.hasNext()) {
			SelectionKey key = iter.next();
			if (!key.isValid()) {
				continue;
			}
			doKeys(key);
			iter.remove();
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
		/* to be optimized */
		ByteBuffer byteBuffer = null;
		if (this.buffer != null && this.buffer.remaining() > 0) {
			int bufferSize = 1024;
			byteBuffer = ByteBuffer.allocate(this.buffer.remaining()
					+ bufferSize);
			byteBuffer.put(this.buffer.array());
		} else {
			byteBuffer = ByteBuffer.wrap(array);
		}
		int count = channel.read(byteBuffer);
		if (count > 0) {
			byteBuffer.flip();
			try {
				for (;;) {
					if (context.getParser().decodable(byteBuffer)) { /* ok */
						context.getMessageHandler().handle(byteBuffer);
					} else { /* need data */
						logger.warning("data not ready, waiting");
						break;
					}
					if (byteBuffer.remaining() < 2) {
						break;
					}
				}
				if (byteBuffer.remaining() <= 0) {
					this.buffer = null;
				} else {
					byte[] buf = new byte[byteBuffer.remaining()];
					byteBuffer.get(buf);
					this.buffer = ByteBuffer.wrap(buf);
				}
			} catch (MQTTException e) {
				e.printStackTrace();
				this.buffer = null;
			}
		} else if (count < 0) {
			throw new IOException("Connection error");
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
