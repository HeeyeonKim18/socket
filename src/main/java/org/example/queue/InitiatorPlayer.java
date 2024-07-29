package org.example.queue;

import java.util.concurrent.BlockingQueue;

class InitiatorPlayer extends Player {
    private final String initMessage;
    protected static String initiator;

    public InitiatorPlayer(BlockingQueue<String> sent, BlockingQueue<String> received,
                           String initiator, String initMessage) {
        super(sent, received, initiator);
        this.initiator = initiator;
        this.initMessage = initMessage;

    }

    @Override
    public void run() {
        sendInitMessage();
        while (true) {
            String receivedMessage = receive();
            reply(receivedMessage);
        }
    }

    private void sendInitMessage() {
        try {
            String message = initMessage + " " + counter.get();
            sent.put(message);
            System.out.printf("[%s]: %s %n", initiator, message);
        } catch (InterruptedException interrupted) {
            String error = String.format(
                    "Player [%s] failed to sent message [%s].",
                    this, initMessage);
            throw new IllegalStateException(error, interrupted);
        }
    }
}
