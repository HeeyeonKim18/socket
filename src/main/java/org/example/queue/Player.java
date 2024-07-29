package org.example.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;
import static org.example.queue.InitiatorPlayer.initiator;

class Player implements Runnable {
    protected final BlockingQueue<String> sent;
    protected final BlockingQueue<String> received;

    protected static final AtomicInteger counter = new AtomicInteger(1);
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
        String receivedMessage;
        try {
            // Take message from the queue if available or wait otherwise.
            receivedMessage = received.take();
            Thread.sleep(500);
        } catch (InterruptedException interrupted) {
            String error = String.format(
                    "Player [%s] failed to receive message on iteration [%d].",
                    this, counter);
            throw new IllegalStateException(error, interrupted);
        }
        return receivedMessage;
    }

    protected void reply(String receivedMessage) {
        String reply = receivedMessage + " ";
        reply = getCounter(reply);
        try {
            // Send message if the queue is not full or wait until one message
            // can fit.
            sent.put(reply);
            System.out.printf("[%s]: %s %n", username, reply);


            // All players will work fine without this delay. It placed here just
            // for slowing the console output down.
            Thread.sleep(500);
        } catch (InterruptedException interrupted) {
            String error = String.format(
                    "Player [%s] failed to send message [%s] on iteration [%n].",
                    this, reply, counter);
            throw new IllegalStateException(error);
        }
    }

    private String getCounter(String reply) {
        if (username.equals(initiator)) reply += counter.incrementAndGet();
        else reply += counter.get();

        if (counter.get() > 10) {
            exit(0);
        }
        return reply;
    }
}
