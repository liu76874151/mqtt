package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PublishMessage;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午9:56 To change
 * this template use File | Settings | File Templates.
 */
public abstract class AbstractPublishCallback implements IPublishCallback {

	public void callback(Message message) {
		PublishMessage publishMessage = (PublishMessage) message;
		published(publishMessage.getTopic(), publishMessage.getPayload());
	}
}
