package org.example.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageTask {

    // Blocking queue looks superfluous for single message. But such a queue saves us from cumbersome
    // synchronization of the threads.
    private static final int MAX_MESSAGES_IN_QUEUE = 1;

    public static void main(String[] args) {
        BlockingQueue<String> firstToSecond = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);
        BlockingQueue<String> secondToFirst = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);

        // Both players use the same queues symmetrically.
        InitiatorPlayer firstPlayer = new InitiatorPlayer(firstToSecond, secondToFirst, "first");
        Player secondPlayer = new Player(secondToFirst, firstToSecond, "second");

        // Please note that we can start threads in reverse order. But thankfully to
        // blocking queues the second player will wait for initialization message from
        // the first player.
        new Thread(secondPlayer).start();
        new Thread(firstPlayer).start();
    }
}
