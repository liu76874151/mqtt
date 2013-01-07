package com.aliyun.mqtt.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-7
 * Time: 下午9:57
 * To change this template use File | Settings | File Templates.
 */
public class PublishMessage extends MessageIDMessage {

    private String topic;

    private byte[] payload;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
