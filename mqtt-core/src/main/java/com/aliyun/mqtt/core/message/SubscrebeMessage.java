package com.aliyun.mqtt.core.message;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.mqtt.core.MQTT;

public class SubscrebeMessage extends MessageIDMessage {
	
	public SubscrebeMessage() {
		this.type = MQTT.MESSAGE_TYPE_SUBSCRIBE;
		setQos(MQTT.QOS_LEAST_ONCE);
	}

	private List<Topic> topics = new ArrayList<Topic>();
	
	public void addTopic(String topic, byte qos) {
		topics.add(new Topic(topic, qos));
	}
	
	public List<Topic> getTopics() {
		return topics;
	}
	
	static class Topic {
		String topic;
		byte qos = 0;
		
		public Topic(String topic, byte qos) {
			super();
			this.topic = topic;
			this.qos = qos;
		}
	}
}
