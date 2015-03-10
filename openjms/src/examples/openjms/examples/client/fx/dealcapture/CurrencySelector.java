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
 * $Id: CurrencySelector.java,v 1.1 2000/05/09 10:15:40 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.dealcapture;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Handle the currency swapping when the currency button is pressed.
 * Notify the caller, after all swapping is done.
 *
 * @version     $Revision: 1.1 $ $Date: 2000/05/09 10:15:40 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class CurrencySelector implements ActionListener
{
	// The button to monitor.
	JButton		button_;

	// The lable containing the other currency
	JLabel		label_;

	// Currency 1
	String		c1_;
	
	// Currency 2
	String		c2_;

	// Client interested in events.
	CurrencyIfc	client_;
	
	
	/**
	 *
	 *
	 *
	 */
	public CurrencySelector(JButton b, JLabel l)
	{
		button_ = b;
		label_ = l;
		button_.addActionListener(this);
	}


	public void setCurrencies(String c1, String c2)
	{
		c1_ = new String(c1);
		c2_ = new String(c2);
		button_.setText(c1_);
		label_.setText(c2_);
	}
	

	public void setNotifier(CurrencyIfc client)
	{
		client_ = client;
	}
	

	/**
	 * The Currency button has been pressed.
	 *
	 * @param e The event the triggered the callback.
	 *
	 */
	public void actionPerformed(ActionEvent e) 
	{
		switchCurrencies();
		client_.swapCurrencies(c1_);
	}
	
	/**
	 * Reset to default.
	 *
	 */
	public void reset()
	{
		button_.setText(c1_);
		label_.setText(c2_);
	}

	/**
	 * Swap the currencies around. Set the button to display the label currency
	 * and the label currency to display the button currency. Finaly notify
	 * the caller.
	 *
	 */
	private void switchCurrencies()
	{
		String tmp = new String(c1_);
		
		c1_ = c2_;
		c2_ = tmp;
		button_.setText(c1_);
		label_.setText(c2_);
	}
	
} // End CurrencySelector
