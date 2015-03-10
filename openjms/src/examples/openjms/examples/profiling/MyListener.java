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
 * $Id: MyListener.java,v 1.4 2001/12/15 11:56:27 mourikis Exp $
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

// Fabien
import java.util.Date;
import java.lang.String;
import java.util.ArrayList;


public class MyListener implements MessageListener, Runnable
{

    // Messages
    private boolean readMsg;
    private int ackMode;
    private int clientAckNb;
    private int nonAckMsg;

    // Mesurement
    private long startTime;
    private long msgReceived = 0;
    private boolean started = false;
    private int displayCount;

    // Message Receipt monitoring
    private boolean onMessageCalled = false;
    private boolean onMessageOperating = false;
    private int endWaitTime;
    private int waitTime;


    // Constructor
    MyListener(boolean msgReading, int acknowledgeMode, int waitingTime, int clientAckSize, int count)
    {
        readMsg = msgReading;
        waitTime = Integer.MAX_VALUE;
        endWaitTime = waitingTime;
        ackMode = acknowledgeMode;
        clientAckNb = clientAckSize;
        nonAckMsg = 0;
        displayCount = count;
    }


    // Message receipt monitor
    public void run()
    {
        System.out.println("onMessage monitor Thread up ...");
        while (true)
        {
            synchronized (this)
            {
                try
                {
                    wait(waitTime);
                    if (onMessageOperating)
                    {
                       wait();
                    }
                }
                catch (Exception e) {}
                if (onMessageCalled)
                {
                    waitTime = endWaitTime;
                    onMessageCalled = false;
                }
                else
                {
                    System.out.println();
                    System.out.println();
                    System.out.println("No messages received for " + waitTime/1000 + " sec. Stopping subscriber ...");
                    long endTime = System.currentTimeMillis() - (long) waitTime;
                    Date startDate = new Date(startTime);
                    Date endDate = new Date(endTime);
                    long duration_ms = endTime - startTime;
                    double duration_s = duration_ms/1000;
                    double msgRate = msgReceived/duration_s;
                    System.out.println();
                    System.out.println("Started receiving at : " + startDate
                                     + "\nEnded receiving at : " + endDate
                                     + "\nReceiving Duration : " + duration_s + " sec."
                                     + "\nMessage count : " + msgReceived
                                     + "\nMessage receiving rate : " + msgRate + " msg/s.");
                    System.exit(-1);
                }
            }
        }
    }

    public void onMessage(Message msg)
    {
        onMessageOperating = true;

        try
        {
            // Message counter
            if (!started)
            {
                startTime = System.currentTimeMillis();
                started = true;
            }
            msgReceived++;

            // Message Treatement
            if(msg==null)
            {
                System.out.println(" : message null ");
            }
            else
            {
                if (readMsg)
                {
                        if(msg instanceof ObjectMessage)
                        {
                            ObjectMessage message = (ObjectMessage) msg;
                            AlarmImpl myAlarm;
                            String displayAlarm = "";
                            try
                            {
                                myAlarm = (AlarmImpl) message.getObject();
                                displayAlarm = myAlarm.getName();
                                //System.out.print("\rAlarm : " + displayAlarm + " Nb : " + msgReceived);
                            }
                            catch (JMSException e)
                            {
                                System.err.println("Cannot Retreive Alarm from msg : " + e.getMessage());
                            }
                        }
                        else if(msg instanceof TextMessage)
                        {
                            //System.out.print("\rMessage :" + ((TextMessage)msg).getText() + " Nb : " + msgReceived);
                        }
                  }
                  else
                  {
                      //System.out.print("\rMessage nb : " + msgReceived);
                  }
                  if (ackMode == Session.CLIENT_ACKNOWLEDGE)
                  {
                      nonAckMsg++;
                      if (nonAckMsg == clientAckNb)
                      {
                           msg.acknowledge();
                           System.out.println(msgReceived + " msg received. Last " + clientAckNb + " acknowledged.");
                           nonAckMsg = 0;
                      }
                  }
                  if ((msgReceived % displayCount) == 0)
                  {
                      // Mesurements
                      long endTime = System.currentTimeMillis();
                      Date startDate = new Date(startTime);
                      Date endDate = new Date(endTime);
                      long duration_ms = endTime - startTime;
                      double duration_s = duration_ms/1000;
                      double msgRate = msgReceived/duration_s;
                      System.out.println();
                      System.out.println("Started receiving at : " + startDate
                                          + "\nCount at : " + endDate
                                          + "\nDuration : " + duration_s + " sec."
                                          + "\nMessage count : " + msgReceived
                                          + "\nMessage receiving rate : " + msgRate + " msg/s.\n");
                  }
            }
        }
        catch (Exception exc)
        {
            System.out.println(" Exception caught :" + exc);
            exc.printStackTrace();
        }

        onMessageOperating = false;
        onMessageCalled = true;
        synchronized(this)
        {
            notifyAll();
        }
    }

/*


    }
*/
}
