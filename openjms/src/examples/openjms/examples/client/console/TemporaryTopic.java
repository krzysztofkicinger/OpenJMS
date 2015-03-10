/**
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright
 *    statements and notices.  Redistributions must also contain a
 *    copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the
 *    above copyright notice, this list of conditions and the
 *    following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 *
 * 3. The name "Exolab" must not be used to endorse or promote
 *    products derived from this Software without prior written
 *    permission of Exoffice Technologies.  For written permission,
 *    please contact info@exolab.org.
 *
 * 4. Products derived from this Software may not be called "Exolab"
 *    nor may "Exolab" appear in their names without prior written
 *    permission of Exoffice Technologies. Exolab is a registered
 *    trademark of Exoffice Technologies.
 *
 * 5. Due credit should be given to the Exolab Project
 *    (http://www.exolab.org/).
 *
 * THIS SOFTWARE IS PROVIDED BY EXOFFICE TECHNOLOGIES AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * EXOFFICE TECHNOLOGIES OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2000-2003 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: TemporaryTopic.java,v 1.19 2003/06/15 22:53:09 tanderson Exp $
 *
 * Date         Author  Changes
 * 04/25/2000   jima    Created
 */
package openjms.examples.client.console;

import java.io.PrintStream;
import java.util.Date;
import java.util.Random;

import javax.naming.Context;

import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicRequestor;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

import org.exolab.jms.util.CommandLine;


/**
 * TemporaryTopic are tested through the TopicRequestor class. A publisher 
 * and subscriber set up for the specified topic name and then temporary
 * topic is used for the publisher to receive a reply from the publisher
 */
public class TemporaryTopic {

    public static void main(String[] args) {
        CommandLine cmdline = new CommandLine(args);
        if (cmdline.exists("help") || !cmdline.exists("topic")) {
            usage();
        } else {
            Thread serverThread = null;

            boolean summary = cmdline.exists("summary");

            try {
                // connect to the JNDI server and get a reference to 
                // root context
                Context context = ContextHelper.getContext(cmdline);
            
                // lookup the connection factory from the context
                String factoryName = cmdline.value(
                    "factory", "JmsTopicConnectionFactory");            
                TopicConnectionFactory factory = (TopicConnectionFactory)
                    context.lookup(factoryName);

                TopicConnection connection = factory.createTopicConnection();
                connection.start();

                TopicSession session = connection.createTopicSession(
                    false, Session.AUTO_ACKNOWLEDGE);

                // this is not the preferred way to get a topic
                String topicName = cmdline.value("topic");
                Topic topic = session.createTopic(topicName);

                // create a receiver of the request in a separate thread and
                // start it running
                if (cmdline.exists("standalone")) {
                    if (cmdline.value("standalone").equals("A")) {
                        RequestReply server = new RequestReply(
                            connection, topic);
                        serverThread = new Thread(server);
                        serverThread.start();
                    }
                } else {
                    RequestReply server = new RequestReply(connection, topic);
                    serverThread = new Thread(server);
                    serverThread.start();

                    // wait some time while it gets ready
                    synchronized (server) {
                        while (true) {
                            try {
                                server.wait();
                                break;
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                }
                                        
                // set up a TopicRequestor and send a request
                TopicRequestor request = new TopicRequestor(session, topic);

                for (int index = 0; index < 10; index++) {
                    Message message = session.createTextMessage(
                        "Message to send " + index);
                    message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    Message received_msg = request.request(message);
                }
                System.err.println(
                    "Processed 10 messages through the temporary topic");

                serverThread.join();
                connection.close();
            } catch (Exception exception) {
                System.err.println("Fatal error: " + exception + 
                                   "\nExiting.....");
                exception.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * Print out information on running this sevice
     */
    protected static void usage() {
        PrintStream out = System.out;

        out.println("\n\n");
        out.println("====================================================================");
        out.println("Usage information for openjms.examples.client.console.TemporaryTopic");
        out.println("====================================================================");
        out.println("\nopenjms.examples.console.client.TemporaryTopic");
        out.println("    [-help | -topic <topic name> ]\n");
        out.println("\t-help   displays this screen\n");
        out.println("\t-topic   topic name to subscriber under.\n");
    }
}

/**
 * This class sets up a subscriber to the specified topic. It basically 
 * creates a session using the specified connection and subscribes to 
 * the registered topic. When it receives a message it simply creates
 * a publishers and replies to the reply-to destination in the message.
 */
class RequestReply implements Runnable {

    private Topic _topic;
    private TopicConnection _connection;

    public RequestReply(TopicConnection connection, Topic topic) {
        _connection = connection;
        _topic = topic;
    }

    public void run() {
        TopicPublisher publisher = null;
        TopicSession session = null;
        TopicSubscriber subscriber = null;
        try {
            session = _connection.createTopicSession(
                false, Session.AUTO_ACKNOWLEDGE);

            // create a subscriber for the specified session and then
            // wait for a message to arrive.
            subscriber = session.createSubscriber(_topic);
            
            // we are ready to notify the waiting thread to
            // continue
            synchronized (this) {
                notifyAll();
            }

            publisher = session.createPublisher(_topic);
            for (int index = 0; index < 10; ++index) {
                Message message = subscriber.receive();
                // publish the message back to the sender
                publisher.publish((Topic)message.getJMSReplyTo(), message);
            }            
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }
}
