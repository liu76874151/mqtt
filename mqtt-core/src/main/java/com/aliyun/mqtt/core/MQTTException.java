package com.aliyun.mqtt.core;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午10:51
 * To change this template use File | Settings | File Templates.
 */
public class MQTTException extends RuntimeException {
    public MQTTException(String message) {
        super(message);
    }

	public MQTTException() {
		super();
	}

	public MQTTException(String message, Throwable cause) {
		super(message, cause);
	}

	public MQTTException(Throwable cause) {
		super(cause);
	}
    
}
