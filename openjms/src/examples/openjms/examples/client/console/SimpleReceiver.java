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
 * $Id: SimpleReceiver.java,v 1.29 2003/06/15 22:53:09 tanderson Exp $
 *
 * Date         Author  Changes
 * 04/25/2000   jima    Created
 */
package openjms.examples.client.console;

import java.io.PrintStream;

import javax.naming.Context;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.exolab.jms.util.CommandLine;


/**
 * The simple receiver will sit there waiting for a specified number of 
 * message to arrive down a specific queue and then exits
 */
public class SimpleReceiver {

    public static void main(String[] args) {
        CommandLine cmdline = new CommandLine(args);

        if (cmdline.exists("help") || !cmdline.exists("queue")) {
            usage();
        } else {
            // see if an ack mode has been specified. If it hasn't
            // then assume CLIENT_ACKNOWLEDGE mode.
            int ackMode = Session.CLIENT_ACKNOWLEDGE;
            String amode = cmdline.value("ackmode", "client");
            if (amode.equals("auto")) {
                ackMode = Session.AUTO_ACKNOWLEDGE;
            } else if (amode.equals("dups")) {
                ackMode = Session.DUPS_OK_ACKNOWLEDGE;
            } else if (!amode.equals("client")) {
                // ignore all ack modes, to test no acking
                ackMode = -1;
            }

            boolean summary = cmdline.exists("summary");

            try {
                // connect to the JNDI server and get a reference to 
                // root context
                Context context = ContextHelper.getContext(cmdline);
            
                // lookup the connection factory from the context
                String factoryName = cmdline.value(
                    "factory", "JmsQueueConnectionFactory");            
                QueueConnectionFactory factory = (QueueConnectionFactory)
                    context.lookup(factoryName);

                QueueConnection connection = factory.createQueueConnection();
                connection.start();

                QueueSession session = connection.createQueueSession(
                    false, ackMode);

                Queue queue = null;
                String queueName = cmdline.value("queue");
                if (cmdline.exists("persistent")) {
                    queue = (Queue) context.lookup(queueName);
                } else {
                    queue = session.createQueue(queueName);
                }

                QueueReceiver receiver = session.createReceiver(queue);

                // if we are republishing the message then also create
                // a sender
                QueueSender sender = null;
                if (cmdline.exists("republish")) {
                    sender = session.createSender(queue);
                }

                // return the number of messages to receive
                int count = Integer.parseInt(cmdline.value("count", "1"));

                int index = 0;
                int sentCount = 0;
                for (; index < count; index++) {
                    Message message = receiver.receive();
                    
                    if (message == null) {
                        System.err.println("No message received");
                        continue;
                    }
                    // only display the message details if the summary flag
                    // is not set.
                    if (!summary) {
                        System.out.print("[" + (index+1) + " of " + count + 
                                         "] " + message.getJMSType());

                        String type = "unknown";
                        String id = "unset";
                        int priority = -1;
                    
                        try {
                            if (message.getJMSDeliveryMode() == 
                                DeliveryMode.NON_PERSISTENT) {
                                type = "NON_PERSISTENT";
                            } else if (message.getJMSDeliveryMode() == 
                                       DeliveryMode.PERSISTENT) {
                                type = "PERSISTENT";
                            }
                            priority = message.getJMSPriority();
                            id = message.getJMSMessageID();
                        } catch (JMSException ignore) {
                        }
                
                        System.err.println(" JMSDeliveryMode=" + type + 
                                           ", Priority=" + priority + 
                                           ", JMSMessageID=" + id);
                    }

                    // if client acknowledgement, then ack it
                    if (ackMode == Session.CLIENT_ACKNOWLEDGE) {
                        message.acknowledge();
                    }
                
                    // if the publish flag has been set then publish the 
                    // same message to the same queue
                
                    if (cmdline.exists("republish")) {
                        sender.send(
                            session.createQueue(cmdline.value("republish")),
                            message, message.getJMSDeliveryMode(), 
                            message.getJMSPriority(), 0);

                        if (!summary) {
                            System.err.println(
                                "[" + ++sentCount + "] Sent " + 
                                message.getJMSType() + 
                                " JMSDeliveryMode=" + 
                                message.getJMSDeliveryMode() + 
                                " ID=" + message.getJMSMessageID());
                        }
                    }

                    if (cmdline.exists("sleep")) {
                        Thread.sleep(Long.parseLong(cmdline.value("sleep")));
                    }
                }

                // close the session and connection
                receiver.close();
                session.close();
                connection.close();

                // if the summary flag is specified then print out
                // the count.
                if (summary) {
                    System.err.println("The receiver has received " + index + 
                                       " messages from queue " + queueName);
                }
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
    static protected void usage() {
        PrintStream out = System.out;

        out.println("usage: java " + SimpleReceiver.class.getName() +
            " [options]\n");
        out.println("options:");
        out.println("  -queue <queue>     queue to receive messages from.\n");
        out.println("  -mode <tcp | tcps | rmi | http | https>");
        out.println("                     specifies the connection protocol." +
                    " Defaults to 'rmi'.\n");
        out.println("  -url <url>  only used for http mode, the client passes\n"
                 +  "              this url to the server, to allow the server\n"
                 +  "              to invoke the clients servlet at the\n"
                 +  "              specfified url. Defaults to "
                 +  "'http://localhost:8080'.\n");
        out.println("  -ackmode <auto | client | dups>\n" + 
            "                     message acknowledgement mode. " +
            "Defaults to 'client'.\n");
        out.println("  -persistent        " + 
            "specifies persistent delivery mode.\n");
        out.println("  -jndiport <num>    port where the jndi server runs.\n");
        out.println("  -jndihost <host>   host where jndi server runs.\n");
        out.println("  -count <num>       number of messages to wait for.\n");
        out.println("  -help              displays this screen.\n");
    }
}

