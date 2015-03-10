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
 * $Id: DealManager.java,v 1.10 2002/02/19 09:09:47 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.dealmanager;

import java.util.Hashtable;
import javax.naming.InitialContext;
import javax.naming.Context;
import javax.jms.Session;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.TopicPublisher;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.ObjectMessage;
import javax.jms.TopicConnectionFactory;
import java.util.Hashtable;
import javax.jms.DeliveryMode;
import org.exolab.jms.util.CommandLine;
import org.exolab.jms.client.JmsTopicConnectionFactory;
import openjms.examples.client.fx.dealmanager.DealData;

/**
 * Class description here
 *
 * @version     $Revision: 1.10 $ $Date: 2002/02/19 09:09:47 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class DealManager implements MessageListener
{
	private static TopicConnectionFactory	factory_;
	private TopicConnection			connection_;
    final static private String ipcMode_ = "org.exolab.jms.jndi.mipc.IpcJndiInitialContextFactory";
	final static private String rmiMode_ = "org.exolab.jms.jndi.rmi.RmiJndiInitialContextFactory";


	/**
	 * Default constructor.
	 *
	 *
	 */
	public DealManager()
	{
		try
		{
			connection_ = factory_.createTopicConnection();
			
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Process all incoming messages. Add the deals to the dealBlotter display
	 * as they arrive.
	 *
	 * @param Message The deal data.
	 *
	 */
	public void onMessage(Message message)
    {
		try
		{
			ObjectMessage m = (ObjectMessage)message;
			DealData  data = (DealData)m.getObject();
			String newTopic = "OpenJMSDemo.USD-" + data.getCurrency2() 
				+ "-SPOT";

			// Extract the message and forward, else the ack will fail
			// since destination gets changed.
			if (forward(newTopic, data))
			{
				message.acknowledge();
			}
			else
			{
				System.err.println
					("Failed to publish message. Check errors. Exiting.....");
				System.exit(-1);
			}
		}
		catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }
	
	
	private boolean forward(String newTopic, DealData data)
	{
		boolean success = true;
		
		try
		{
			TopicSession session = connection_.createTopicSession
				(false, Session.CLIENT_ACKNOWLEDGE);
            connection_.start();
			Topic topic = session.createTopic(newTopic);
			TopicPublisher publisher = session.createPublisher(topic);
			int delivery_mode = DeliveryMode.NON_PERSISTENT;
			// need to copy message else destination will change, and ack will
			// fail.
			ObjectMessage message = session.createObjectMessage(data);
			publisher.publish(message, delivery_mode, 1, 0);
			session.close();
		}
		catch (Exception exception)
		{
			success = false;
			exception.printStackTrace();
		}
		return success;
	}
	

	/**
     * Print out information on running this sevice
     */
    static protected void usage()
    {
		java.io.PrintStream out = System.out;

		out.println("\n\n");
		out.println("====================================================================");
		out.println("Usage information for openjms.examples.client.fx.dealmanager.DealManager");
		out.println("====================================================================");
		out.println("openjms.examples.client.fx.dealmanager.DealManager\n");
		out.println("    [-help | -mode <rmi/ipc> \n");
		out.println("\t-help   displays this screen\n");
		out.println("\t-mode    connect in either ipc mode or rmi mode.\n");
    }


	/**
	 * Create the DealManager. Pick up all deals and re-publish under the
	 * book name.
	 *
	 * @param args the command line arguments
	 *
	 */
	public static void main (String args[])
	{
		try
		{
			CommandLine cmdline = new CommandLine(args);
			if (cmdline.exists("help"))
			{
				usage();
			}
            String md = cmdline.value("mode");
            String uri = "rmi://";
            String mode = rmiMode_;
            String host = "localhost";
            String port = "1099";
            String jndiServer = "JndiServer";
            if (md != null && md.equals("ipc")) {
                System.out.println("Using IPC mode");
                mode = ipcMode_;
                port = "3035";
                uri = "tcp://";
                jndiServer = "";
            }
            
            if (cmdline.exists("jndiport")) {
                port = cmdline.value("jndiport");
            }
            if (cmdline.exists("jndihost")) {
                host = cmdline.value("jndihost");
            }
            Hashtable props = new Hashtable();
            props.put(Context.PROVIDER_URL, 
                      uri + host + ":" + port + '/' + jndiServer);
            props.put(Context.INITIAL_CONTEXT_FACTORY, mode);
            try {
                Context context = new InitialContext(props);
                factory_ = (TopicConnectionFactory) 
                    context.lookup("JmsTopicConnectionFactory");
            } catch (Exception err) {
                err.printStackTrace();
                System.err.println("Exiting");
                System.exit(-1);
            }
		
			 TopicConnection connection = factory_.createTopicConnection();
			 TopicSession session = connection.createTopicSession
				 (false, Session.CLIENT_ACKNOWLEDGE);
             connection.start();
			 Topic topic = session.createTopic("OpenJMSDemo-deals");
			 // if the 'name' option has been specified then assume a 
			 // durable subscriber otherwise transient
			 TopicSubscriber subscriber = null;
			 subscriber = session.createDurableSubscriber(topic, "DealMgr");
			 subscriber.setMessageListener(new DealManager());
			 System.err.println("Message listener has been set");
		}
		catch (Exception exception)
        {
            exception.printStackTrace();
        }
	}
	

} // End DealManager
