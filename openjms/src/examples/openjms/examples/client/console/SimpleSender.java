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
 * $Id: SimpleSender.java,v 1.28 2003/03/24 13:57:20 tanderson Exp $
 *
 * Date         Author  Changes
 * 04/25/2000   jima    Created
 */
package openjms.examples.client.console;

import java.io.PrintStream;
import java.util.Date;
import java.util.Random;

import javax.naming.Context;

import javax.jms.BytesMessage;
import javax.jms.DeliveryMode;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.QueueConnectionFactory;

import org.exolab.jms.util.CommandLine;


/**
 * The simple publisher application simply grabs a connection and a session.
 * It then creates a queue and begins to sends messages. The application
 * expects a queue name to be specified on the command line. 
 */
public class SimpleSender {

    public static void main(String[] args) {
        CommandLine cmdline = new CommandLine(args);
        if (cmdline.exists("help") || !cmdline.exists("queue")) {
            usage();
        } else {

            boolean summary = cmdline.exists("summary");

            try {
                // connect to the JNDI server and get a reference to the
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
                    false, Session.CLIENT_ACKNOWLEDGE);

                // if the persistent flag is not specified then create the
                // queue object otherwise look it up
                String queueName = cmdline.value("queue");
                Queue queue = null;
                if (cmdline.exists("persistent")) {
                    queue = (Queue) context.lookup(queueName);
                } else {
                    queue = (Queue) session.createQueue(queueName);
                }

                QueueSender sender = session.createSender(queue);

                // check the number of iterations that have been specified
                // on the command line. If it has not yet been specified 
                // then default to 10.
                int count = Integer.parseInt(cmdline.value("count", "10"));

                // check if a sleep has been specified
                int sleep = Integer.parseInt(cmdline.value("sleep", "0"));

                // check if message time-to-live has been specified
                long timeToLive = Integer.parseInt(cmdline.value("ttl", "0"));

                // determine if the delivery mode should be persistent or 
                // non persistent
                int deliveryMode = DeliveryMode.NON_PERSISTENT;
                if (cmdline.exists("persistent")) {
                    deliveryMode = DeliveryMode.PERSISTENT;
                }

                // retrieve the message size
                int size = Integer.parseInt(cmdline.value("size", "10000"));

                // create a message
                Random rand = new Random((new Date()).getTime());
                for (int index = 0; index < count; index++) {
                    byte[] buf = new byte[size];
                    BytesMessage message = session.createBytesMessage();
                    message.writeBytes(buf, 0, buf.length);
                    int priority = Math.abs(rand.nextInt() % 10);
                    sender.send(message, deliveryMode, priority, timeToLive);

                    if (!summary) {
                        System.out.println("Publishing " + message);
                    }
                    
                    if (sleep > 0) {
                        try {
                            Thread.sleep(sleep);
                        } catch (Exception ignore) {
                        }
                    }
                }

                // close the connection
                connection.close();

                if (summary) {
                    System.err.println("The sender has sent " + count + 
                                       " messages to queue " + queueName);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Print out information on running this sevice
     */
    static protected void usage() {
        PrintStream out = System.out;

        out.println("usage: java " + SimpleSender.class.getName() +
            " [options]\n");
        out.println("options:");
        out.println("  -queue <name>      queue to send messages to.\n");
        out.println("  -persistent        " + 
            "specifies persistent delivery mode.\n");
        out.println("  -mode <tcp | tcps | rmi | http | https>");
        out.println("                     specifies the connection protocol." +
                    " Defaults to 'rmi'.\n");
        out.println("  -size <num>        size of messages (defaults to 10K).\n");
        out.println("  -jndiport <num>    port where the jndi server runs.\n");
        out.println("  -jndihost <host>   host where jndi server runs.\n");
        out.println("  -count <num>       number of messages to publish.\n");
        out.println("  -sleep <num>       time to sleep b/w each send (ms).\n");
        out.println("  -help              displays this screen.\n");
    }
}

