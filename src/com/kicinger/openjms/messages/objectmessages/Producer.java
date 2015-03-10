package com.kicinger.openjms.messages.objectmessages;
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
        TopicConnection connection = null;

        try {
            logger.info("Producer initialization...");
            logger.info("Context creation...");
            context = OpenJMSUtil.createContext("tcp://localhost:3035");

            logger.info("Connection creation...");
            TopicConnectionFactory factory = (TopicConnectionFactory) context.lookup("JmsTopicConnectionFactory");
            connection = factory.createTopicConnection();
            connection.start();

            logger.info("Session and Topic creation...");
            TopicSession session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) context.lookup("objectMessagesTopic");

            TopicPublisher publisher = session.createPublisher(topic);
            logger.info("TopicPublisher creation...");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("Type car make: ");
                String make = br.readLine();
                System.out.print("Type car model: ");
                String model = br.readLine();
                ObjectMessage message = session.createObjectMessage(new Car(model, make));
                publisher.publish(message);
                System.out.println("Message \"" + (Car)message.getObject() + "\" sent.");
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
