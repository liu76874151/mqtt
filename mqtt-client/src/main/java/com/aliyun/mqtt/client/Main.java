package com.aliyun.mqtt.client;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:14 To change
 * this template use File | Settings | File Templates.
 */
public class Main {

	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 1883, "client1");
		client.connect();
		client.subscribe("/topic");
	}

}
