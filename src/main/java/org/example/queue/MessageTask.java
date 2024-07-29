package org.example.queue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageTask {

    // Blocking queue looks superfluous for single message. But such a queue saves us from cumbersome
    // synchronization of the threads.
    private static final int MAX_MESSAGES_IN_QUEUE = 1;

    private static final Options options = new Options();
    private static String startMessage;
    private static String findFirst;
    private static String findSecond;

    public static void main(String[] args) {
        initChat(args);

        BlockingQueue<String> firstToSecond = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);
        BlockingQueue<String> secondToFirst = new ArrayBlockingQueue<String>(MAX_MESSAGES_IN_QUEUE);

        // Both players use the same queues symmetrically.
        InitiatorPlayer firstPlayer = new InitiatorPlayer(firstToSecond, secondToFirst, findFirst, startMessage);
        Player secondPlayer = new Player(secondToFirst, firstToSecond, findSecond);

        // Please note that we can start threads in reverse order. But thankfully to
        // blocking queues the second player will wait for initialization message from
        // the first player.
        new Thread(secondPlayer).start();
        new Thread(firstPlayer).start();
    }

    private static void initChat(String[] args) {
        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        String[] players = commandLine.getArgs()[0].split(",");
        String initiator = commandLine.getArgs()[1];
        startMessage = commandLine.getArgs()[2];

        findFirst = Arrays.stream(players)
                .filter(player -> player.equals(initiator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Initiator not found in players list"));

        findSecond = Arrays.stream(players)
                .filter(player -> !player.equals(initiator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Names must be different"));
    }
}
