package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * It initializes and manages a messaging system where two players
 * communicate with each other using blocking queues.
 */
public class Application {

    private static final int MAX_MESSAGES_IN_QUEUE = 1;
    private static final Options options = new Options();
    private static String startMessage;
    private static String initiator;
    private static String responder;

    public static void main(String[] args) {
        initChat(args);

        BlockingQueue<String> firstToSecond = new ArrayBlockingQueue<>(MAX_MESSAGES_IN_QUEUE);
        BlockingQueue<String> secondToFirst = new ArrayBlockingQueue<>(MAX_MESSAGES_IN_QUEUE);

        // Create two players
        InitiatorPlayer firstPlayer = new InitiatorPlayer(firstToSecond, secondToFirst, initiator, startMessage);
        Player secondPlayer = new Player(secondToFirst, firstToSecond, responder);

        // Even if the second player is called first, it will wait for a value in the queue before proceeding.
        new Thread(secondPlayer).start();
        new Thread(firstPlayer).start();
    }

    /**
     * Players' names, the starting player, and the message are passed as args.
     * @param args
     */
    private static void initChat(String[] args) {

        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        String[] players = commandLine.getArgs()[0].split(",");
        initiator = commandLine.getArgs()[1];
        startMessage = commandLine.getArgs()[2];

        initiator = Arrays.stream(players)
                .filter(player -> player.equals(initiator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Initiator not found in players list"));

        responder = Arrays.stream(players)
                .filter(player -> !player.equals(initiator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Names must be different"));
    }
}
