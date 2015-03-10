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
 * $Id: Admin.java,v 1.5 2003/03/23 06:11:16 tanderson Exp $
 */
package openjms.examples.client.console;

// java utility
import java.util.Iterator;
import java.util.Vector;

// jms
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;

// openjms
import org.exolab.jms.administration.AdminConnectionFactory;
import org.exolab.jms.administration.JmsAdminServerIfc;
import org.exolab.jms.util.CommandLine;


/**
 * Example demonstrating the administration interface
 */
public class Admin {

    /**
     * List all destinations
     */
    public static void list(String url) throws Exception {
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        Vector destinations = admin.getAllDestinations();
        Iterator iter = destinations.iterator();
        while (iter.hasNext()) {
            Destination destination = (Destination) iter.next();
            if (destination instanceof Queue) {
                Queue queue = (Queue) destination;
                System.out.println("queue://" + queue.getQueueName() + " [" + 
                                   admin.getQueueMessageCount(queue.getQueueName()) + "]");
            } else {
                Topic topic = (Topic) destination;
                System.out.println("topic://" + topic.getTopicName());
            }
        }
        admin.close();
    }

    /**
     * Add an administered destination
     */
    public static void add(String url, String type, String name) 
        throws Exception {
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        Boolean queue = (type.equals("queue")) ? Boolean.TRUE : Boolean.FALSE;
        if (!admin.addDestination(name, queue)) {
            System.err.println("Failed to add destination=" + name);
        }
        admin.close();
    }

    /**
     * Return the number of messages on the specified queue
     *
     * @param queue - the name of the queue
     */
    public static void queueCount(String url, String queue)
    throws Exception {
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        System.err.println("[" + queue + "] = " + 
                           admin.getQueueMessageCount(queue));
        admin.close();
    }

    /**
     * Return the number of messages on the specified topic for the
     * nominated consumer
     *
     * @param topic - the name of the topic
     * @param name - the consumer name
     */
    public static void topicCount(String url, String topic, String name)
    throws Exception {                                                
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        System.err.println("[" + topic + ":" + name + "] = " +
                           admin.getDurableConsumerMessageCount(topic, name));
        admin.close();
    }

    /**
     * Remove an administered destination
     */
    public static void remove(String url, String name) 
        throws Exception {
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        if (!admin.removeDestination(name)) {
            System.err.println("Failed to remove destination=" + name);
        }
        admin.close();
    }

    /**
     * Determine if the specified destination exists
     */
    public static void destinationExists(String url, String name) 
        throws Exception {
        JmsAdminServerIfc admin = AdminConnectionFactory.create(url);
        if (admin.destinationExists(name)) {
            System.out.println("Destination " + name + " exists.");
        } else {
            System.out.println("Destination " + name + " does not exist.");
        }

        admin.close();
    }

    public static void main(String[] args) throws Exception {
        CommandLine cmdline = new CommandLine(args);
        String url = cmdline.value("url");

        if (url == null || cmdline.exists("help")) {
            usage();
        } else if (cmdline.exists("list")) {
            list(url);
        } else if (cmdline.exists("add")) {
            add(url, cmdline.value("add"), cmdline.value("name"));
        } else if (cmdline.exists("queue")) {
            queueCount(url, cmdline.value("queue"));
        } else if (cmdline.exists("topic") && (cmdline.exists("name"))) {
            topicCount(url, cmdline.value("topic"), cmdline.value("name"));
        } else if (cmdline.exists("remove")) {
            remove(url, cmdline.value("name"));
        } else if (cmdline.exists("destExists")) {
            destinationExists(url, cmdline.value("destExists"));
        } else {
            usage();
        }
    }

    /**
     * Print out information on running this sevice
     */
    private static void usage() {
        System.out.println(
            "usage: java " + Admin.class.getName() +
            " <arguments> [options]\n\n" +
            "arguments:\n" +
            "  -url <url>                       administration URL.\n\n" +
            "options:\n" + 
            "  -add=<topic|queue> -name=<name>  add a destination\n" +
            "  -list                            list destinations\n" +
            "  -queue=<queue>                   number of messages on queue\n" +
            "  -topic=<topic> -name=<name>      number of messages on topic for consumer\n" +
            "  -destExists=<name>               determine whether the destination exists\n" +
            "  -help                            displays this help.\n\n" +
            "sample admin urls:\n" +
            "  tcp://localhost:3035\n" +
            "  rmi://localhost:1099");
    }
}
