package com.aliyun.mqtt.client;

import com.aliyun.mqtt.client.callback.AbstractOnMessageCallback;
import com.aliyun.mqtt.client.callback.Callback;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;

public class Main {

	public static void main(String[] args) throws InterruptedException {
//		Client client = new Client("10.249.193.120", 9493, "xmwhkhs5u3juJUJf5PD6I6DqwtJ/U/YfayrHoejB/eEG1KR5Vc7EARYa366sWzDJ4=");
//		client.setUsername("pubfagy0y218uk4+YqzpR2UVq4u7pjbK/J1PJVbMb81V6v2uajTZQlC8TAOAalTQIGzMV2FwueNVsPMlfNBq6mye16tw==");
//		client.setPassword("2kzTGiMhk8L0B0W");
//		client.connect();
		final Client client = new Client("127.0.0.1", 1883, "client1");
		client.setUsername("pubfagy0y218uk4+YqzpR2UVq4u7pjbK/J1PJVbMb81V6v2uajTZQlC8TAOAalTQIGzMV2FwueNVsPMlfNBq6mye16tw==");
		client.setPassword("2kzTGiMhk8L0B0W");
		client.onMessage(new AbstractOnMessageCallback() {
			public void onMessage(String topic, byte[] payload) {
				System.out.println(topic);
				System.out.println(new String(payload));
			}
		});
		client.connect(new Callback<ConnAckMessage>() {
			public void onSuccess(ConnAckMessage value) {
				
				client.subscribe("/topic", MQTT.QOS_ONCE, new Callback<Message>() {
					public void onSuccess(Message value) {
						System.out.println(value);
					}
					public void onFailure(Throwable throwable) {
						System.out.println(throwable);
					}
					
				});
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		
				client.publish("/topic", "hello lijing".getBytes(), MQTT.QOS_LEAST_ONCE,
						false, new Callback<Message>() {
							public void onSuccess(Message value) {
								System.out.println(value);
							}
							public void onFailure(Throwable throwable) {
								System.out.println(throwable);
							}
						});
				client.publish("/topic", "hello lijing1".getBytes(), MQTT.QOS_ONCE);
				client.publish("/topic", "hello lijing2".getBytes(), MQTT.QOS_ONCE);
				client.publish("/topic", "hello lijing3".getBytes(), MQTT.QOS_ONCE);
				client.publish("/topic", "hello lijing4".getBytes(), MQTT.QOS_ONCE);
				
			}
			public void onFailure(Throwable throwable) {
				System.out.println(throwable);
			}
		});
		// client.setDefaultOnMessageCallback(new Callback() {
		// public void callback(Object result) {
		// System.out.println(result);
		// }
		// });
		
	}

}
