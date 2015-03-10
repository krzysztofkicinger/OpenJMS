package com.kicinger.openjms.filtering;

import com.kicinger.openjms.OpenJMSUtil;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;

public class Consumer {

    private static final Logger logger = Logger.getLogger(Consumer.class);

    public static void main(String[] args) {

        Context context = null;
        QueueConnection connection = null;

        try {
            logger.info("QueueReceiver initialization...");
            logger.info("Context creation...");
            context = OpenJMSUtil.createContext("tcp://localhost:3035");

            logger.info("Connection creation...");
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("JmsQueueConnectionFactory");
            connection = factory.createQueueConnection();
            connection.start();

            logger.info("Session and Queue creation...");
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) context.lookup("filteringQueue");

            logger.info("QueueReceiver creation...");
            QueueReceiver receiver = session.createReceiver(queue, "num > 5");

            while (true) {
                System.out.println("Waiting for message...");
                TextMessage message = (TextMessage) receiver.receive();         // blocking
                System.out.println("Received message: " + message.getText());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (context != null) {
                    context.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (NamingException | JMSException ex) {
                ex.printStackTrace();
            }
        }
    }
}
