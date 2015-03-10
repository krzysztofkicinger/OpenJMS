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
 * $Id: TemporaryQueue.java,v 1.20 2003/06/15 22:53:09 tanderson Exp $
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
import javax.jms.QueueRequestor;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.exolab.jms.util.CommandLine;


/**
 * TemporaryQueue are tested through the QueueRequestor class. A sender 
 * and receiver set up for the specified queue name and then temporary
 * queue is used for the publisher to receive a reply from the publisher
 */
public class TemporaryQueue {

    static public void main(String[] args) {
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

                String queueName = cmdline.value("queue");
                Queue queue = session.createQueue(queueName);

                // create a receiver of the request in a separate thread and
                // start it running
                QueueRequestReply server = new QueueRequestReply(
                    connection, queue);
                Thread serverThread = new Thread(server);
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

                // set up a QueueRequestor and send a request
                QueueRequestor request = new QueueRequestor(session, queue);

                for (int index = 0; index < 10; index++) {
                    Message message = session.createTextMessage(
                        "Message to send " + index);
                    message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
                    Message received_msg = request.request(message);
                }

                System.err.println(
                    "Processed 10 messages through the temporary queue");

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
    static protected void usage() {
        PrintStream out = System.out;

        out.println("\n\n");
        out.println("====================================================================");
        out.println("Usage information for openjms.examples.client.console.TemporaryQueue");
        out.println("====================================================================");
        out.println("\nopenjms.examples.console.client.TemporaryQueue");
        out.println("    [-help | -queue <queue name> ]\n");
        out.println("\t-help   displays this screen\n");
        out.println("\t-queue   queue name to subscriber under.\n");
    }
}



/**
 * This class setsup a subscriber to the specified queue. It basically 
 * creates a session using the specified connection and subscribes to 
 * the registered queue. When it receives a message it simply creates
 * a publishers and replies to the reply-to destination in the message.
 */
class QueueRequestReply implements Runnable {

    private QueueConnection _connection;
    private Queue _queue;

    public QueueRequestReply(QueueConnection connection, Queue queue) {
        _connection = connection;
        _queue = queue;
    }

    public void run() {
        QueueSession session = null;
        try {
            session = _connection.createQueueSession(
                false, Session.AUTO_ACKNOWLEDGE);
            QueueReceiver receiver = session.createReceiver(_queue);
            QueueSender sender = session.createSender(null);

            // we are ready to notify the waiting thread to
            // continue
            synchronized (this) {
                notifyAll();
            }

            for (int index = 0; index < 10; index++) {
                Message message = receiver.receive();
                // send the message back
                sender.send((Queue)message.getJMSReplyTo(), message);
            }
        } catch (JMSException exception) {
            exception.printStackTrace();
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

}           
