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
 * $Id: RateValidator.java,v 1.2 2000/05/12 23:46:13 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */


package openjms.examples.client.fx.util;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.AttributeSet;
import java.text.DecimalFormat;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.Toolkit;
import java.text.ParseException;
import java.lang.IllegalArgumentException;
import java.lang.Double;



/**
 * Creates rates as dollars and cents. Cents are always displayed, i.e
 * as .00
 *
 * @version     $Revision: 1.2 $ $Date: 2000/05/12 23:46:13 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class RateValidator extends PlainDocument 
	implements ActionListener, FocusListener
{
	// The field displaying the rates
	private JTextComponent		textComponent_;

	// The current location of the cursor.
	private int					newOffset_;

	// format all numbers as 0.0000
	private DecimalFormat		formatter_;

	// The caller handle.
	private RateIfc			client_;
	
	/**
	 *
	 *
	 *
	 */
	public RateValidator(JTextField tc)
	{
		super();
		tc.setDocument(this);
		textComponent_ = tc;
		try
		{
			formatter_ = new DecimalFormat("0.0000");
		}
		catch(IllegalArgumentException err)
		{
			System.err.println(err + 
								" Failed to create formatter exitting....");
		
			System.exit(-1);
		}
		tc.addActionListener(this);
		tc.addFocusListener(this);
	}

	public void setNotifier(RateIfc client)
	{
		client_ = client;
	}
	

	public void insertString(int offset, String s, AttributeSet attributeSet) 
		throws BadLocationException 
	{
		try 
		{
			if (!s.equals("."))
			{
				Integer.parseInt(s);
			}
			else
			{
				// Make sure there are no other decimals.
				if (super.getText(0, super.getLength()).lastIndexOf('.') != -1)
				{
					// ALready have a decimal.
					beep();
					return;
				}
			}
		}
		catch(Exception ex) 
		{
			beep();
			return;  // only allow integer values
		}
		super.insertString(offset, s, attributeSet);
	}
	

	public void actionPerformed(ActionEvent e)
	{
		// set correct display output.
		try
		{
			if (super.getLength() == 0)
			{
				super.insertString(0, "0", null);
			}

			Number d = formatter_.parse(super.getText(0, super.getLength()));
			String num;
			
			if ((num = formatter_.format(d.doubleValue())) == null)
			{
				JOptionPane.showMessageDialog
					(null, "Invalid Rate: ", "Invalid Rate",
					 JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				try 
				{
					super.remove(0, super.getLength());
					super.insertString(0, num, null);
					client_.rateChanged(d.doubleValue());
				}
				catch(Exception ex) 
				{
					System.err.println("Unexpected format error:\n" + ex);
				}
			}
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog
				(null, err.getMessage(), "Invalid Rate",
				 JOptionPane.ERROR_MESSAGE);
		}
	}

	public void set(double rate)
	{
		try 
		{	
			super.remove(0, super.getLength());
			super.insertString(0, formatter_.format(rate), null);
		}
		catch(Exception ex) 
		{
			System.err.println("unexpected format error\n" + ex);
		}
	}
	

	public void focusGained(FocusEvent e)
	{
	}
	

	public void focusLost(FocusEvent e)
	{
		actionPerformed(null);
	}
	
	private void beep()
	{
		Toolkit.getDefaultToolkit().beep();
	}

	public String formatRate(double d)
	{
		return formatter_.format(d);
	}
	
	
} // End RateValidator
