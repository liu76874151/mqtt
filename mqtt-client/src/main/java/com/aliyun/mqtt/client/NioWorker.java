package com.aliyun.mqtt.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午8:19
 * To change this template use File | Settings | File Templates.
 */
public class NioWorker implements Runnable {

    private Logger logger = Logger.getLogger("mqtt-client");

    private SocketChannel socketChannel = null;
    private Selector selector = null;

    private byte[] array = new byte[1024];

    public NioWorker(SocketChannel socketChannel, Selector selector) {
        this.socketChannel = socketChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (!socketChannel.isOpen()) {
                    break;
                }
                if (selector.select(30) > 0) {
                    doSelector();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            stopClient();
        }
    }

    public void doSelector() throws IOException {
        for (SelectionKey key : selector.selectedKeys()) {
            selector.selectedKeys().remove(key);
            if (!key.isValid()) {
                continue;
            }
            doKeys(key);
        }
    }

    public void doKeys(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (key.isReadable()) {
            readResponse(channel);
        }
        if (key.isWritable()) {
            sendRequest(channel);
        }
    }

    private void readResponse(SocketChannel channel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(array);
        int count = channel.read(byteBuffer);
        if(count != -1){
            byteBuffer.flip();
            byte[] bs = new byte[count];
            byteBuffer.get(bs);
            ByteBuffer returnBuffer = ByteBuffer.allocate(count);
            returnBuffer.clear();
            returnBuffer.put(bs);
            returnBuffer.flip();
        }
        if(count < 0) {
            stopClient();
        }
    }

    public void sendRequest(SocketChannel channel) throws IOException {
//        byte[] array = "hello".getBytes();
//        if(array!=null){
//            ByteBuffer byteBuffer = ByteBuffer.wrap(array);
//            channel.write(byteBuffer);
//        }
    }

    public void stopClient(){
        if(socketChannel.isConnected() && !socketChannel.isOpen()){
            try {
                socketChannel.close();
                logger.info("server_socket has connected");
            } catch (IOException e) {
                logger.warning("Channel closed to failed: IOException");
            }
        }
    }
}
