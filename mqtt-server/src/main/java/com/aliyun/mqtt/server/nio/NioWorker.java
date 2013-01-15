package com.aliyun.mqtt.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

import com.aliyun.mqtt.server.client.Session;

public class NioWorker implements Runnable {
	
	private static Logger logger = Logger.getLogger("mqtt-server");

	private ServerSocketChannel server = null;
	private Selector selector = null;
	
	public NioWorker(ServerSocketChannel server, Selector selector) {
		this.server = server;
		this.selector = selector;
	}
	
	public void run() {
		try {
			while (true) {
				if (!server.isOpen()) {
					break;
				}
				if (selector.select(30) > 0) {
					doSelector();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
		if (key.isAcceptable()) {
			ServerSocketChannel channel = (ServerSocketChannel) key.channel();
			SocketChannel socket = channel.accept();
			socket.configureBlocking(false);
			SelectionKey key1 = socket.register(selector, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
			key1.attach(new Session(socket));
			logger.info("socket connected");
		} else {
			Session client = (Session)key.attachment();
			try {
				if (key.isReadable()) {
					client.readResponse();
				} 
				if (key.isWritable()) {
					client.sendRequest();
				}
			} catch (Exception e) {
				e.printStackTrace();
				client.close();
			}
		}
		
	}

}
