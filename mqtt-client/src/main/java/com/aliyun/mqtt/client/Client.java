package com.aliyun.mqtt.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: lijing
 * Date: 13-1-6
 * Time: 下午8:12
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    private static Logger logger = Logger.getLogger("mqtt-client");

    private SocketChannel socketChannel;
    private Selector selector = null;
    private String host = "0.0.0.0";
    private int port = 1883;
    private String clientId;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        init();
    }

    public Client(String host, int port, String clientId) {
        this(host, port);
        this.clientId = clientId;

    }

    public void init() {
        openSocketChannel();
        socketConnet();
    }

    private void openSocketChannel() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            socketChannel = SocketChannel.open();
            socketChannel.socket().setReuseAddress(true);
            socketChannel.connect(socketAddress);
        } catch (ClosedChannelException e) {
            logger.warning("Channel is closed: ClosedChannelException");
        } catch (IOException e) {
            logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
        }
    }

    public void socketBuilder() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
            logger.warning("Open to selector failed: IOException");
        }
    }

    public void socketConnet() {
        try {
            if (socketChannel.isOpen()) {
                socketBuilder();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                NioWorker nioWorker = new NioWorker(socketChannel, selector);
                ExecutorServiceUtil.execute(nioWorker);
                ExecutorServiceUtil.shutdown();
                logger.info("Has been successfully connected to " + host  + "and port:" + port);
            } else {
                socketChannel.close();
            }
        } catch (ClosedChannelException e) {
            logger.warning("Channel is closed: ClosedChannelException");
        } catch (IOException e) {
            logger.warning("Connet is failed or time out,the system will automatically re-connected : IOException");
        }

    }
}
