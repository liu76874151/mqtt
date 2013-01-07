package com.aliyun.mqtt.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午10:02
 * To change this template use File | Settings | File Templates.
 */
public class MQTT {

    public final static String VERSION = "\00\06MQIsdp\03";

    public final static byte MESSAGE_TYPE_CONNECT = 0x01;
    public final static byte MESSAGE_TYPE_CONNACK = 0x02;

    public final static byte QOS_MOST_ONCE = 0;
    public final static byte QOS_LEAST_ONCE = 1;
    public final static byte QOS_ONCE = 2;
    
    public final static Map<Byte, String> TYPES = new HashMap<Byte, String>();
    static {
    	TYPES.put((byte)0x01, "CONNECT");
    	TYPES.put((byte)0x02, "CONNACK");
    	TYPES.put((byte)0x03, "PUBLISH");
    	TYPES.put((byte)0x04, "PUBACK");
    	TYPES.put((byte)0x05, "PUBREC");
    	TYPES.put((byte)0x06, "PUBREL");
    	TYPES.put((byte)0x07, "PUBCOMP");
    	TYPES.put((byte)0x08, "SUBSCRIBE");
    	TYPES.put((byte)0x09, "SUBACK");
    	TYPES.put((byte)0x0a, "UNSUBSCRIBE");
    	TYPES.put((byte)0x0b, "UNSUBACK");
    	TYPES.put((byte)0x0c, "PINGREQ");
    	TYPES.put((byte)0x0d, "PINGRESP");
    	TYPES.put((byte)0x0e, "DISCONNECT");
    }

}
