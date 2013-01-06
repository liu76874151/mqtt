package com.aliyun.mqtt.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午9:39
 * To change this template use File | Settings | File Templates.
 */
public abstract class Message {

    protected short type;

    private boolean dup;

    private short qos;

    private boolean retain;

    private int remainLength;

    public short getType() {
        return type;
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

    public int getRemainLength() {
        return remainLength;
    }

    public void setRemainLength(int remainLength) {
        this.remainLength = remainLength;
    }

    public abstract String getTypeName();
}
