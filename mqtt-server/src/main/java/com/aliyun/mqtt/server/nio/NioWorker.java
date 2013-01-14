package com.aliyun.mqtt.server.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.aliyun.mqtt.server.Client;

public class NioWorker implements Runnable {

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
			socket.register(selector, SelectionKey.OP_READ
					| SelectionKey.OP_WRITE);
			key.attach(new Client(socket));
		} else {
			if (key.isReadable()) {
				Client client = (Client)key.attachment();
				client.readResponse();
			} else if (key.isWritable()) {
				Client client = (Client)key.attachment();
				client.sendRequest();
			}
		}
		
	}

}
