package com.aliyun.mqtt.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午9:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class Message {

    private Header header;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
