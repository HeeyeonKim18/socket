package org.example;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.lang.System.out;
import static java.util.stream.Collectors.toMap;

// "java -jar players-app-{version}.jar <playerName1> <playerName2> <initiatorNic> <message>";
public class Main {
    private static final Options options = new Options();
    private static Map<String, Player> members = new ConcurrentHashMap<>();
    private static String initiatorNic;
    private static String startMessage;
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static Player player;

    public static void main(String[] args) {

        try {

            initChat(args);

//            run();

        } catch (Exception e) {
//            printUsage(e.getMessage());
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

        if (commandLine.getArgs().length < 3) {
            throw new IllegalArgumentException("not enough input parameters");
        }



        try {
            serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            setUpPlayers(commandLine.getArgs()[0].split(","));
            initiatorNic = commandLine.getArgs()[1];
            startMessage = commandLine.getArgs()[2];
            members.get(initiatorNic).sendMessage(startMessage);

            socket = server.startServer();
        }catch (IOException e){
            e.printStackTrace();
        }

        ClientHandler clientHandler = new ClientHandler(socket, members);
        Thread thread = new Thread(clientHandler);
        thread.start();
    }

    private static void setUpPlayers(String... players) {

        members = Arrays.stream(players)
                .map(Player::new)
                .collect(toMap(player -> player.username, Function.identity()));

        members.values().forEach(player -> {

//            bus.subscribe(player);
            members.put(player.username, player);

            out.println(String.format("Player {%s} has been joined to chat.", player.username));
        });
    }

    private static void run() {

//        players.get(initiatorNic).send(startMessage);
//
//        while (true) {
//            if (bus.isEmpty()) {
//                System.out.println("Shutting down ...");
//                exit(0);
//            }
//
//            try {
//                Thread.sleep(100L);
//            } catch (InterruptedException ignored) {
//            }
//        }

    }
}
