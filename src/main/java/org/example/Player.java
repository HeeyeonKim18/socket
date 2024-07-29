package org.example;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * It handles message exchange by sending and receiving messages through blocking queues,
 * maintains message counters, and processes messages in a continuous loop.
 */
class Player implements Runnable {
    protected final BlockingQueue<String> sent;
    protected final BlockingQueue<String> received;
    protected static final AtomicInteger counter = new AtomicInteger(1);
    protected static String initiator;
    private final String username;

    public Player(BlockingQueue<String> sent, BlockingQueue<String> received, String username) {
        this.sent = sent;
        this.received = received;
        this.username = username;
    }

    @Override
    public void run() {
        while (true) {
            String receivedMessage = receive();
            reply(receivedMessage);
        }
    }

    protected String receive() {
        try {
            String receivedMessage = received.take();
            Thread.sleep(500);
            return receivedMessage;
        } catch (InterruptedException interrupted) {
            throw new IllegalStateException(String.format("[%s] failed to receive message", getUsername()), interrupted);
        }
    }

    protected void reply(String receivedMessage) {
        String replyMessage = getCounter(receivedMessage + " ");
        try {
            sent.put(replyMessage);
            System.out.printf("[%s]: %s %n", getUsername(), replyMessage);
            Thread.sleep(500);
        } catch (InterruptedException interrupted) {
            throw new IllegalStateException(String.format("[%s] failed to send message [%s]", getUsername(), replyMessage), interrupted);
        }
    }

    private String getCounter(String message) {
        if (getUsername().equals(initiator)) {
            message += counter.incrementAndGet();
        } else {
            message += counter.get();
        }

        // stop condition
        if (counter.get() > 10) {
            System.exit(0);
        }
        return message;
    }

    protected String getUsername() {
        return username;
    }
}
