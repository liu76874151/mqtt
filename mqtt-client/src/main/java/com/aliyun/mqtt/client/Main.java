package com.aliyun.mqtt.client;

import com.aliyun.mqtt.client.callback.AbstractCallback;
import com.aliyun.mqtt.client.callback.AbstractOnMessageCallback;
import com.aliyun.mqtt.core.MQTT;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:14 To change
 * this template use File | Settings | File Templates.
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		Client client = new Client("127.0.0.1", 1883, "client2");
		client.connect();
		// client.setDefaultOnMessageCallback(new Callback() {
		// public void callback(Object result) {
		// System.out.println(result);
		// }
		// });
		client.setDefaultOnMessageCallback(new AbstractOnMessageCallback() {
			public void onMessage(String topic, byte[] payload) {
				System.out.println(topic);
				System.out.println(new String(payload));
			}
		});

		client.subscribe("/topic", MQTT.QOS_ONCE, new AbstractCallback() {
			public void result(boolean result) {
				System.out.println(result);
			}
		});

		Thread.sleep(1000);
//		client.publish("/topic", "hello lijing".getBytes(), MQTT.QOS_ONCE,
//				false, new AbstractCallback() {
//					@Override
//					public void result(boolean result) {
//						System.out.println("publish result=" + result);
//					}
//				});
//		client.publish("/topic", "hello lijing1".getBytes(), MQTT.QOS_ONCE);
//		client.publish("/topic", "hello lijing2".getBytes(), MQTT.QOS_ONCE);
//		client.publish("/topic", "hello lijing3".getBytes(), MQTT.QOS_ONCE);
//		client.publish("/topic", "hello lijing4".getBytes(), MQTT.QOS_ONCE);
	}

}
