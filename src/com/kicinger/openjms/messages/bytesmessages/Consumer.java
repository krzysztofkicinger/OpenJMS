package com.kicinger.openjms.messages.bytesmessages;

import com.kicinger.openjms.OpenJMSUtil;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;


public class Consumer {

    private static final Logger logger = Logger.getLogger(Consumer.class);

    public static void main(String[] args){

        Context context = null;
        TopicConnection connection = null;

        try {
            logger.info("TopicSubscriber initialization...");
            logger.info("Context creation...");
            context = OpenJMSUtil.createContext("tcp://localhost:3035");

            logger.info("Connection creation...");
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("JmsTopicConnectionFactory");
            connection = factory.createTopicConnection();
            connection.start();

            logger.info("Session and Topic creation...");
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("bytesMessagesTopic");

            logger.info("TopicSubscriber creation...");
            TopicSubscriber subscriber = session.createSubscriber(topic);

            while (true) {
                System.out.println("Waiting for message...");
                BytesMessage message = (BytesMessage) subscriber.receive();
                System.out.println("Received message: " + message.readUTF());
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
