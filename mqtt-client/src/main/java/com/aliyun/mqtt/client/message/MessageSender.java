package com.aliyun.mqtt.client.message;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.Context;
import com.aliyun.mqtt.client.callback.Callback;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.MessageIDMessage;
import com.aliyun.mqtt.core.message.PubRecMessage;
import com.aliyun.mqtt.core.message.PublishMessage;

/**
 * Created with IntelliJ IDEA. User: lijing Date: 13-1-7 Time: 下午10:22 To change
 * this template use File | Settings | File Templates.
 */
public class MessageSender {

	private static final long SCHEDULE_DELAY = 3 * 1000L;
	private static final int SCHEDULE_RETRY = 3;

	private Context context;

	private Logger logger = Logger.getLogger("mqtt-client");

	private Map<String, ScheduledFuture<?>> scheduledFutures = Collections
			.synchronizedMap(new HashMap<String, ScheduledFuture<?>>());

	public MessageSender(Context context) {
		this.context = context;
	}

	public void send(Message message) {
		send(message, null);
	}

	public void send(Message message, Callback callback) {
		if (message.getQos() == MQTT.QOS_LEAST_ONCE) {
			sendQos1((MessageIDMessage) message, 0);
		} else if (message.getQos() == MQTT.QOS_ONCE
				&& message instanceof PublishMessage) {
			sendQos2((PublishMessage) message, 0);
		} else {
			message.setQos(MQTT.QOS_MOST_ONCE);
			send0(message);
		}
	}

	private void sendQos1(final MessageIDMessage message, int tryTimes) {
		String name = MQTT.TYPES.get(message.getType());
		final int t = tryTimes + 1;
		if (t > SCHEDULE_RETRY) {
			if (message instanceof PubRecMessage) {
				context.getMessageStore().getQos2("" + message.getMessageID());
			}
			scheduledFutures.remove(name + message.getMessageID());
			context.getClient().sendQosFailCallback(message);
			return;
		}
		ScheduledFuture<?> future = context.getScheduler().schedule(
				new Runnable() {
					public void run() {
						message.setDup(true);
						sendQos1(message, t);
					}
				}, SCHEDULE_DELAY, TimeUnit.MILLISECONDS);
		scheduledFutures.put(name + message.getMessageID(), future);
		send0(message);
	}

	private void sendQos2(final MessageIDMessage message, int tryTimes) {
		String name = MQTT.TYPES.get(message.getType());
		final int t = tryTimes + 1;
		if (t > SCHEDULE_RETRY) {
			context.getMessageStore().getQos2("" + message.getMessageID());
			scheduledFutures.remove(name + message.getMessageID());
			context.getClient().sendQosFailCallback(message);
			return;
		}
		ScheduledFuture<?> future = context.getScheduler().schedule(
				new Runnable() {
					public void run() {
						message.setDup(true);
						sendQos2(message, t);
					}
				}, SCHEDULE_DELAY, TimeUnit.MILLISECONDS);
		scheduledFutures.put(name + message.getMessageID(), future);
		send0(message);
	}

	public void sendQosAck(String name) {
		ScheduledFuture<?> future = scheduledFutures.remove(name);
		if (future != null) {
			future.cancel(false);
		}
	}

	private void send0(Message message) {
		try {
			logger.info("Send a message of type "
					+ message.getClass().getSimpleName());
			ByteBuffer buffer = context.getParser().encode(message);
			if (buffer != null) {
				context.getMessageQueue().add(buffer);
			}
		} catch (MQTTException e) {
			e.printStackTrace();
		}
	}

}
