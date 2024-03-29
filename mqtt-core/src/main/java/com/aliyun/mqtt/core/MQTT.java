package com.aliyun.mqtt.core;

import java.util.HashMap;
import java.util.Map;

public class MQTT {

	public final static String VERSION = "\00\06MQIsdp\03";

	public final static byte MESSAGE_TYPE_CONNECT = 0x01;
	public final static byte MESSAGE_TYPE_CONNACK = 0x02;
	public final static byte MESSAGE_TYPE_PUBLISH = 0x03;
	public final static byte MESSAGE_TYPE_PUBACK = 0x04;
	public final static byte MESSAGE_TYPE_PUBREC = 0x05;
	public final static byte MESSAGE_TYPE_PUBREL = 0x06;
	public final static byte MESSAGE_TYPE_PUBCOMP = 0x07;
	public final static byte MESSAGE_TYPE_SUBSCRIBE = 0x08;
	public final static byte MESSAGE_TYPE_SUBACK = 0x09;
	public final static byte MESSAGE_TYPE_UNSUBSCRIBE = 0x0a;
	public final static byte MESSAGE_TYPE_UNSUBACK = 0x0b;
	public final static byte MESSAGE_TYPE_PINGREQ = 0x0c;
	public final static byte MESSAGE_TYPE_PINGRESP = 0x0d;
	public final static byte MESSAGE_TYPE_DISCONNECT = 0x0e;

	public final static byte QOS_MOST_ONCE = 0;
	public final static byte QOS_LEAST_ONCE = 1;
	public final static byte QOS_ONCE = 2;

	public final static Map<Byte, String> TYPES = new HashMap<Byte, String>();
	static {
		TYPES.put((byte) 0x01, "CONNECT");
		TYPES.put((byte) 0x02, "CONNACK");
		TYPES.put((byte) 0x03, "PUBLISH");
		TYPES.put((byte) 0x04, "PUBACK");
		TYPES.put((byte) 0x05, "PUBREC");
		TYPES.put((byte) 0x06, "PUBREL");
		TYPES.put((byte) 0x07, "PUBCOMP");
		TYPES.put((byte) 0x08, "SUBSCRIBE");
		TYPES.put((byte) 0x09, "SUBACK");
		TYPES.put((byte) 0x0a, "UNSUBSCRIBE");
		TYPES.put((byte) 0x0b, "UNSUBACK");
		TYPES.put((byte) 0x0c, "PINGREQ");
		TYPES.put((byte) 0x0d, "PINGRESP");
		TYPES.put((byte) 0x0e, "DISCONNECT");
	}

}
