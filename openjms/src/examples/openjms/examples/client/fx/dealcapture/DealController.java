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
 * $Id: DealController.java,v 1.5 2002/02/19 09:09:47 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.dealcapture;

import java.util.Date;
import java.util.HashMap;
import java.lang.Double;


import javax.jms.TopicConnectionFactory;

import openjms.examples.client.fx.util.DateValidator;
import openjms.examples.client.fx.util.DateValidIfc;
import openjms.examples.client.fx.util.AmountValidator;
import openjms.examples.client.fx.util.AmountIfc;
import openjms.examples.client.fx.util.RateValidator;
import openjms.examples.client.fx.util.RateIfc;
import openjms.examples.client.fx.dealmanager.DealData;
import openjms.examples.client.fx.dealmanager.DealIfc;


/**
 * Class description here
 *
 * @version     $Revision: 1.5 $ $Date: 2002/02/19 09:09:47 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class DealController  
	implements AmountIfc, DateValidIfc, CurrencyIfc, RateIfc, DealIfc
{
	// Performs all date validations
	DateValidator		dateValidator_;
	
	// performs all amount validations
	AmountValidator		amountValidator_;

	// performs all rate validations
	RateValidator		rateValidator_;

	// Swaps currencies.
	CurrencySelector	currencySelector_;
	
	// Swaps Buy/Sell.
	BuySellSelector		buySellSelector_;

	// A handle to the client.
	DealNotifierIfc		client_;

	// The dealHolder
	DealData			dealData_;

	// Currency 1
	String				c1_ = null;
	
	// Currency 2
	String				c2_ = null;
	
	// The rate for the selected currency.
	double				rate_ = 0;

	// The spot date
	Date				spotDate_;
	
	// The current amounts.
	double				amount1_ = -1;

	// A simple storage for rates.
	HashMap				rates_;
	
	// Indicates whether the currencies are swapped.
	boolean				swapped_ = false;

	/**
	 *
	 *
	 *
	 */
	public DealController(DealNotifierIfc client,  
                          TopicConnectionFactory factory)
	{
		client_ = client;
		dealData_ = new DealData(this, factory);
		// Only one product supported.
		dealData_.setDealType("Spot");
		rates_ = new HashMap();
		rates_.put(new String("AUD"), new Double("1.7145"));
		rates_.put(new String("EUR"), new Double("1.0921"));
		rates_.put(new String("FRF"), new Double("7.1634"));
		rates_.put(new String("GBP"), new Double("0.6441"));
		rates_.put(new String("JPY"), new Double("108.78"));
	}

	public void setDateValidator(DateValidator dateValidator)
	{
		dateValidator_ = dateValidator;
		dateValidator_.setNotifier(this);
	}
	
	public void setAmountValidator(AmountValidator amountValidator)
	{
		amountValidator_ = amountValidator;
		amountValidator_.setNotifier(this);
	}

	public void setRateValidator(RateValidator rateValidator)
	{
		rateValidator_ = rateValidator;
		rateValidator_.setNotifier(this);
	}

	public void setCurrencySelector(CurrencySelector currencySelector)
	{
		currencySelector_ = currencySelector;
		currencySelector_.setNotifier(this);
	}

	public void setBuySellSelector(BuySellSelector buySellSelector)
	{
		buySellSelector_ = buySellSelector;
	}

	public void amountChanged(double d)
	{
		double am2;
		
		amount1_ = d;
		if (!swapped_)
		{
			am2 = d * rate_;
		}
		else
		{
			if (rate_ == 0)
			{
				am2 = 0;
			}
			else
			{
				am2 = d/rate_;
			}
		}
		
		client_.setAmount2(amountValidator_.formatAmount(am2));
		dealData_.setAmount1
			((d == 0 ? null : amountValidator_.formatAmount(d)));
		dealData_.setAmount2
			((am2 == 0 ? null: amountValidator_.formatAmount(am2)));
	}
	

	public void dateChanged(Date date)
	{
		spotDate_ = date;
		dealData_.setValueDate(date);
	}


	public void rateChanged(double rate)
	{
		rate_ = rate;
		if (amount1_ != -1)
		{
			amountChanged(amount1_);
		}
		dealData_.setSpotRate
			((rate_ == 0 ? null : rateValidator_.formatRate(rate_)));
	}

	public void setCurrencies(String c1, String c2)
	{
		c1_ = c1;
		c2_ = c2;
		dealData_.setCurrency1(c1_);
		dealData_.setCurrency2(c2_);
		currencySelector_.setCurrencies(c1, c2);
		reset();
	}


	private void reset()
	{
		Double rate = (Double)rates_.get(c2_);
		Date date = new Date();
		
		rate_ = rate.doubleValue();
		dealData_.setSpotRate(rateValidator_.formatRate(rate_));
		rateValidator_.set(rate_);
		dateValidator_.reset();
		dealData_.setValueDate(date);
		dealData_.setTradingDate(date);
		amountValidator_.reset();
		dealData_.setAmount1(null);
		dealData_.setAmount2(null);
		client_.setMarketrate(rateValidator_.formatRate(rate_));
		dealData_.setMarketRate(rateValidator_.formatRate(rate_));
		client_.reset();
		swapped_ = false;
		amount1_ = -1;
		buySellSelector_.reset();
	}
	

	public void swapCurrencies(String mainCurrency)
	{
		swapped_ = (swapped_ ? false : true);
		if (amount1_ != -1)
		{
			amountChanged(amount1_);
		}
		if (swapped_)
		{
			dealData_.setCurrency1(c1_);
			dealData_.setCurrency2(c2_);
		}
		
	}
	
	public boolean commit()
	{
		dealData_.setBuySell(buySellSelector_.getCurrency1Indicator());
		return dealData_.commit();
	}


	public void canCommit(boolean flag)
	{
		client_.canCommit(flag);
	}
	

} // End DealController
