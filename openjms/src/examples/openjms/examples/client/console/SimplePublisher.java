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
 * $Id: SimplePublisher.java,v 1.46 2003/06/15 22:53:09 tanderson Exp $
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
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.TopicConnectionFactory;

import org.exolab.jms.util.CommandLine;


/**
 * The simple publisher application simply grabs a connection and a session.
 * It then creates a topic and begins to publish messages. The application
 * expects a topic name to be specified on the command line. If one is not
 * specified then an exception is raised.
 */
public class SimplePublisher {

    public static void main(String[] args) {
        CommandLine cmdline = new CommandLine(args);
        if (cmdline.exists("help") || !cmdline.exists("topic")) {
            usage();
        } else {
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
                    false, Session.CLIENT_ACKNOWLEDGE);

                String topicName = cmdline.value("topic");
                Topic topic = session.createTopic(topicName);

                TopicPublisher publisher = session.createPublisher(topic);

                // check the number of iterations that have been specified
                // on the command line. If it has not yet been specified 
                // then default to 10.
                int count = Integer.parseInt(cmdline.value("count", "10"));

                // check if a sleep has been specified
                int sleep = Integer.parseInt(cmdline.value("sleep", "0"));

                // determine if the delivery mode should be persistent or 
                // non persistent
                int deliveryMode = DeliveryMode.NON_PERSISTENT;
                if (cmdline.exists("persistent")) {
                    deliveryMode = DeliveryMode.PERSISTENT;
                }

                // create a text message
                Random rand = new Random((new Date()).getTime());
                for (int index = 0; index < count; index++) {
                    TextMessage message = session.createTextMessage(
                        "message " + index);
                    int priority = Math.abs(rand.nextInt() % 10);
                    publisher.publish(topic, message, deliveryMode, 
                        priority, 0);

                    if (!summary) {
                        System.err.println(
                            "[" + index + " of " + count + 
                            "] JMSDeliveryMode=" + 
                            message.getJMSDeliveryMode() +  
                            ", Priority=" + message.getJMSPriority()  + 
                            ", JMSMessageID=" + message.getJMSMessageID());
                    }
                    
                    if (sleep > 0) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception ignore) {
                        }
                    }
                }

                System.out.println("The publisher published " + count + 
                                   " messages on topic " + topicName);
                
                connection.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        System.exit(0);
    }

    /**
     * Print out information on running this sevice
     */
    private static void usage() {
        PrintStream out = System.out;

        out.println("usage: java " + SimplePublisher.class.getName() +
            " [options]\n");
        out.println("options:");
        out.println("  -topic <topic>     topic to publisher under.\n");
        out.println("  -persistent        " + 
                    "specifies persistent delivery mode.\n");
        out.println("  -mode <tcp | tcps | rmi | http | https>");
        out.println("                     specifies the connection protocol." +
                    " Defaults to 'rmi'.\n");
        out.println("  -jndihost <host>   host where JNDI server runs.\n");
        out.println("  -jndiport <port>   port where the JNDI server runs.\n");
        out.println("  -jndiname <name>   name of the JNDI server\n");
        out.println("  -count <num>       number of messages to publish.\n");
        out.println("  -sleep <num>       time to sleep b/w each publish (ms).\n");
        out.println("  -summary           only print a summary\n");
        out.println("  -help              displays this screen.\n");
    }
}
