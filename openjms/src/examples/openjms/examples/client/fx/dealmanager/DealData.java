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
 * $Id: DealData.java,v 1.8 2002/02/19 09:09:47 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.dealmanager;

import java.util.Date;
import java.util.Hashtable;
import java.lang.String;
import java.io.Serializable;
import java.io.PrintStream;

import javax.jms.Session;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicPublisher;
import javax.jms.Topic;
import javax.jms.ObjectMessage;
import javax.jms.DeliveryMode;
import javax.jms.TopicConnectionFactory;
import org.exolab.jms.util.CommandLine;

/**
 * A serializable container for storing the dealData.
 *
 * @version     $Revision: 1.8 $ $Date: 2002/02/19 09:09:47 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class DealData implements Serializable
{
	private String					dealType_;
	
	private Date					valueDate_;
	
	private Date					tradingDate_;
	
	private String					currency1_;
	
	private String					currency2_;
	
	private String					amount1_;
	
	private String					amount2_;
	
	private String					spotRate_;
	
	private String					marketRate_;

	private String					buySell_;

	transient private TopicConnection			connection_;

	transient private DealIfc		client_;
	
	transient private int			complete_ = 0;
	
	transient private static final int	dealTypeSet_ = 1;
	transient private static final int	valueDateSet_ = 2;
	transient private static final int	tradingDateSet_ = 4;
	transient private static final int	currency1Set_ = 8;
	transient private static final int	currency2Set_ = 16;
	transient private static final int	amount1Set_ = 32;
	transient private static final int	amount2Set_ = 64;
	transient private static final int	spotRateSet_ = 128;
	transient private static final int	marketRateSet_ = 256;
	transient private static final int	allSet_ = 511;

	public DealData(DealIfc client,  TopicConnectionFactory factory)
	{
		client_ = client;
		try
		{
			connection_ = factory.createTopicConnection();
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			System.exit(-1);
		}
	}
	

	public void setDealType(String dealType)
	{
		dealType_ = dealType;
		update(dealTypeSet_ * (dealType == null ? -1 : 1)); 
	}
	

	public void setValueDate(Date date)
	{
		valueDate_ = date;
		update(valueDateSet_ * (date == null ? -1 : 1)); 
	}
	

	public void setTradingDate(Date date)
	{
		tradingDate_ = date;
		update(tradingDateSet_ * (date == null ? -1 : 1));
	}


	public void setCurrency1(String c)
	{
		currency1_ = c;
		update(currency1Set_ * (c == null ? -1 : 1));
	}


	public void setCurrency2(String c)
	{
		currency2_ = c;
		update(currency2Set_ * (c == null ? -1 : 1));
	}


	public void setAmount1(String am)
	{
		amount1_ = am;
		update(amount1Set_ * (am == null || am.length() == 0 ? -1 : 1));
	}


	public void setAmount2(String am)
	{
		amount2_ = am;
		update(amount2Set_ * (am == null ? -1 : 1));
	}


	public void setSpotRate(String r)
	{
		spotRate_ = r;
		update(spotRateSet_ * (r == null ? -1 : 1));
	}


	public void setMarketRate(String r)
	{
		marketRate_ = r;
		update(marketRateSet_ * (r == null ? -1 : 1));
	}

	public void setBuySell(String b)
	{
		buySell_ = b;
	}
	
	public String getDealType()
	{
		return dealType_;
	}
	

	public Date getValueDate()
	{
		return valueDate_;
	}
	

	public Date getTradingDate()
	{
		return tradingDate_;
	}


	public String getCurrency1()
	{
		return currency1_;
	}


	public String getCurrency2()
	{
		return currency2_;
	}


	public String getAmount1()
	{
		return amount1_;
	}


	public String getAmount2()
	{
		return amount2_;
	}


	public String getSpotRate()
	{
		return spotRate_;
	}


	public String getMarketRate()
	{
		return marketRate_;
	}


	public String getBuySell1()
	{
		return buySell_;
	}

	public String getBuySell2()
	{
		return (buySell_.equals("Buy") ? "Sell" : "Buy");
	}

	public void update(int i)
	{
		int num = (i < 0 ? i*-1 : i);
		
		if (i < 0)
		{
			if (complete_ >= allSet_)
			{
				client_.canCommit(false);
			}
			complete_ |= num;
			complete_ ^= num;
		}
		else
		{
			complete_ |= num;
		}
		
		if (complete_ >= allSet_)
		{
			client_.canCommit(true);
		}
	}
	
	public boolean commit()
	{
		boolean success = true;
		
		try
		{
			TopicSession session = connection_.createTopicSession
				(false, Session.CLIENT_ACKNOWLEDGE);
			Topic topic = session.createTopic("OpenJMSDemo-deals");
			TopicPublisher publisher = session.createPublisher(topic);
			int delivery_mode = DeliveryMode.PERSISTENT;
			ObjectMessage message = session.createObjectMessage(this);
			publisher.publish(message, delivery_mode, 1, 0);
			//			session.close();
		}
		catch (Exception exception)
		{
			success = false;
			exception.printStackTrace();
		}
		
		return success;
	}
	
} // End DealData
