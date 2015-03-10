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
 * $Id: SimplePublisher.java,v 1.6 2001/12/15 11:56:27 mourikis Exp $
 *
 * Date         Author  Changes
 * 01/01/2001   fabien  Created
 */
package openjms.examples.profiling;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;
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
import javax.jms.TopicPublisher;
import javax.jms.Topic;
import javax.jms.TextMessage;
import javax.jms.ObjectMessage;
import javax.jms.DeliveryMode;
import javax.jms.TopicConnectionFactory;

// Exolab
import org.exolab.jms.util.CommandLine;
import org.exolab.jms.client.JmsTopicConnectionFactory;
import org.exolab.jms.jndi.JndiConstants;

// Fabien
import java.util.*;
import java.io.*;

public class SimplePublisher
{
    static public void main(String[] args)
    {
        try
        {
              // Variables set
                  // Administred object accessing
                  int jndiPort = 1099;
                  String jndiHost = "localhost";
                  // JMS Messaging
                  int ackType = Session.AUTO_ACKNOWLEDGE;
                  int count = 500;
                  boolean infiniteCount = false;
                  boolean heavyweight = false;
                  int weight = 150000;
                  int persistency = DeliveryMode.NON_PERSISTENT;
                  int sleepTime = 0;
                  boolean textMessages = false;
                  String topicName = "topic";
                  boolean transacted = false;
              // Misc
              long absCount = 0;

              // Retreiving the command line arguments with an Open JMS utility
              CommandLine cmdline = new CommandLine(args);
              
              String connector = null;

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
                  if (cmdline.exists("count"))
                  {
                      count = Integer.parseInt(cmdline.value("count"));
                      System.out.println("Messages count parametered");
                  }
                  if (cmdline.exists("infinite"))
                  {
                      infiniteCount = true;
                      System.out.println("Infinite messages count parametered");
                  }
                  if (cmdline.exists("heavyweight"))
                  {
                      heavyweight = true;
                      weight = Integer.parseInt(cmdline.value("heavyweight"));
                      System.out.println("Heavyweight messages parametered");
                  }
                  if (cmdline.exists("persistent"))
                  {
                      persistency = DeliveryMode.PERSISTENT;
                      System.out.println("Persistent messages delivery parametered");
                  }
                  if (cmdline.exists("sleep"))
                  {
                      sleepTime = Integer.parseInt(cmdline.value("sleep"));
                      System.out.println("Sleep parametered");
                  }
                  if (cmdline.exists("text"))
                  {
                      textMessages = true;
                      System.out.println("Text messages parametered");
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
                  if (cmdline.exists("mode")) {
                        connector = cmdline.value("mode");
                  }
                  // Misc
                  if (cmdline.exists("help"))
                  {
                      usage();
                  }
                  if (cmdline.exists("test"))
                  {
                    System.out.println("jndiPort : " + jndiPort);
                    System.out.println("jndiHost : " + jndiHost);
                    System.out.println("ackType : " + ackType);
                    System.out.println("count : " + count);
                    System.out.println("infiniteCount : " + infiniteCount);
                    System.out.println("heavyweight : " + heavyweight);
                    System.out.println("persistency : " + persistency);
                    System.out.println("textMessages : " + textMessages);
                    System.out.println("topicName : " + topicName);
                    System.out.println("transacted : " + transacted);
                    System.exit(0);
                  }


              // connect to the JNDI server and get a reference to
              // root context
              Properties sysprops = new Properties(System.getProperties());
              try
              {
                sysprops.load(new BufferedInputStream(new FileInputStream("OpenJMS.properties")));
                System.setProperties(sysprops);
              }
              catch(Exception err)
              {
                System.err.println("Error reading OpenJMS.properties file\n" +
                err.getMessage());
              }

              Hashtable props = new Hashtable();
              String modeType = null;

              if (connector != null &&  connector.equals("ipc")) {
                  modeType = 
                      "org.exolab.jms.jndi.mipc.IpcJndiInitialContextFactory";
                  System.out.println("Using IPC connection");
              } else {
                   modeType = 
                       "org.exolab.jms.jndi.rmi.RmiJndiInitialContextFactory";
              }
              
              
              props.put(JndiConstants.PORT_NUMBER_PROPERTY, new Integer(jndiPort));
              props.put(JndiConstants.HOST_PROPERTY, jndiHost);
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

              // Create a connection with the ConnectionFactory
              TopicConnection connection = factory.createTopicConnection();
              connection.start();
              System.out.println("Connection ready");

              // Create a session within the connection
              TopicSession session = connection.createTopicSession(transacted,ackType);
              System.out.println("Session ready");

              // Looking for / Creating a topic
              Topic topic = null;
              try
              {
                  topic = session.createTopic(topicName);
                  //topic = (Topic)context.lookup(topicName);
              }
              catch (Exception e)
              {
                  System.err.println("Failed to create Topic");
              }
              System.out.println("Topic - " + topicName + " ready");


              // Creating publisher
              TopicPublisher publisher = session.createPublisher(topic);
              System.out.println("Publisher ready");


              System.out.println("Publishing Messages ...");


              // Messages preparing

              // Messages Type
              TextMessage tMessage=null;     // Using simple text messages
              ObjectMessage oMessage=null;   // Using Lumos Alarm

              if (textMessages)
              {
                  // Setting the JMS Message
                  tMessage = session.createTextMessage();
                  tMessage.setText("JMS Text Message");
				  System.out.println("Size of Text : " + size("JMS Text Message"));
				  System.out.println("Size of JMS Text Message : " + size((Serializable)tMessage));				  
              }
              else
              {
                  // Defining Message structure variables
                  AlarmImpl mAlarm;
				  String mEntity = "Company" + "Lumos" + "Dept." + "R+D" + "User" + "Fabien" + "IP" + "10.0.0.137";
 				  String addData = "Vive la France ";
                  if (heavyweight)
                  {
                      addData = new String(new byte[weight]);
                      System.out.println("Heavyweight object building, size of addData : " + addData.length());

                  }
                  ArrayList mList = new ArrayList();
                  mList.add(0,"Item1");

                  // Creating an Alarm
                  mAlarm = new AlarmImpl(   // int id,
                                            0,
                                             // String name,
                                            "JMSTest",
                                            // EntityId src,
                                            mEntity,
                                            // String ct, - Condition Type
                                            "Message",
                                            // AlarmType type,
                                            "UNKNOWN",
                                            // AlarmSeverity sev,
                                            "WARNING",
                                            // long creTime,
                                            System.currentTimeMillis(),
                                            // boolean isSA, - Service Affecting ?
                                            true,
                                            // boolean isCAuto, - Auto Cleared
                                            false,
                                            // String add, - Additional Data
                                            addData,
                                            // List ad,
                                            mList,
                                            // boolean ack,
                                            true,
                                            // boolean isC - isCleared
                                            false
                                            );

                  // Setting the JMS Message
                  oMessage = session.createObjectMessage();
                  oMessage.setObject(mAlarm);
				  System.out.println("Size of Alarm : " + size(mAlarm));
				  System.out.println("Size of JMS Object Message : " + size((Serializable)oMessage));				                    

              }

              // Sending Messages
              long startTime = System.currentTimeMillis();
              if (textMessages)
              {
                  do
                  {
                      for (int i=0;i<count;i++)
                      {
                          absCount++;
                          publisher.publish(tMessage, persistency, 1, Long.MAX_VALUE);
                          //System.out.print("\rmsg: " + absCount);
                          //Thread.sleep(sleepTime);
                      }
                      System.out.print("\rmsg: " + absCount);
                      
                      if (infiniteCount)
                      {
                          // Mesurements
                          long endTime = System.currentTimeMillis();
                          Date startDate = new Date(startTime);
                          Date endDate = new Date(endTime);
                          long duration_ms = endTime - startTime;
                          double duration_s = duration_ms/1000;
                          double msgRate = absCount/duration_s;
                          System.out.println();
                          System.out.println("Started sending at : " + startDate
                                              + "\nCount at : " + endDate
                                              + "\nDuration : " + duration_s + " sec."
                                              + "\nMessage count : " + absCount
                                              + "\nMessage sending rate : " + msgRate + " msg/s.\n");
                      }
                  }
                  while (infiniteCount);
              }
              else
              {
                  do
                  {
                      for (int i=0;i<count;i++)
                      {
                          absCount++;
                          publisher.publish(oMessage, persistency, 1, Long.MAX_VALUE);
                          //System.out.print("\rmsg: " + absCount);
                          //Thread.sleep(sleepTime);
                      }
                      System.out.print("\rmsg: " + absCount);
                      
                      if (infiniteCount)
                      {
                          // Mesurements
                          long endTime = System.currentTimeMillis();
                          Date startDate = new Date(startTime);
                          Date endDate = new Date(endTime);
                          long duration_ms = endTime - startTime;
                          double duration_s = duration_ms/1000;
                          double msgRate = absCount/duration_s;
                          System.out.println();
                          System.out.println("Started sending at : " + startDate
                                              + "\nCount at : " + endDate
                                              + "\nDuration : " + duration_s + " sec."
                                              + "\nMessage count : " + absCount
                                              + "\nMessage sending rate : " + msgRate + " msg/s.\n");
                      }
                  }
                  while (infiniteCount);
              }
              long endTime = System.currentTimeMillis();

              // Mesurements
              Date startDate = new Date(startTime);
              Date endDate = new Date(endTime);
              long duration_ms = endTime - startTime;
              double duration_s = duration_ms/1000;
              double msgRate = count/duration_s;
              System.out.println();
              System.out.println("Started sending at : " + startDate
                                  + "\nEnded sending at : " + endDate
                                  + "\nSent Duration : " + duration_s + " sec."
                                  + "\nMessage count : " + count
                                  + "\nMessage sending rate : " + msgRate + " msg/s.");


              // Closing communication objects
              publisher.close();
              session.close();
              connection.close();

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            // run the gc and the finalizer
            System.gc();
            System.runFinalization();
            System.exit(0);
        }

    }

	/**
	*
	*/ 
	private static int size( Serializable x)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream( baos);
			oos.writeObject( x);
			oos.close();
			baos.close();
			return baos.toByteArray().length;
		}
		catch( IOException io)
		{
			io.printStackTrace();
			return -1;
		}
	}

    static protected void usage()
    {
		PrintStream out = System.out;

		out.println("\n\n");
		out.println("==================================================================================");
		out.println("        Usage information for openjms.examples.profiling.SimplePublisher");
		out.println("==================================================================================");
		out.println();
		out.println("openjms.examples.profiling.SimplePublisher");
		out.println();
		out.println("    [-help | -jndiport <port number> | -jndihost <host address> | -ack <auto/client/dups>");
		out.println("     -count <msg nb> | -infinite | -heavyweight <size_bytes> | -persistent | -text");
                out.println("     -topic <topic name> | -transacted | -test]");
		out.println();
		out.println("Misc. options :");
		out.println("\t-help         displays this screen");
		out.println("\t-test         displays command line argument values");
		out.println();
		out.println("Administered objects options :");
		out.println("\t-jndiport     port where the jndi server runs    - Defaut = 1099");
		out.println("\t-jndihost     host where jndi server runs        - Defaut = localhost");
		out.println();
		out.println("JMS Messaging options :");
		out.println("\t-ack          \"auto\", \"client\" or \"dups\"        - Default = auto");
		out.println("\t-count        number of message to be sent            - Default = 500");
		out.println("\t-inifite      infinite number of message to be sent   - Default = false");
		out.println("\t-heavyweight  size of the alarm to send               - Default = false");
		out.println("\t-persistent   make the messages persistent            - Default = false");
		out.println("\t-text         only text messages to be sent           - Default = false");
		out.println("\t-topic        topic name to subscriber under.         - Default = topic");
		out.println("\t-transacted   use transacted session with the Topic.  - Default = false");
                out.println("\n\n");
                System.exit(0);
    }


}
