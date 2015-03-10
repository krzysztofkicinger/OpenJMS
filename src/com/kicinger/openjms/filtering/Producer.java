package com.kicinger.openjms.filtering;

import com.kicinger.openjms.OpenJMSUtil;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Producer {

    private static final Logger logger = Logger.getLogger(Producer.class);

    public static void main(String[] args) {

        Context context = null;
        QueueConnection connection = null;

        try {
            logger.info("QueueSender initialization...");
            logger.info("Context creation...");
            context = OpenJMSUtil.createContext("tcp://localhost:3035");

            logger.info("Connection creation...");
            QueueConnectionFactory factory = (QueueConnectionFactory) context.lookup("JmsQueueConnectionFactory");
            connection = factory.createQueueConnection();
            connection.start();

            logger.info("Session and Queue creation...");
            QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) context.lookup("filteringQueue");

            logger.info("QueueSender creation...");
            QueueSender sender = session.createSender(queue);

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("Type message: ");
                TextMessage message = session.createTextMessage(br.readLine());
                System.out.print("Type message priority: ");
                message.setIntProperty("num", Integer.parseInt(br.readLine()));
                sender.send(message);
                System.out.println("Message \"" + message.getText() + "\" sent.");
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
