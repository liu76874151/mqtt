package com.aliyun.mqtt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

import com.aliyun.mqtt.core.parser.ConnAckEncoder;
import com.aliyun.mqtt.core.parser.ConnectDecoder;
import com.aliyun.mqtt.core.parser.MQTTParser;
import com.aliyun.mqtt.core.parser.PingReqDecoder;
import com.aliyun.mqtt.core.parser.PingReqEncoder;
import com.aliyun.mqtt.core.parser.PingRespDecoder;
import com.aliyun.mqtt.core.parser.PingRespEncoder;
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
import com.aliyun.mqtt.core.parser.SubscribeDecoder;
import com.aliyun.mqtt.core.parser.SubscribeEncoder;
import com.aliyun.mqtt.server.message.MessageHandler;
import com.aliyun.mqtt.server.message.MessageSender;
import com.aliyun.mqtt.server.nio.NioWorker;

public class Server {

	private static Logger logger = Logger.getLogger("mqtt-server");

	private int port = 1883;

	private ServerSocketChannel server = null;
	private Selector selector = null;
	
	private MQTTParser parser = null;

	public Server(int port) {
		this.port = port;
		init();
	}

	public void init() {
		createSocket();
		
		parser = new MQTTParser();
		parser.registeEncoder(new ConnAckEncoder());
		parser.registeEncoder(new PingReqEncoder());
		parser.registeEncoder(new PingRespEncoder());
		parser.registeEncoder(new SubscribeEncoder());
		parser.registeEncoder(new PublishEncoder());
		parser.registeEncoder(new PubAckEncoder());
		parser.registeEncoder(new PubRecEncoder());
		parser.registeEncoder(new PubRelEncoder());
		parser.registeEncoder(new PubCompEncoder());

		parser.registeDecoder(new ConnectDecoder());
		parser.registeDecoder(new PingReqDecoder());
		parser.registeDecoder(new PingRespDecoder());
		parser.registeDecoder(new SubscribeDecoder());
		parser.registeDecoder(new PublishDecoder());
		parser.registeDecoder(new PubAckDecoder());
		parser.registeDecoder(new PubRecDecoder());
		parser.registeDecoder(new PubRelDecoder());
		parser.registeDecoder(new PubCompDecoder());
		
		ApplicationContext.getContext().registeParser(parser);
		ApplicationContext.getContext().registeMessageHandler(new MessageHandler());
		ApplicationContext.getContext().registeMessageSender(new MessageSender());
	}

	private void createSocket() {
		try {
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			ServerSocket ss = server.socket();
			ss.bind(new InetSocketAddress(this.port));
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			new Thread(new NioWorker(server, selector)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		if (server != null) {
			try {
				server.close();
				logger.info("Server has closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
