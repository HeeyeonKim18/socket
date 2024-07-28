package org.example.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.exit;

class Player implements Runnable
{
    protected final BlockingQueue<String> sent;
    protected final BlockingQueue<String> received;

    // Please aware that integer field may overflow during prolonged run
    // of the program. So after 2147483647 we'll get -2147483648. We can
    // either use BigInteger or compare the field with Integer.MAX_VALUE
    // before each increment.
    //
    private AtomicInteger receiveCount = new AtomicInteger(1);
    private AtomicInteger replyCount = new AtomicInteger(1);

    public Player(BlockingQueue<String> sent, BlockingQueue<String> received)
    {
        this.sent = sent;
        this.received = received;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if(receiveCount.get() > 10 && replyCount.get() > 10) {
                exit(0);
            }
            String receivedMessage = receive();
            reply(receivedMessage);
        }
    }

    protected String receive()
    {
        String receivedMessage;
        try
        {
            // Take message from the queue if available or wait otherwise.
            receivedMessage = received.take();
            Thread.sleep(500);
            receiveCount.incrementAndGet();
        }
        catch (InterruptedException interrupted)
        {
            String error = String.format(
                    "Player [%s] failed to receive message on iteration [%d].",
                    this, receiveCount);
            throw new IllegalStateException(error, interrupted);
        }
        return receivedMessage;
    }

    protected void reply(String receivedMessage)
    {
        String reply = receivedMessage + " " + replyCount;
        try
        {
            // Send message if the queue is not full or wait until one message
            // can fit.
//            if(receiveCount.get() <= 10 && replyCount.get() <= 10) {
                sent.put(reply);
                System.out.printf("Player [%s] sent message: %s %n", this, reply);
                replyCount.incrementAndGet();

                // All players will work fine without this delay. It placed here just
                // for slowing the console output down.
                Thread.sleep(500);
//            }
        }
        catch (InterruptedException interrupted)
        {
            String error = String.format(
                    "Player [%s] failed to send message [%s] on iteration [%d].",
                    this, reply, replyCount);
            throw new IllegalStateException(error);
        }
    }
}
