package org.example;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 3. when a player receives a message should send back a new message that contains the received message concatenated with the message counter that this player sent.
 *
 * 4. finalize the program (gracefully) after the initiator sent 10 messages and received back 10 messages (stop condition)
 *
 * 5. both players should run in the same java process (strong requirement)
 *
 * 6. document for every class the responsibilities it has.
 *
 * 7. opposite to 5: have every player in a separate JAVA process (different PID).
 */
public class Player {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private final AtomicInteger sendCounter = new AtomicInteger();
    private final AtomicInteger receiveCounter = new AtomicInteger();

    public Player(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);

        }
    }

    public void sendMessage(String message){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

//            Scanner scanner = new Scanner(System.in);
            int sendCount = sendCounter.incrementAndGet();
            while(socket.isConnected()){
                if(sendCount <= 10 && receiveCounter.get() <= 10) {
//                    String messageToSend = scanner.nextLine();
//                bufferedWriter.write(username + ": " + messageToSend);
                    bufferedWriter.write(username + ": " + message + ", " + sendCount++);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }else{
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(){
        new Thread(()->{
            String msgFromGroupChat;

            while(socket.isConnected()){
                try{
                    msgFromGroupChat = bufferedReader.readLine();
                    System.out.println(msgFromGroupChat);
                    receiveCounter.incrementAndGet();
                }catch(IOException e){
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter!= null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Player player1 = new Player(socket, username);
        player1.listenForMessage();
        player1.sendMessage("hey");
    }
}
