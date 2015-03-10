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
 * $Id: BuySellSelector.java,v 1.1 2000/05/09 10:15:40 mourikis Exp $
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
 * Handle the buy/sell swapping when the buy/sell button is pressed.
 *
 * @version     $Revision: 1.1 $ $Date: 2000/05/09 10:15:40 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class BuySellSelector implements ActionListener
{
	// The button to monitor.
	JButton		button_;

	// The lable containing the other currency
	JLabel		label_;

	// BuySell 1
	String		buttonString_;
	
	// BuySell 2
	String		labelString_;
	
	/**
	 * Get the strings for from the button and the label. And set the button
	 * callback.
	 *
	 */
	public BuySellSelector(JButton b, JLabel l)
	{
		button_ = b;
		label_ = l;
		buttonString_ = button_.getText();
		labelString_ = label_.getText();
		button_.addActionListener(this);
	}

	/**
	 * The BuySell button has been pressed.
	 *
	 * @param e The event the triggered the callback.
	 *
	 */
	public void actionPerformed(ActionEvent e) 
	{
		switchBuySell();
	}
	


	/**
	 * Swap the buy/sell around. Set the button to display the label value
	 * and the label to display the button value.
	 *
	 */
	private void switchBuySell()
	{
		String tmp = new String(buttonString_);
		
		buttonString_ = labelString_;
		labelString_ = tmp;
		button_.setText(buttonString_);
		label_.setText(labelString_);
	}


	/**
	 * Reset to default.
	 *
	 */
	public void reset()
	{
		buttonString_ = "Buy";
		labelString_ = "Sell";
		
		button_.setText(buttonString_);
		label_.setText(labelString_);
	}
	


	/**
	 * Get the buy sell indicator for currency 1.
	 *
	 * @return String Get the currency 1 string.
	 *
	 */
	String getCurrency1Indicator()
	{
		return buttonString_;
	}
	

	/**
	 * Get the buy sell indicator for currency 2.
	 *
	 * @return String Get the currency 2 string.
	 *
	 */
	String getCurrency2Indicator()
	{
		return labelString_;
	}
	
} // End BuySellSelector
