package com.aliyun.mqtt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.message.MessageHandler;
import com.aliyun.mqtt.client.message.MessageQueue;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.ConnAckMessage;
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

	private static final int KEEPALIVE_SECS = 3;
	
	private SocketChannel socketChannel;
	private Selector selector = null;
	private String host = "0.0.0.0";
	private int port = 1883;
	private String clientID;
	
	private MQTTParser parser = null;
	private MessageQueue messageQueue = null;
	private MessageHandler handler = null;
	
	private CountDownLatch connectBarrier;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture heartbeatHandler;
	
	private ConnAckMessage connAckMessage = null;

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
		scheduler = Executors.newScheduledThreadPool(1);
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
		messageQueue.add(parser.encode(message));
		//suspend until the server respond with CONN_ACK
        boolean unlocked = false;
		try {
            unlocked = connectBarrier.await(30 * 1000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            throw new MQTTException(ex);
        }

        //if not arrive into certain limit, raise an error
        if (!unlocked) {
            throw new MQTTException("Connection timeout elapsed unless server responded with a CONN_ACK");
        }
        
        if(connAckMessage.getAck() != 0) {
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
        	throw new MQTTException(errMsg);
        }
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
				NioWorker nioWorker = new NioWorker(socketChannel, selector, handler);
				new Thread(nioWorker).start();
				logger.info("Has been successfully connected to " + host
						+ "and port:" + port);
			} else {
				closeSocket();
			}
		} catch (ClosedChannelException e) {
			logger.warning("Channel is closed: ClosedChannelException");
		} catch (IOException e) {
			logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
		}
	}
	
	public void subscribe(String topic, IMessageCallback callback) {
		
	}
	
	public void connAckCallback(ConnAckMessage message) {
		logger.info("connAckCallback invoked, ackCode=" + message.getAck());
		connAckMessage = message;
		connectBarrier.countDown();
	}
	
	final Runnable pingreqDeamon = new Runnable() {
		public void run() {
			
		}
	};
	public void heartbeat() {
		if (heartbeatHandler != null) {
			heartbeatHandler.cancel(false);
        }
		heartbeatHandler = scheduler.scheduleWithFixedDelay(pingreqDeamon, KEEPALIVE_SECS, KEEPALIVE_SECS, TimeUnit.SECONDS);
	}
}
