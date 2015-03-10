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
 * Copyright 2000 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: SimpleConsumer.java,v 1.4 2001/12/15 11:56:27 mourikis Exp $
 *
 * Date         Author  Changes
 * 01/01/2001   fabien  Created
 */
package openjms.examples.profiling;

import java.io.PrintStream;
import java.util.Hashtable;

// JNDI
import javax.naming.InitialContext;
import javax.naming.Context;

// JMS
import javax.jms.Session;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TextMessage;
import javax.jms.ObjectMessage;
import javax.jms.TopicConnectionFactory;
import javax.jms.JMSException;

// exolab
import org.exolab.jms.util.CommandLine;
import org.exolab.jms.client.JmsTopicConnectionFactory;
import org.exolab.jms.jndi.JndiConstants;

// Fabien
import java.util.Date;
import java.lang.String;
import java.util.ArrayList;



/**
 * The simple consumer is used to subscribe to a particular topic and receive
 * events. It will set a message listener so that events can be pushed down
 * to it when they become available.
 */
public class SimpleConsumer
{
    static public void main(String[] args)
    {
        try
        {
            // Variable set
                // Administered object accessing
                int jndiPort = 1099;
                String jndiHost = "localhost";
                // Messaging
                int ackType = Session.AUTO_ACKNOWLEDGE;
                int clientAckNb = 100;
                String topicName = "topic";
                boolean transacted = false;
                // Listener Parametring
                boolean readMsg = false;
                int waitTime = 5000;
                int displayCount = 500;


            // Retreiving the command line arguments with a OpenJMS utility
            CommandLine cmdline = new CommandLine(args);

                // Administered objects accessing
                if (cmdline.exists("jndiport"))
                {
                    jndiPort = Integer.parseInt(cmdline.value("jndiport"));
                    System.out.println("JNDI Port parametered");
                }
                if (cmdline.exists("jndihost"))
                {
                    jndiHost = cmdline.value("jndihost");
                    System.out.println("JNDI Host parametered");
                }

                // Messaging
                if (cmdline.exists("ack"))
                {
                    try
                    {
                        String mode = cmdline.value("ack");
                        if (mode.equals("auto"))
                        {
                            ackType = Session.AUTO_ACKNOWLEDGE;
                            System.out.println("Ack Type parametered");
                        }
                        else if (mode.equals("client"))
                        {
                            ackType = Session.CLIENT_ACKNOWLEDGE;
                            System.out.println("Ack Type parametered");
                        }
                        else if (mode.equals("dups"))
                        {
                            ackType = Session.DUPS_OK_ACKNOWLEDGE;
                            System.out.println("Ack Type parametered");
                        }
                        else
                        {
                            System.err.println("Invalid ackmode specified");
                            System.exit(1);
                        }
                     }
                     catch (Exception exception)
                     {
                        System.err.println("Invalid ackmode specified");
                        System.exit(0);
                     }
                }
                if (cmdline.exists("acknb"))
                {
                    clientAckNb = Integer.parseInt(cmdline.value("acknb"));
                    System.out.println("Client ack size parametered");
                }
                if (cmdline.exists("topic"))
                {
                    topicName = cmdline.value("topic");
                    System.out.println("Topic parametered");
                }
                if (cmdline.exists("transacted"))
                {
                    transacted = true;
                    System.out.println("Transacted mode parametered");
                }

                // Listener parametering
                if (cmdline.exists("count"))
                {
                    // The message listener should show mesurements every <count> messages
                    displayCount = Integer.parseInt(cmdline.value("count"));
                    System.out.println("Message read mode parametered");
                }
                if (cmdline.exists("readmsg"))
                {
                    // The message listener should read the received messages instead of only counting it
                    readMsg = true;
                    System.out.println("Message read mode parametered");
                }
                if (cmdline.exists("wait"))
                {
                    // At the end of a message receipt, wait <time> ms for new messages before terminating
                    waitTime = Integer.parseInt(cmdline.value("wait"));
                    System.out.println("Waiting time parametered");
                }


                // Misc
                if (cmdline.exists("help"))
                {
                    usage();
                }
                if (cmdline.exists("test"))
                {
                    System.out.println("jndiport : " + jndiPort);
                    System.out.println("jndihost : " + jndiHost);
                    System.out.println("ackType : " + ackType);
                    System.out.println("topicName: " + topicName);
                    System.out.println("transacted : " + transacted);
                    System.out.println("readMsg : " + readMsg);
                    System.out.println("waitTime : " + waitTime);
                    System.exit(0);
                }

            // Connect to the JNDI Server to get a reference to the administred objects
            Hashtable props = new Hashtable();
            props.put(JndiConstants.PORT_NUMBER_PROPERTY, new Integer(jndiPort));
            props.put(JndiConstants.HOST_PROPERTY, jndiHost);
            String modeType = "org.exolab.jms.jndi.rmi.RmiJndiInitialContextFactory";
            props.put(Context.INITIAL_CONTEXT_FACTORY, modeType);
            Context context = new InitialContext(props);
            // if we can't get the root context then exit with an exception
            if (context == null)
            {
                throw new RuntimeException("Failed to get the root context");
            }

            // lookup the connection factory from the context
            TopicConnectionFactory factory = (TopicConnectionFactory)context.lookup("JmsTopicConnectionFactory");
            // if we can't find the factory then throw an exception
            if (factory == null)
            {
                throw new RuntimeException("Failed to locate connection factory");
            }
            System.out.println("Connection Factory ready");

            // Openning a connection with the connection Factory
            TopicConnection connection = factory.createTopicConnection();
            connection.start();
            System.out.println("Connection ready");

            // Openning a session within the connection
            TopicSession session = connection.createTopicSession(transacted, ackType);
            System.out.println("Session ready");

            // Looking for / Creating a topic
            Topic topic = null;
            try
            {
                topic = (Topic)context.lookup(topicName);
            }
            catch (Exception e)
            {
                topic = session.createTopic(topicName);
            }
            if (topic == null)
            {
                topic = session.createTopic(topicName);
            }
            System.out.println("Topic ready");

            // Registering as a subscriber under the topic
            TopicSubscriber subscriber = session.createDurableSubscriber(topic,"c1");
            System.out.println("Subscriber ready");

            // MyListener listener = new MyListener(readMsg, Integer.MAX_VALUE, waitTime);
            MyListener listener = new MyListener(readMsg, ackType, waitTime, clientAckNb, displayCount);
            subscriber.setMessageListener(listener);
            System.err.println("Message Listener ready");
            // onMessage Monitor thread start
            Thread myMonitor = new Thread(listener);
            myMonitor.start();
            System.err.println("Waiting for messages...");

            // close the session and connection
            // Commented becase of a bug ?
            // session.close();
            // connection.close();
        }
        catch (Exception exception)
        {
            System.err.println("Fatal error: " + exception + "\nExiting.....");
            exception.printStackTrace();
            System.exit(-1);
        }

   }

   /**1
     * Print out information on running this sevice
     */
    static protected void usage()
    {
		PrintStream out = System.out;

		out.println("\n\n");
		out.println("==================================================================================");
		out.println("        Usage information for openjms.examples.profiling.SimpleConsumer");
		out.println("==================================================================================");
		out.println("\nopenjms.examples.profiling.SimpleConsumer");
		out.println();
		out.println("    [-help | -test | -jndiport <port number> | -jndihost <host address>");
                out.println("     -ack <auto/client/dups> | -topic <topic name> | -transacted");
		out.println("     -count <nb_msg> | -readmsg | -wait <number of ms>]");
		out.println();
		out.println("Misc. options :");
		out.println("\t-help         displays this screen");
		out.println("\t-test         displays command line argument values");
		out.println();
		out.println("Administered objects options :");
		out.println("\t-jndiport     port where the jndi server runs         - Default = 1099");
		out.println("\t-jndihost     host where jndi server runs             - Default = localhost");
		out.println();
		out.println("JMS Messaging options :");
		out.println("\t-ack          \"auto\", \"client\" or \"dups\"              - Default = auto");
		out.println("\t-acknb        Nb of messages to acknoledge at a time  - Default = 100");
		out.println("\t              Only for \"client\" ack mode");
		out.println("\t-topic        topic name to subscriber under.         - Default = topic");
		out.println("\t-transacted   use transacted session with the Topic.  - Default = false");
		out.println();
		out.println("Message Listener options :");
		out.println("\t-count        show mesurements every <count> messages                 - Default = false");
		out.println("\t-readmsg      read the message instead of only counting it            - Default = false");
		out.println("\t-wait         number of ms to wait for new messages when recieved one - Default = 5000");
                out.println("\n\n");
                System.exit(0);
    }

}




