package com.aliyun.mqtt.server.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.ConnectMessage;
import com.aliyun.mqtt.server.ApplicationContext;
import com.aliyun.mqtt.server.message.MessageQueue;

public class Session {
	
	private static Logger logger = Logger.getLogger("mqtt-server");
	
	private SocketChannel socket = null;
	private MessageQueue messageQueue = null;
	
	private String clientID;
	private boolean sessionOpened = false;
	
	private ByteBuffer buffer = null;
	
	private byte[] array = new byte[256];
	
	public Session(SocketChannel socket) {
		this.socket = socket;
		 messageQueue = new MessageQueue();
	}
	
	public void readResponse() throws IOException {
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
		int count = socket.read(byteBuffer);
		if (count > 0) {
			byteBuffer.flip();
			try {
				for (;;) {
					if (ApplicationContext.getContext().getParser().decodable(byteBuffer)) { /* ok */
						ApplicationContext.getContext().getMessageHandler().handle(this, byteBuffer);
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
	
	public void sendRequest() throws IOException {
		ByteBuffer buffer = messageQueue.get();
		if (buffer != null) {
			socket.write(buffer);
		}
	}
	
	public void write(ByteBuffer buffer) {
		messageQueue.add(buffer);
	}
	
	public void close() {
		if (socket.isConnected()) {
			try {
				socket.close();
				logger.info("Socket has disconnected");
			} catch (IOException e) {
				e.printStackTrace();
				logger.warning("Channel closed to failed: IOException");
			}
		}
		messageQueue.clear();
	}
	
	public void connectCallback(ConnectMessage message) {
		if (0 == message.getAck()) {
			this.clientID = message.getClientID();
			this.sessionOpened = true;
		}
	}

	public String getClientID() {
		return this.clientID;
	}
}
