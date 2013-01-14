package com.aliyun.mqtt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

import com.aliyun.mqtt.server.nio.NioWorker;

public class Server {
	
	private static Logger logger = Logger.getLogger("mqtt-server");

	private int port = 1883;

	private ServerSocketChannel server = null;
	private Selector selector = null;

	public Server(int port) {
		this.port = port;
		init();
	}

	public void init() {
		try {
			createSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createSocket() throws IOException {
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		ServerSocket ss = server.socket();
		ss.bind(new InetSocketAddress(this.port));
		selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);

		new Thread(new NioWorker(server, selector)).start();
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
