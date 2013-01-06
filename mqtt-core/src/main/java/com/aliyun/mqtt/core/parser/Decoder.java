package com.aliyun.mqtt.core.parser;

import com.aliyun.mqtt.core.message.Message;

import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午9:37
 * To change this template use File | Settings | File Templates.
 */
public abstract class Decoder {
    public void decodeHeader(Message message, ByteBuffer buffer) {
    }

    public abstract Message decode(ByteBuffer buffer);
}
