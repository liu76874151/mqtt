package com.aliyun.mqtt.client.callback;

import com.aliyun.mqtt.core.message.Message;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-7
 * Time: 下午9:51
 * To change this template use File | Settings | File Templates.
 */
public interface Callback {
    public void callback(Message message);
}