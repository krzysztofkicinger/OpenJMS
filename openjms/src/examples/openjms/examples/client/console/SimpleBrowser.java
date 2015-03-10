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
 * Copyright 2001-2003 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: SimpleBrowser.java,v 1.14 2003/06/15 22:53:09 tanderson Exp $
 *
 * Date         Author  Changes
 * 22/02/2001   tima    Created
 * 23/01/2002   jima    Modifed to run with soak test
 */


package openjms.examples.client.console;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.naming.Context;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.exolab.jms.util.CommandLine;


/**
 * The simple browser will browse messages on a queue
 */
public class SimpleBrowser {

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
                    false, Session.AUTO_ACKNOWLEDGE);

                // if the persistent flag is not specified then create the
                // queue object otherwise look it up
                String queueName = cmdline.value("queue");
                Queue queue = null;
                if (cmdline.exists("persistent")) {
                    queue = (Queue) context.lookup(queueName);
                } else {
                    queue = (Queue) session.createQueue(queueName);
                }

                QueueBrowser browser = session.createBrowser(queue);

                // return the number of messages to browse
                int count = Integer.parseInt(cmdline.value("count", "-1"));

                Enumeration iter = browser.getEnumeration();
                int received = 0;
                while (iter.hasMoreElements()) {
                    Message message = (Message) iter.nextElement();
                    
                    if (!summary) {
                        System.out.print(message);
                    }

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
                    } catch (JMSException ignore) {
                    }

                    try {
                        priority = message.getJMSPriority();
                        id = message.getJMSMessageID();
                    } catch (JMSException ignore) {
                    }

                    if (!summary) {
                        System.err.println(" JMSDeliveryMode=" + type + 
                                           ", Priority=" + 
                                           priority + ", JMSMessageID=" + id);
                    }

                    if (count > -1 && ++received >= count) {
                        break;
                    }
                }
                // close the session and connection
                browser.close();
                session.close();
                connection.close();

                if (summary) {
                    System.err.println("SimpleBrowser browsed " + received + 
                                       " msgs on queue " + queueName);
                }
            } catch (Exception exception) {
                System.err.println("Fatal error: " + exception + 
                                   "\nExiting.....");
                System.exit(-1);
            }
        }
    }

    /**
     * Print out information on running this sevice
     */
    static protected void usage() {
        PrintStream out = System.out;

        out.println("usage: java " + SimpleBrowser.class.getName() +
            " [options]\n");
        out.println("options:");
        out.println("  -queue <name>      queue to browse.\n");
        out.println("  -mode <tcp | tcps | rmi | http | https>");
        out.println("                     specifies the connection protocol." +
                    " Defaults to 'rmi'.\n");
        out.println("  -jndiport <num>    port where the jndi server runs.\n");
        out.println("  -jndihost <host>   host where jndi server runs.\n");
        out.println("  -persistent        if browsing an administered queue.\n");
        out.println("  -count <num>       number of messages to browse.\n");
        out.println("  -summary           displays the number of messages browsed.\n");
        out.println("  -help              displays this screen.\n");
    }
}

