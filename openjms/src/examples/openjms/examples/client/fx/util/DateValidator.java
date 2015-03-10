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
 * $Id: DateValidator.java,v 1.1 2000/05/09 10:16:15 mourikis Exp $
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
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.Toolkit;
import java.text.ParseException;



/**
 * Validates and formats date fields in two formats only. US mm/dd/yyyy
 * and AUD dd/mm/yyyy.
 *
 * @version     $Revision: 1.1 $ $Date: 2000/05/09 10:16:15 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         References here
 **/


public class DateValidator extends PlainDocument 
	implements CaretListener, ActionListener, FocusListener
															

{
	public static String		initString_ = "DD/MM/YYYY"; // Y10K!
	private static int			sep1_ = 2, sep2_ = 5; 
	private static int			d1_, d2_;
	private static int			m1_, m2_;
	private static int			yearStart_ = 6, yearMid_ = 8, yearEnd_ = 9;
	private JTextComponent		textComponent_;
	private int					newOffset_;
	private SimpleDateFormat	df_;
	private DateValidIfc		client_;

	/**
	 *
	 *
	 *
	 */
	public DateValidator(JTextField tc)
	{
		super();
		tc.setDocument(this);
		textComponent_ = tc;
		Date date = new Date();
		
		try 
		{
			// Just keep this very simple for demo purposes.
			if(Locale.getDefault().getDisplayName().equals
			   ("English (Australia)"))
			{
				d1_ = 0; 
				d2_ = 1;
				m1_ = 3; 
				m2_ = 4;
				df_ = new SimpleDateFormat("dd/MM/yyyy");
			}
			else
			{
				// assume US Format.
				d1_ = 3; 
				d2_ = 4;
				m1_ = 0; 
				m2_ = 1;
				df_ = new SimpleDateFormat("MM/dd/yyyy");
			}
			df_.setLenient(false);
			initString_ = df_.format(date);
			insertString(0, initString_, null);
			createCallbacks(tc);
		}
		catch(Exception ex) 
		{ 
			ex.printStackTrace(); 
		}
	}

	public void setNotifier(DateValidIfc client)
	{
		client_ = client;
	}

	private void createCallbacks(JTextField tc)
	{
		tc.addCaretListener(this);
		tc.addActionListener(this);
		tc.addFocusListener(this);
	}

	public void reset()
	{
		Date date = new Date();
		initString_ = df_.format(date);
		try
		{
			super.remove(0, super.getLength());
			super.insertString(0, initString_, null);
			client_.dateChanged(date);
		}
		catch(BadLocationException err)
		{
			System.err.println("Date format error\n" + err);
		}
	}
	

	public void insertString(int offset, String s, AttributeSet attributeSet) 
		throws BadLocationException 
	{
		if(s.equals(initString_)) 
		{
			super.insertString(offset, s, attributeSet);
		}
		else 
		{
			try 
			{
				Integer.parseInt(s);
			}
			catch(Exception ex) 
			{
				beep();
				return;  // only allow integer values
			}
			newOffset_ = offset;
			if (atSeparator(offset))
			{
				newOffset_++;	
				textComponent_.setCaretPosition(newOffset_);
			}
			if (check(s))
			{
				super.remove(newOffset_, 1);
				super.insertString(newOffset_, s, attributeSet);
			}
			else
			{
				beep();
			}
		}
	}


	private boolean check(String s) throws BadLocationException
	{
		boolean result = false;

		if (newOffset_ == d1_ || newOffset_ == d2_)
		{
			result = checkDay(s);
		}
		else if (newOffset_ == m1_ || newOffset_ == m2_)
		{
			result = checkMonth(s);
		}
		else if (newOffset_ >= yearStart_ && newOffset_ <= yearEnd_)
		{
			result = checkYear(s);
		}
		return result;
	}
	


	public void remove(int offset, int length) 
		throws BadLocationException 
	{
		if(atSeparator(offset)) 
		{
			textComponent_.setCaretPosition(offset-1);
		}
		else
		{
			textComponent_.setCaretPosition(offset);
		}
	}


	private boolean atSeparator(int offset) 
	{
		return (offset == sep1_ || offset == sep2_);
	}


	public void caretUpdate(CaretEvent e)
	{
		newOffset_ = e.getDot();
		
		if (newOffset_ == sep1_)
		{
			textComponent_.setCaretPosition(++newOffset_);
		}
		else if (newOffset_ == sep2_)
		{
			// Move to last 2 digits in year, for efficiency.
			newOffset_ = yearMid_;
			textComponent_.setCaretPosition(newOffset_);
		}
	}


	public void actionPerformed(ActionEvent e)
	{
		// Check valid date format.
		try
		{
			Date date;
			
			if ((date = df_.parse(super.getText(0, super.getLength()))) 
				== null)
			{
				JOptionPane.showMessageDialog
					(null, "Invalid Date: ", "Invalid Date",
					 JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				client_.dateChanged(date);
			}
		}
		catch(Exception err)
		{
			JOptionPane.showMessageDialog
				(null, err.getMessage(), "Invalid Date",
				 JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void focusGained(FocusEvent e)
	{
	}
	

	public void focusLost(FocusEvent e)
	{
		actionPerformed(null);
	}
	

	private boolean checkDay(String s) throws BadLocationException
	{
		int num;
		boolean result = false;
		
		if (newOffset_ == d1_)
		{
			num = Integer.parseInt(s);
			if (num >= 0 && num < 4)
			{
				result = true;
			}
		}
		else if (newOffset_ == d2_)
		{
			String st = textComponent_.getText(newOffset_-1, 1) + s;
			num = Integer.parseInt(st);
			if (num >= 0 && num < 32) 
			{
				result = true;
			}
		}
		return result;
	}
	

	private boolean checkMonth(String s) throws BadLocationException
	{
		int num;
		boolean result = false;
		
		if (newOffset_ == m1_)
		{
			num = Integer.parseInt(s);
			if (num >= 0 && num < 2)
			{
				result = true;
			}
		}
		else if (newOffset_ == m2_)
		{
			String st = textComponent_.getText(newOffset_-1, 1) + s;
			num = Integer.parseInt(st);
			if (num >= 0 && num < 13) 
			{
				result = true;
			}
		}
		return result;
	}
	

	/**
	 * The year is always valid for the purposes of this demo.
	 *
	 * @param s The year digit.
	 *
	 */
	private boolean checkYear(String s) 
	{
		return true;
	}

	private void beep()
	{
		Toolkit.getDefaultToolkit().beep();
	}



	/**
	 * Get tofays date formated appropriatley for the two locales we 
	 * are interested in.
	 *
	 * @return String Todays date in the locale format.
	 *
	 */
	public String getToday()
	{
		return df_.format(new Date());
	}
	
	
} // End DateValidator
