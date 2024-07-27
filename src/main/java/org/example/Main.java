package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {
    private static final Options options = new Options();
    private static Map<String, Player> chatMembers = new HashMap<>();
    private static String initiatorNic;
    private static String startMessage;

    public static void main(String[] args) {

        try {

            initChat(args);

            run();

        } catch (Exception e) {
            printUsage(e.getMessage());
        }
    }

    private static void initChat(String... args) {

        out.println("initializing chat ...");

        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (commandLine.hasOption("help")) {
            printUsage();
            exit(0);
        }

        if (commandLine.getArgs().length < 3) {
            throw new IllegalArgumentException("not enough input parameters");
        }

        setUpEventBus(commandLine.getArgs()[0]);

        setUpPlayers(commandLine.getArgs()[1].split(","));

        setUpInitiator(commandLine.getArgs()[2]);

        startMessage = commandLine.getArgs()[3];

        out.println(String.format("chat {%s} started..", bus.getChatName()));
    }

    private static void run() {

        chatMembers.get(initiatorNic).send(startMessage);

        while (true) {
            if (bus.isEmpty()) {
                System.out.println("Shutting down ...");
                exit(0);
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException ignored) {
            }
        }

    }
}
