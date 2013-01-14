package com.aliyun.mqtt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.callback.Callback;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.ConnectMessage;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.MessageIDMessage;
import com.aliyun.mqtt.core.message.PingReqMessage;
import com.aliyun.mqtt.core.message.PubRelMessage;
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
import com.aliyun.mqtt.nio.NioWorker;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:12 To change
 * this template use File | Settings | File Templates.
 */
public class Client {

	private static Logger logger = Logger.getLogger("mqtt-client");

	/* CONNECT_TIMEOUT */
	private static final long CONNECT_TIMEOUT = 10 * 1000L;
	/* KEEPALIVE_SECS */
	private static final int KEEPALIVE_SECS = 60;

	private SocketChannel socketChannel;
	private Selector selector = null;
	private String host = "0.0.0.0";
	private int port = 1883;
	private String clientID;

	private Map<String, Callback> registedCallbacks = new HashMap<String, Callback>();
	private Callback defaultOnMessageCallback;

	private MQTTParser parser = null;

	private CountDownLatch connectBarrier;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> heartbeatHandler;

	private ConnAckMessage connAckMessage = null;

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

	public void connect() {
		connect(true);
	}

	public void connect(boolean cleanSession) {
		socketConnet();

		connectBarrier = new CountDownLatch(1);
		ConnectMessage message = new ConnectMessage();
		message.setClientID(clientID);
		message.setKeepAlive(KEEPALIVE_SECS);
		message.setCleanSession(cleanSession);
		addSendMessage(message);
		// suspend until the server respond with CONN_ACK
		boolean unlocked = false;
		try {
			unlocked = connectBarrier.await(CONNECT_TIMEOUT,
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			this.close();
			throw new MQTTException(ex);
		}

		// if not arrive into certain limit, raise an error
		if (!unlocked) {
			this.close();
			throw new MQTTException(
					"Connection timeout elapsed unless server responded with a CONN_ACK");
		}

		if (connAckMessage.getAck() != 0) {
			String errMsg = null;
			switch (connAckMessage.getAck()) {
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
			throw new MQTTException(errMsg);
		}
		this.heartbeat();
	}

	public void setDefaultOnMessageCallback(Callback callback) {
		this.defaultOnMessageCallback = callback;
	}

	public void disconnect() {
		close();
	}

	public void close() {
		if (socketChannel != null && socketChannel.isConnected()) {
			try {
				socketChannel.close();
				logger.info("server_socket has disconnected");
			} catch (IOException e) {
				logger.warning("Channel closed to failed: IOException");
			}
		}
		if (heartbeatHandler != null) {
			heartbeatHandler.cancel(false);
		}
		registedCallbacks.clear();
		if (!scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		context.clear();
	}

	private void socketConnet() {
		try {
			InetSocketAddress socketAddress = new InetSocketAddress(host, port);
			socketChannel = SocketChannel.open();
			socketChannel.socket().setReuseAddress(true);
			socketChannel.connect(socketAddress);
			if (socketChannel.isOpen()) {
				selector = Selector.open();
				socketChannel.configureBlocking(false);
				socketChannel.register(selector, SelectionKey.OP_READ
						| SelectionKey.OP_WRITE);
				NioWorker nioWorker = new NioWorker(socketChannel, selector,
						context);
				new Thread(nioWorker).start();
				logger.info("Has been successfully connected to " + host
						+ "and port:" + port);
			} else {
				close();
			}
		} catch (ClosedChannelException e) {
			logger.warning("Channel is closed: ClosedChannelException");
		} catch (IOException e) {
			logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
		}
	}

	public void registeCallback(String name, Callback callback) {
		this.registedCallbacks.put(name, callback);
	}

	public void connAckCallback(ConnAckMessage message) {
		logger.info("connAckCallback invoked, ackCode=" + message.getAck());
		connAckMessage = message;
		connectBarrier.countDown();
	}

	public void subscribe(String topic) {
		subscribe(topic, MQTT.QOS_MOST_ONCE);
	}

	public void subscribe(String topic, byte qos) {
		subscribe(topic, qos, null);
	}

	public void subscribe(String topic, byte qos, Callback callback) {
		SubscribeMessage message = new SubscribeMessage();
		message.setMessageID(context.nextMessageID());
		message.addTopic(topic, qos);
		addSendMessage(message);
		if (callback != null) {
			registeCallback(
					MQTT.TYPES.get(message.getType()) + message.getMessageID(),
					callback);
		}
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

	public void publish(String topic, byte[] payload, byte qos, boolean retain,
			Callback callback) {
		PublishMessage publishMessage = new PublishMessage();
		publishMessage.setQos(qos);
		publishMessage.setRetain(retain);
		publishMessage.setTopic(topic);
		publishMessage.setPayload(payload);
		publishMessage.setMessageID(context.nextMessageID());
		addSendMessage(publishMessage);
		if (callback != null) {
			if (qos == MQTT.QOS_LEAST_ONCE || qos == MQTT.QOS_ONCE) {
				registeCallback(MQTT.TYPES.get(publishMessage.getType())
						+ publishMessage.getMessageID(), callback);
			} else {
				callback.callback(publishMessage);
			}
		}
		this.heartbeat();
	}

	public void sendQosCallback(String name, MessageIDMessage message) {
		logger.info(message.getClass().getSimpleName()
				+ " callback invoked, messageID=" + message.getMessageID());
		Callback callback = registedCallbacks.remove(name);
		if (callback != null) {
			callback.callback(message);
		}
	}

	public void sendQosFailCallback(MessageIDMessage message) {
		logger.info(message.getClass().getSimpleName()
				+ " fail callback invoked, messageID=" + message.getMessageID());
		byte type = message.getType();
		if (message instanceof PubRelMessage) {
			/* publish fail */
			type = MQTT.MESSAGE_TYPE_PUBLISH;
		}
		Callback callback = registedCallbacks.remove(MQTT.TYPES.get(type)
				+ message.getMessageID());
		if (callback != null) {
			callback.callback(null);
		}
	}

	public void onMessage(PublishMessage publishMessage) {
		logger.info("Received a publish message : messageID="
				+ publishMessage.getMessageID() + "\nqos="
				+ publishMessage.getQos() + "\ntopic="
				+ publishMessage.getTopic());
		String topic = publishMessage.getTopic();
		Callback callback = registedCallbacks.get("ONMESSAGE_" + topic);
		if (callback == null) {
			callback = defaultOnMessageCallback;
		}
		if (callback != null) {
			callback.callback(publishMessage);
		}
	}

	final Runnable pingreqDeamon = new Runnable() {
		public void run() {
			addSendMessage(new PingReqMessage());
		}
	};

	public void heartbeat() {
		if (heartbeatHandler != null) {
			heartbeatHandler.cancel(false);
		}
		heartbeatHandler = scheduler.scheduleWithFixedDelay(pingreqDeamon,
				KEEPALIVE_SECS, KEEPALIVE_SECS, TimeUnit.SECONDS);
	}

	protected void addSendMessage(Message message) {
		context.getSender().send(message);
	}

	public Context getContext() {
		return context;
	}

}
