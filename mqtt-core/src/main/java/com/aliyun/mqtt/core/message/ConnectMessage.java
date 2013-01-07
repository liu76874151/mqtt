package com.aliyun.mqtt.core.message;

import com.aliyun.mqtt.core.MQTT;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午10:04
 * To change this template use File | Settings | File Templates.
 */
public class ConnectMessage extends Message {
    public ConnectMessage() {
        this.type = MQTT.MESSAGE_TYPE_CONNECT;
    }

    private boolean hasUsername;
    private boolean hasPassword;
    private boolean willRetain;
    private byte willQos;
    private boolean willFlag;
    private boolean cleanSession;

    private int keepAlive;

    private String clientID;

    private String username;
    private String password;

    public boolean isHasUsername() {
        return hasUsername;
    }

    public void setHasUsername(boolean hasUsername) {
        this.hasUsername = hasUsername;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public boolean isWillRetain() {
        return willRetain;
    }

    public void setWillRetain(boolean willRetain) {
        this.willRetain = willRetain;
    }

    public byte getWillQos() {
        return willQos;
    }

    public void setWillQos(byte willQos) {
        this.willQos = willQos;
    }

    public boolean isWillFlag() {
        return willFlag;
    }

    public void setWillFlag(boolean willFlag) {
        this.willFlag = willFlag;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
