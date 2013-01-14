package com.aliyun.mqtt.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {

	private String host = "0.0.0.0";
	private int port = 1883;

	private SocketChannel client = null;

	private ServerSocketChannel server = null;

	public Server(String host, int port) {
		this.host = host;
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
		Selector selector = Selector.open();
		server.register(selector, SelectionKey.OP_ACCEPT);

		while (true) {
			// 获取通道内是否有选择器的关心事件
			int num = selector.select();
			// 如果小于1,停止此次循环,进行下一个循环
			if (num < 1) {
				continue;
			}
			// 获取通道内关心事件的集合
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = selectedKeys.iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				// 移走此次事件
				iterator.remove();

				if (key.isAcceptable()) {
					// 获取对应的SocketChannel
					SocketChannel client = server.accept();
					System.out.println("Accepted connection from " + client);
					// 使此通道为非阻塞
					client.configureBlocking(false);
					// 在此通道上注册事件
					SelectionKey key2 = client.register(selector,
							SelectionKey.OP_WRITE);
				} else if (key.isWritable()) {
					// 获取此通道的SocketChannel
					SocketChannel client = (SocketChannel) key.channel();
					ByteBuffer output = (ByteBuffer) key.attachment();
					// 如果缓存区没了,重置一下
					if (!output.hasRemaining()) {
						output.rewind();
					}
					// 在此通道内写东西
					client.write(output);
				}
				key.channel().close();
			}
		}
	}

	class Client {

	}
}
