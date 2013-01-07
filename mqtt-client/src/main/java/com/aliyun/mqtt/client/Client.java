package com.aliyun.mqtt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.message.MessageHandler;
import com.aliyun.mqtt.client.message.MessageQueue;
import com.aliyun.mqtt.core.message.ConnectMessage;
import com.aliyun.mqtt.core.parser.ConnAckDecoder;
import com.aliyun.mqtt.core.parser.ConnectEncoder;
import com.aliyun.mqtt.core.parser.MQTTParser;
import com.aliyun.mqtt.nio.NioWorker;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:12 To change
 * this template use File | Settings | File Templates.
 */
public class Client {

	private static Logger logger = Logger.getLogger("mqtt-client");

	private SocketChannel socketChannel;
	private Selector selector = null;
	private String host = "0.0.0.0";
	private int port = 1883;
	private String clientID;
	
	private MQTTParser parser = null;
	private MessageQueue messageQueue = null;
	private MessageHandler handler = null;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
		init();
	}

	public Client(String host, int port, String clientID) {
		this(host, port);
		this.clientID = clientID;

	}

	public void init() {
		parser = MQTTParser.open();
		parser.registeEncoder(new ConnectEncoder());
		parser.registeDecoder(new ConnAckDecoder());
		
		messageQueue = MessageQueue.open();
		handler = new MessageHandler(this);
	}
	
	public void connect() {
		openSocketChannel();
		socketConnet();
		
		ConnectMessage message = new ConnectMessage();
		message.setClientID(clientID);
		message.setKeepAlive(30 * 1000);
		messageQueue.add(parser.encode(message));
	}
	
	public void disconnect() {
		closeSocket();
	}
	
	private void closeSocket() {
		try {
			socketChannel.close();
		} catch (IOException e) {
			logger.warning("Channel closed to failed: IOException");
		}
	}

	private void openSocketChannel() {
		try {
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);
			socketChannel = SocketChannel.open();
			socketChannel.socket().setReuseAddress(true);
			socketChannel.connect(socketAddress);
		} catch (ClosedChannelException e) {
			logger.warning("Channel is closed: ClosedChannelException");
		} catch (IOException e) {
			logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
		}
	}

	private void socketBuilder() {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
			logger.warning("Open to selector failed: IOException");
		}
	}

	private void socketConnet() {
		try {
			if (socketChannel.isOpen()) {
				socketBuilder();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ
						| SelectionKey.OP_WRITE);
				NioWorker nioWorker = new NioWorker(socketChannel, selector, handler);
				new Thread(nioWorker).start();
				logger.info("Has been successfully connected to " + host
						+ "and port:" + port);
			} else {
				socketChannel.close();
			}
		} catch (ClosedChannelException e) {
			logger.warning("Channel is closed: ClosedChannelException");
		} catch (IOException e) {
			logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
		}

	}
}
