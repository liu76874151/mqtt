package com.aliyun.mqtt.core.parser;

import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午9:48
 * To change this template use File | Settings | File Templates.
 */
public abstract class Encoder {

    public void encodeHeader(Message message, OutputStream out) throws IOException, MQTTException {
        byte b = 0;
        b = (byte)(0x00 | message.getType() * 0x10 /* bits 7-4 */ | message.getQos() * 0x02 /* bits 2-1 */);
        if (message.isDup()) {
            /* bits 3 */
            b |= 0x08;
        }
        if (message.isRetain()) {
             /* bits 1 */
            b |= 0x01;
        }
        out.write(b);
    }

    protected void encodeRemainLength(int remainLength, OutputStream out) throws IOException, MQTTException {
        if (remainLength > 268435455 || remainLength < 0) {
            throw new MQTTException("Protocol error - remaining length too large");
        }
        do {
            int digit = (byte)(remainLength % 128);
            remainLength = remainLength / 128;
            if (remainLength > 0) {
                digit = (digit | 0x80);
            }
            out.write(digit);
        } while(remainLength > 0);
    }

    protected void encodeLength(int length, OutputStream out) throws IOException, MQTTException {
        out.write((byte) ((length & 0xFF00) >> 8)); //msb
        out.write((byte) (length & 0x00FF)); //lsb
    }

    protected void encodeString(String string, OutputStream out) throws IOException, MQTTException {
        byte[] bs = string.getBytes("UTF-8");
        encodeLength(bs.length, out);
        out.write(bs);
    }

    public abstract ByteBuffer encode(Message message);
}