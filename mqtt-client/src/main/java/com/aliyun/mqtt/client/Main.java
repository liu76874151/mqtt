package com.aliyun.mqtt.client;

import com.aliyun.mqtt.client.callback.AbstractOnMessageCallback;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-6 Time: 下午8:14 To change
 * this template use File | Settings | File Templates.
 */
public class Main {

	public static void main(String[] args) throws InterruptedException {
		Client client = new Client("10.249.193.120", 9493, "xmwhkhs5u3juJUJf5PD6I6DqwtJ/U/YfayrHoejB/eEG1KR5Vc7EARYa366sWzDJ4=");
		client.setUsername("pubfagy0y218uk4+YqzpR2UVq4u7pjbK/J1PJVbMb81V6v2uajTZQlC8TAOAalTQIGzMV2FwueNVsPMlfNBq6mye16tw==");
		client.setPassword("2kzTGiMhk8L0B0W");
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
//
//		client.subscribe("/topic", MQTT.QOS_ONCE, new AbstractCallback() {
//			public void result(boolean result) {
//				System.out.println(result);
//			}
//		});
//
//		Thread.sleep(1000);
//		
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
