package com.kicinger.openjms.rmi;

import com.kicinger.openjms.OpenJMSUtil;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Producer {

    private static final Logger logger = Logger.getLogger(Producer.class);

    public static void main(String[] args){
    	
    	Context context = null;
    	TopicConnection connection = null;
    	
        try {
            logger.info("Producer initialization...");
            logger.info("Context creation...");
            context = OpenJMSUtil.createContext("rmi://localhost:1099");

            logger.info("Connection creation...");
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("JmsTopicConnectionFactory");
            connection = factory.createTopicConnection();
            connection.start();

            logger.info("Session and Topic creation...");
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("simpleTopic");

            TopicPublisher publisher = session.createPublisher(topic);
            logger.info("TopicPublisher creation...");
            
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while(true){
                System.out.print("Type message: ");
                TextMessage message = session.createTextMessage(br.readLine());
                publisher.publish(message);
                System.out.println("Message \"" + message.getText() + "\" sent.");
            }   
            
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
			
            // close the context
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException exception) {
                    exception.printStackTrace();
                }
            }

            // close the connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
