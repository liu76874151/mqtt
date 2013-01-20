package com.aliyun.mqtt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.callback.Callback;
import com.aliyun.mqtt.client.nio.NioWorker;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.ConnectMessage;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PingReqMessage;
import com.aliyun.mqtt.core.message.PublishMessage;
import com.aliyun.mqtt.core.message.SubscribeMessage;
import com.aliyun.mqtt.core.parser.ConnAckDecoder;
import com.aliyun.mqtt.core.parser.ConnectEncoder;
import com.aliyun.mqtt.core.parser.MQTTParser;
import com.aliyun.mqtt.core.parser.PingReqEncoder;
import com.aliyun.mqtt.core.parser.PingRespDecoder;
import com.aliyun.mqtt.core.parser.PubAckDecoder;
import com.aliyun.mqtt.core.parser.PubAckEncoder;
import com.aliyun.mqtt.core.parser.PubCompDecoder;
import com.aliyun.mqtt.core.parser.PubCompEncoder;
import com.aliyun.mqtt.core.parser.PubRecDecoder;
import com.aliyun.mqtt.core.parser.PubRecEncoder;
import com.aliyun.mqtt.core.parser.PubRelDecoder;
import com.aliyun.mqtt.core.parser.PubRelEncoder;
import com.aliyun.mqtt.core.parser.PublishDecoder;
import com.aliyun.mqtt.core.parser.PublishEncoder;
import com.aliyun.mqtt.core.parser.SubAckDecoder;
import com.aliyun.mqtt.core.parser.SubscribeEncoder;

public class Client {

	private static Logger logger = Logger.getLogger("mqtt-client");

	/* CONNECT_TIMEOUT */
	private static final long CONNECT_TIMEOUT = 3 * 1000L;
	private static final long RECONNECT_TIMES = 3;
	
	/* KEEPALIVE_SECS */
	private static final int KEEPALIVE_SECS = 6;

	private SocketChannel socketChannel;
	private Selector selector = null;
	private String host = "0.0.0.0";
	private int port = 1883;
	private String clientID;
	private String username;
	private String password;
	private boolean closed = true;

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	private Callback<PublishMessage> publishCallback;

	private MQTTParser parser = null;

	private NioWorker nioWorker;
	
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> heartbeatHandler;
	private ScheduledFuture<?> connectHandler;
	private ScheduledFuture<?> reconnectHandler;
	private Callback<ConnAckMessage> connectCallback;

	private Context context = new Context(this);

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
		parser = new MQTTParser();
		parser.registeEncoder(new ConnectEncoder());
		parser.registeEncoder(new PingReqEncoder());
		parser.registeEncoder(new SubscribeEncoder());
		parser.registeEncoder(new PublishEncoder());
		parser.registeEncoder(new PubAckEncoder());
		parser.registeEncoder(new PubRecEncoder());
		parser.registeEncoder(new PubRelEncoder());
		parser.registeEncoder(new PubCompEncoder());

		parser.registeDecoder(new ConnAckDecoder());
		parser.registeDecoder(new PingRespDecoder());
		parser.registeDecoder(new SubAckDecoder());
		parser.registeDecoder(new PublishDecoder());
		parser.registeDecoder(new PubAckDecoder());
		parser.registeDecoder(new PubRecDecoder());
		parser.registeDecoder(new PubRelDecoder());
		parser.registeDecoder(new PubCompDecoder());

		context.registeParser(parser);
		scheduler = Executors.newScheduledThreadPool(1);
		context.registeScheduler(scheduler);
	}

	public void connect(Callback<ConnAckMessage> connectCallback) {
		connect(true, connectCallback);
	}

	public void connect(boolean cleanSession, Callback<ConnAckMessage> connectCallback) {
		this.connectCallback = connectCallback;
		try {
			socketConnet();
		} catch (IOException e) {
			this.connectCallback.onFailure(e);
			return;
		}
		
		/* add to the head of queue */
		context.getSender().sendNow(buildConnectMessage(cleanSession));
		
		nioWorker = new NioWorker(socketChannel, selector, context);
		new Thread(nioWorker).start();
		
		connectHandler = scheduler.schedule(new Runnable() {
			public void run() {
				/* Connection timeout (no CONNACK) */
				Client.this.close();
				Client.this.connectCallback.onFailure(new MQTTException("Connection timeout (no CONNACK)"));
			}
		}, CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
	private void socketConnet() throws IOException {
		try {
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);
			socketChannel = SocketChannel.open();
			socketChannel.socket().setReuseAddress(true);
			socketChannel.connect(socketAddress);
			selector = Selector.open();
			socketChannel.configureBlocking(false);
			socketChannel.register(selector, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
			logger.info("Connected to " + host + " and port:" + port);
		} catch (IOException e) {
			logger.warning("Connecting fail or time out : IOException " + e.getMessage());
			throw e;
		}
	}
	
	private ConnectMessage buildConnectMessage(boolean cleanSession) {
		ConnectMessage message = new ConnectMessage();
		message.setClientID(clientID);
		if (this.username != null) {
			message.setHasUsername(true);
			message.setUsername(this.username);
		}
		if (this.password != null) {
			message.setHasPassword(true);
			message.setPassword(password);
		}
		message.setKeepAlive(KEEPALIVE_SECS);
		message.setCleanSession(cleanSession);
		return message;
	}

	/**
	 * onMessage callback
	 * @param callback
	 */
	public void onMessage(Callback<PublishMessage> callback) {
		this.publishCallback = callback;
	}

	public void disconnect() {
		close();
	}

	/**
	 * close session
	 */
	public void close() {
		closed = true;
		if (socketChannel != null && socketChannel.isConnected()) {
			try {
				socketChannel.close();
				logger.info("Socket disconnect");
			} catch (IOException e) {
				logger.warning("Channel closed to failed: IOException");
			}
		}
		if (heartbeatHandler != null) {
			heartbeatHandler.cancel(false);
		}
		if (this.reconnectHandler != null) {
			this.reconnectHandler.cancel(false);
		}
		if (!scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		context.clear();
	}

	/**
	 * ConnAck
	 * @param message
	 */
	public void connAckCallback(ConnAckMessage message) {
		logger.info("connAckCallback invoked, ackCode=" + message.getAck());
		
		/* it's a reconnect ack */
		if (this.reconnectHandler != null) {
			this.reconnectHandler.cancel(false);
			this.reconnectHandler = null;
			if (message.getAck() == 0) {
				this.heartbeat();
			} else {
				close();
			}
			return;
		}
		
		/* it's the first connect ack */
		if (this.connectHandler.isDone()) {
			return;
		}
		this.connectHandler.cancel(false);
		
		if (message.getAck() != 0) {
			String errMsg = null;
			switch (message.getAck()) {
			case 1:
				errMsg = "Unacceptable protocol version";
				break;
			case 2:
				errMsg = "Identifier rejected";
				break;
			case 3:
				errMsg = "Server unavailable";
				break;
			case 4:
				errMsg = "Bad username or password";
				break;
			case 5:
				errMsg = "Not authorized";
				break;
			default:
				break;
			}
			this.close();
			this.connectCallback.onFailure(new MQTTException(errMsg));
			return;
		}
		closed = false;
		this.connectCallback.onSuccess(message);
		this.heartbeat();
	}

	public void subscribe(String topic) {
		subscribe(topic, MQTT.QOS_MOST_ONCE);
	}

	public void subscribe(String topic, byte qos) {
		subscribe(topic, qos, null);
	}

	/**
	 * subscribe a topic
	 * @param topic
	 * @param qos
	 * @param callback
	 */
	public void subscribe(String topic, byte qos, Callback<Message> callback) {
		if (closed()) {
			if (callback != null) {
				callback.onFailure(new MQTTException("Session closed"));
			}
			return;
		}
		SubscribeMessage message = new SubscribeMessage();
		message.setMessageID(context.nextMessageID());
		message.addTopic(topic, qos);
		addSendMessage(message, callback);
		this.heartbeat();
	}

	public void publish(String topic, byte[] payload) {
		publish(topic, payload, MQTT.QOS_MOST_ONCE);
	}

	public void publish(String topic, byte[] payload, byte qos) {
		publish(topic, payload, qos, false);
	}

	public void publish(String topic, byte[] payload, byte qos, boolean retain) {
		publish(topic, payload, qos, retain, null);
	}

	/**
	 * publish a message
	 * @param topic
	 * @param payload
	 * @param qos
	 * @param retain
	 * @param callback
	 */
	public void publish(String topic, byte[] payload, byte qos, boolean retain,
			Callback<Message> callback) {
		if (closed()) {
			if (callback != null) {
				callback.onFailure(new MQTTException("Session closed"));
			}
			return;
		}
		PublishMessage publishMessage = new PublishMessage();
		publishMessage.setQos(qos);
		publishMessage.setRetain(retain);
		publishMessage.setTopic(topic);
		publishMessage.setPayload(payload);
		publishMessage.setMessageID(context.nextMessageID());
		addSendMessage(publishMessage, callback);
		this.heartbeat();
	}

	/**
	 * receive publish message
	 * @param publishMessage
	 */
	public void messageReceived(PublishMessage publishMessage) {
		logger.info("Received a publish message : messageID="
				+ publishMessage.getMessageID() + "\nqos="
				+ publishMessage.getQos() + "\ntopic="
				+ publishMessage.getTopic());
		if (this.publishCallback != null) {
			this.publishCallback.onSuccess(publishMessage);
		}
	}

	final Runnable pingreqDeamon = new Runnable() {
		public void run() {
			addSendMessage(new PingReqMessage(), null);
		}
	};

	/**
	 * heartbeat/send a PingReq message
	 */
	public void heartbeat() {
		if (heartbeatHandler != null) {
			heartbeatHandler.cancel(false);
		}
		heartbeatHandler = scheduler.scheduleWithFixedDelay(pingreqDeamon,
				KEEPALIVE_SECS, KEEPALIVE_SECS, TimeUnit.SECONDS);
	}

	protected void addSendMessage(Message message, Callback<Message> callback) {
		context.getSender().send(message, callback);
	}

	public Context getContext() {
		return context;
	}
	
	public boolean closed() {
		return closed;
	}
	
	/**
	 * reconnect to mqtt server
	 */
	public void reconnect() {
		reconnect(1);
	}
	
	private void reconnect(final int attempt) {
		if (attempt > RECONNECT_TIMES) {
			this.reconnectHandler = null;
			logger.warning("Reconnect attempt too muth, close");
			close();
			return;
		}
		logger.info("Reconnect attempt " + attempt);
		nioWorker.stop();
		try {
			socketChannel.close();
			socketConnet();
			context.getSender().sendNow(buildConnectMessage(false));
			nioWorker = new NioWorker(socketChannel, selector, context);
			new Thread(nioWorker).start();
		} catch (IOException e) {
		}
		/* add to the head of queue */
		this.reconnectHandler = scheduler.schedule(new Runnable() {
			public void run() {
				reconnect(attempt + 1);
			}
		}, CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
	}
	
}
