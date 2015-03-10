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
 * Copyright 1999 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: SimpleQueueListener.java,v 1.5 2003/06/15 22:53:09 tanderson Exp $
 *
 * Date         Author  Changes
 * 04/25/2000   jima    Created
 */
package openjms.examples.client.console;

import java.io.PrintStream;

import javax.naming.Context;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.exolab.jms.util.CommandLine;


/**
 * SimpleQueueListener listens for messages on a queue.
 */
public class SimpleQueueListener 
    implements MessageListener, TimerListener, ExceptionListener {

    private QueueReceiver _receiver;
    private QueueConnection _connection;
    private int _ackMode;
    private Timer _timer;
    private int _timeout;
    private boolean _counting;
    private int _count;
    private int _received;
    private boolean _summary;

    public SimpleQueueListener(QueueConnection connection, 
                          QueueReceiver receiver, int ackMode, 
                          int count, int timeout, boolean summary) {
        _connection = connection;
        _receiver = receiver;
        _ackMode = ackMode;
        _counting = (count > -1);
        _count = count;
        _timeout = timeout;
        _received = 0;
        _summary = summary;

        if (_timeout > 0) {
            _timer = new Timer(this, _timeout * 1000);
            _timer.start();
        } 
    }

    /**
     * All messages that for this listener are received by this method
     *
     * @param message the message
     */
    public void onMessage(Message message) {
        String mode = "unknown";
        String id = "unset";
        int priority = -1;

        if (_timer != null) {
            _timer.reset();
        }

        _received++;
        if (!_summary) {
            if (_counting) {
                System.err.print("[" + _received + " of " + _count + "] ");
            }

            try {
                if (message.getJMSDeliveryMode() == DeliveryMode.PERSISTENT) {
                    mode = "PERSISTENT";
                } else if (message.getJMSDeliveryMode() == 
                           DeliveryMode.NON_PERSISTENT) {
                    mode = "NON_PERSISTENT";
                }
                id = message.getJMSMessageID();
                priority = message.getJMSPriority();
            } catch (JMSException ignore) {
            }
            System.err.println("JMSDeliveryMode=" + mode + ", Priority=" + 
                               priority + ", JMSMessageID=" + id);
        }

        try {
            if (_ackMode == Session.CLIENT_ACKNOWLEDGE) {
                message.acknowledge();
            }
        } catch (JMSException exception) {
            System.err.println("Failed to acknowledge message: " + exception);
        }

        if (_counting && _received >= _count) {
            // can't do the close in the same thread as the callback,
            // so reset the listener, and close from a separate thread
            try {
                _receiver.setMessageListener(null);
            } catch (JMSException exception) {
                System.err.println("Could not unset the listener: " + 
                                   exception);
            }

            String summary = "The listener received " + _received + 
                " messages";
            try {
                summary += " from queue " + _receiver.getQueue();
            } catch (JMSException exception) {
                exception.printStackTrace();
            }
            
            final String msg = summary;
            Thread shutdown = new Thread() {
                public void run() {
                    SimpleQueueListener.this.exit(msg);
                }
            };
            shutdown.start();
        }
    }

    public void onTimeout() {
        exit("No message received in the last " + _timeout + 
             " seconds, exiting");
    }

    // implementation of ExceptionListener.onException
    public void onException(JMSException exception) {
        exit("Received onException notification");
    }
    
    private void exit(String message) {
        System.err.println(message);

        try {
            _connection.close();
            _timer.stop();
        } catch (Exception error) {
            error.printStackTrace();
        }
        System.exit(0);
    }

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

                String selector = cmdline.value("selector");

                QueueReceiver receiver = session.createReceiver(
                    queue, selector);

                int count = Integer.parseInt(cmdline.value("count", "-1"));

                // the timeout is used to terminate the listener if no
                // messages are received within the specified timeframe.
                // The default value is 5 minutes.
                int timeout = Integer.parseInt(
                    cmdline.value("timeout", "300"));

                SimpleQueueListener listener = new SimpleQueueListener(
                    connection, receiver, ackMode, count, timeout, summary);
                receiver.setMessageListener(listener);
                connection.setExceptionListener(listener);
            } catch (Exception exception) {
                exception.printStackTrace();
                System.err.println("Fatal error: " + exception + 
                                   "\nExiting.....");
                System.exit(-1);
            }
        }
    }
    
    /**
     * Print out information on running this sevice
     */
    protected static void usage() {
        PrintStream out = System.out;

        out.println("usage: java " + SimpleQueueListener.class.getName() +
            " [options]\n");
        out.println("options:");
        out.println("  -queue <name>      queue to subscribe to.\n");
        out.println("  -mode <tcp | tcps | rmi | http | https>");
        out.println("                     specifies the connection protocol." +
                    " Defaults to 'rmi'.\n");
        out.println("  -url <url> only used for http mode, the client passes\n"
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
        out.println("  -jndiname <name>   name of the jndi server\n");
        out.println("  -count <num>       number of messages to wait for.\n");
        out.println("  -timeout <seconds> seconds to wait before exiting.");
        out.println("                     Default is wait forever.\n");
        out.println("  -help              displays this screen.\n");
    }   

}
