package org.example.queue;

import java.util.concurrent.BlockingQueue;

class InitiatorPlayer extends Player {
    private static final String INIT_MESSAGE = "initiator player";

    public InitiatorPlayer(BlockingQueue<String> sent, BlockingQueue<String> received, String order) {
        super(sent, received, order);
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
            String message = INIT_MESSAGE + " " + counter.get();
            sent.put(message);
            System.out.printf("Player [%s] sent message: %s %n", this, message);
        } catch (InterruptedException interrupted) {
            String error = String.format(
                    "Player [%s] failed to sent message [%s].",
                    this, INIT_MESSAGE);
            throw new IllegalStateException(error, interrupted);
        }
    }
}
