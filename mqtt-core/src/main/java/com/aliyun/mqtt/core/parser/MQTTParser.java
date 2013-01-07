package com.aliyun.mqtt.core.parser;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.mqtt.core.MQTT;
import com.aliyun.mqtt.core.MQTTException;
import com.aliyun.mqtt.core.message.Message;

public class MQTTParser {
	
	private Map<String, Encoder> encoders = new HashMap<String, Encoder>();
	private Map<String, Decoder> decoders = new HashMap<String, Decoder>();
	
	private static MQTTParser parser = null;
	
	private MQTTParser() {
	}
	
	public static MQTTParser open() {
		if(parser == null) {
			parser = new MQTTParser();
		}
		return parser;
	}
	
	public void registeEncoder(Encoder encoder) {
		String name = encoder.getClass().getSimpleName().replace("Encoder", "").toUpperCase();
		encoders.put(name, encoder);
	}
	
	public void registeDecoder(Decoder encoder) {
		String name = encoder.getClass().getSimpleName().replace("Decoder", "").toUpperCase();
		decoders.put(name, encoder);
	}
	
	public ByteBuffer encode(Message message) {
		String name = message.getClass().getSimpleName().replace("Message", "").toUpperCase();
		Encoder encoder = encoders.get(name);
		if (encoder == null) {
			throw new MQTTException("Encoder of name '" + name + "' not registed");
		}
		return encoder.encode(message);
	}
	
	public Message decode(ByteBuffer buffer) {
		String name = MQTT.TYPES.get((byte)(buffer.get() >> 4));
		if(name == null) {
			throw new MQTTException("Message type error");
		}
		buffer.rewind();
		Decoder decoder = decoders.get(name);
		if (decoder == null) {
			throw new MQTTException("Decoder of name '" + name + "' not registed");
		}
		return decoder.decode(buffer);
	}

}
