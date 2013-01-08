package com.aliyun.mqtt.client;

import com.aliyun.mqtt.client.callback.AbstractPublishCallback;
import com.aliyun.mqtt.core.MQTT;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:14 To change
 * this template use File | Settings | File Templates.
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		Client client = new Client("127.0.0.1", 1883, "client2");
		client.connect();
		client.subscribe("/topic", MQTT.QOS_ONCE, new AbstractPublishCallback() {
			public void published(String topic, byte[] payload) {
				System.out.println(topic);
				System.out.println(new String(payload));
			}
		});
		
		Thread.sleep(3000);
		client.publish("/topic", "hello lijing".getBytes());
		client.publish("/topic", "hello lijing1".getBytes());
		client.publish("/topic", "hello lijing2".getBytes());
		client.publish("/topic", "hello lijing3".getBytes());
		client.publish("/topic", "hello lijing4".getBytes());
	}

}
