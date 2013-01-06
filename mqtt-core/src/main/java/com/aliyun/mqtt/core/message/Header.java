package com.aliyun.mqtt.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午9:46
 * To change this template use File | Settings | File Templates.
 */
public class Header {

    private String type;

    private boolean dup;

    private short qos;

    private boolean retain;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }

    public short getQos() {
        return qos;
    }

    public void setQos(short qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }
}
