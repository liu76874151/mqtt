package com.aliyun.mqtt.client.message;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.aliyun.mqtt.client.Context;
import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.message.ConnAckMessage;
import com.aliyun.mqtt.core.message.Message;
import com.aliyun.mqtt.core.message.PublishMessage;
import com.aliyun.mqtt.core.message.SubAckMessage;

public class MessageHandler {

    private Logger logger = Logger.getLogger("mqtt-client");

    private Context context = null;

    public MessageHandler(Context context) {
        this.context = context;
        this.context.registeMessageHandler(this);
    }

    public void handle(ByteBuffer buffer) {
        Message message = context.getParser().decode(buffer);
        if (message != null) {
            logger.info("Received a message of type " + message.getType());
            switch (message.getType()) {
                case MQTT.MESSAGE_TYPE_CONNACK:
                    handleConnAck(message);
                    break;
                case MQTT.MESSAGE_TYPE_SUBACK:
                    handleSubAck(message);
                    break;
                case MQTT.MESSAGE_TYPE_PUBLISH:
                    handlePublish(message);
                    break;
                case MQTT.MESSAGE_TYPE_PINGRESP:
                    handlePingResp(message);
                    break;
            }
        }
    }

    protected void handleConnAck(Message message) {
        context.getClient().connAckCallback((ConnAckMessage) message);
    }

    protected void handleSubAck(Message message) {
        context.getClient().subAckCallback((SubAckMessage) message);
    }

    protected void handlePublish(Message message) {
        if (message.getQos() == MQTT.QOS_MOST_ONCE) {
            context.getClient().onPublished((PublishMessage)message);
        } else if (message.getQos() == MQTT.QOS_LEAST_ONCE) {
            context.getClient().onPublished((PublishMessage)message);
        }
    }

    protected void handlePingResp(Message message) {

    }

}
