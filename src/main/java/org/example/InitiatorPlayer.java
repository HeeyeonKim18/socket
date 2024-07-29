package org.example;

import java.util.concurrent.BlockingQueue;

/**
 * The InitiatorPlayer class, which extends the Player class,
 * sets the initial message and performs the default actions of the Player.
 */
class InitiatorPlayer extends Player {
    private final String initMessage;

    public InitiatorPlayer(BlockingQueue<String> sent, BlockingQueue<String> received, String username, String initMessage) {
        super(sent, received, username);
        this.initMessage = initMessage;
        Player.initiator = username;
    }

    @Override
    public void run() {
        sendInitMessage();
        super.run();
    }

    private void sendInitMessage() {
        try {
            String message = initMessage + " " + counter.get();
            sent.put(message);
            System.out.printf("[%s]: %s %n", getUsername(), message);
        } catch (InterruptedException interrupted) {
            throw new IllegalStateException(String.format("[%s] failed to send message [%s].", getUsername(), initMessage), interrupted);
        }
    }
}
