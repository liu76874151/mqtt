package com.aliyun.mqtt.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class Client {
	
	private static Logger logger = Logger.getLogger("mqtt-server");
	
	private SocketChannel socket = null;
	
	private String clientID;
	
	public Client(SocketChannel socket) {
		this.socket = socket;
	}
	
	public void readResponse() {
		
	}
	
	public void sendRequest() {
		
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
	}

}
