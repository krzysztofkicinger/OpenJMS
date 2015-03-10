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
 * $Id: DealCapture.java,v 1.7 2002/02/19 09:09:47 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */

 
package openjms.examples.client.fx.dealcapture;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.AWTEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;


import javax.jms.TopicConnectionFactory;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.Context;
import openjms.examples.client.fx.util.DateValidator;
import openjms.examples.client.fx.util.AmountValidator;
import openjms.examples.client.fx.util.RateValidator;
import org.exolab.jms.util.CommandLine;

/**
 * The DealCapture is a small demo to introduce the power of OpenJMS. Deals
 * are created by the DealCapture then published via OpenJMS to the 
 * dealBlotter.
 *
 *
 * @version     $Revision: 1.7 $ $Date: 2002/02/19 09:09:47 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         openjms.examples.client.fx.dealblotter.DealTable
 * @see			openjms.examples.client.fx.dealblotter.DealBlotter
 **/


public class DealCapture extends JFrame implements DealNotifierIfc
{

	public DealCapture() 
	{
		initComponents ();
		pack ();
	}

	private void initComponents () 
	{
		commands_ = new JPanel ();
		separator_ = new JSeparator ();
		panel1_ = new JPanel ();
		commit_ = new JButton ();
		cancel_ = new JButton ();
		amounts_ = new JPanel ();
		date_ = new JTextField(6);
		currencySelect_ = new JButton ();
		buySellSelect_ = new JButton ();
		amount1_ = new JTextField ();
		amount2_ = new JTextField ();
		currency2Label_ = new JLabel ();
		buySellLabel_ = new JLabel ();
		spacer3_ = new JLabel ();
		spacer4_ = new JLabel ();
		spotRateField_ = new JTextField ();
		currencySelection_ = new JPanel ();
		currency1_ = new JComboBox ();
		currency2_ = new JComboBox ();
		marketRate_ = new JLabel ();
		marketRateLabel_ = new JLabel ();
		products_ = new JComboBox ();
		filler1_ = new JLabel ();
		filller2_ = new JLabel ();
		currentDate_ = new JLabel ();
		spacer5_ = new JLabel ();
		getContentPane ().setLayout (new GridBagLayout());
		GridBagConstraints gridBagConstraints1;

		setTitle ("OpenJMS FX Demo");
		setResizable(false);
		commands_.setLayout (new BorderLayout (0, 2));
		commands_.add (separator_, BorderLayout.NORTH);
		panel1_.setLayout (new FlowLayout (1, 70, 10));
        commit_.setText ("Commit");
        panel1_.add (commit_);
        cancel_.setText ("Clear");
        panel1_.add (cancel_);
		commands_.add (panel1_, BorderLayout.SOUTH);
		gridBagConstraints1 = new GridBagConstraints ();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 6;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		getContentPane ().add (commands_, gridBagConstraints1);
		amounts_.setLayout (new GridBagLayout ());
		GridBagConstraints gridBagConstraints2;

		products_.addItem("Spot              ");
		products_.addItem("Outright Forward  ");
		products_.addItem("Forward Swap      ");
		currency1_.addItem("USD");
		currency2_.addItem("AUD");
		currency2_.addItem("EUR");
		currency2_.addItem("FRF");
		currency2_.addItem("GBP");
		currency2_.addItem("JPY");
		gridBagConstraints2 = new GridBagConstraints();

		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.ipady = 6;
		gridBagConstraints2.insets = new Insets (0, 9, 0, 0);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		amounts_.add(date_, gridBagConstraints2);
		currencySelect_.setToolTipText("Select to toggle currency");
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new Insets (0, 11, 0, 11);
		amounts_.add (currencySelect_, gridBagConstraints2);
  
		buySellSelect_.setToolTipText ("Select to toggle buy/sell");
  
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new Insets (0, 11, 0, 11);
		amounts_.add (buySellSelect_, gridBagConstraints2);
  		amount1_.setToolTipText ("Enter Currency 1 Amount");
		amount1_.setBorder (new EtchedBorder());
		amount1_.setColumns (10);
		amount1_.setMinimumSize (new Dimension(8, 20));
		amountValidator_ = new AmountValidator(amount1_);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.ipady = 6;
		gridBagConstraints2.insets = new Insets (5, 0, 6, 14);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		amounts_.add (amount1_, gridBagConstraints2);
  		amount2_.setBorder (new EtchedBorder());
		amount2_.setEditable (false);
		amount2_.setColumns (10);
		amount2_.setMinimumSize (new Dimension(8, 20));
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 3;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.ipady = 6;
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		amounts_.add (amount2_, gridBagConstraints2);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.insets = new Insets (8, 0, 8, 0);
		amounts_.add (currency2Label_, gridBagConstraints2);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 2;
		gridBagConstraints2.gridy = 1;
		amounts_.add (buySellLabel_, gridBagConstraints2);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 0;
		amounts_.add (spacer3_, gridBagConstraints2);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.ipady = 26;
		amounts_.add (spacer4_, gridBagConstraints2);
		spotRateField_.setToolTipText ("Enter the current spot rate");
		spotRateField_.setColumns (9);
		gridBagConstraints2 = new GridBagConstraints ();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 3;
		gridBagConstraints2.ipady = 6;
		gridBagConstraints2.insets = new Insets (0, 9, 12, 0);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		amounts_.add (spotRateField_, gridBagConstraints2);
		gridBagConstraints1 = new GridBagConstraints ();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 3;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		getContentPane ().add (amounts_, gridBagConstraints1);
		currencySelection_.setLayout (new GridBagLayout ());
		GridBagConstraints gridBagConstraints3;

		currency1_.setPreferredSize (new Dimension(60, 25));
		currency1_.setMinimumSize (new Dimension(60, 25));
		currency1_.setToolTipText ("Select currency 1");
		currency1_.setMaximumSize (new Dimension(60, 25));
		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 3;
		gridBagConstraints3.insets = new Insets (0, 9, 0, 9);
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		currencySelection_.add (currency1_, gridBagConstraints3);
		currency2_.setPreferredSize (new Dimension(60, 25));
		currency2_.setMinimumSize (new Dimension(60, 25));
		currency2_.setToolTipText ("Select currency 2");
		currency2_.setMaximumSize (new Dimension(60, 25));
		
		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 3;
		gridBagConstraints3.insets = new Insets (0, 83, 0, 0);
		gridBagConstraints3.anchor = GridBagConstraints.EAST;
		currencySelection_.add (currency2_, gridBagConstraints3);
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 3;
		currencySelection_.add (marketRate_, gridBagConstraints3);
  		marketRateLabel_.setText ("Market Rate");
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 2;
		currencySelection_.add (marketRateLabel_, gridBagConstraints3);
  		products_.setBorder (new TitledBorder("Selected Product"));
		products_.setToolTipText ("Select the currency product");
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 1;
		currencySelection_.add (products_, gridBagConstraints3);
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.ipady = 22;
		currencySelection_.add (filler1_, gridBagConstraints3);
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 4;
		gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints3.ipady = 24;
		gridBagConstraints3.anchor = GridBagConstraints.SOUTH;
		currencySelection_.add (filller2_, gridBagConstraints3);
		date_.setToolTipText("Enter date");
		dateValidator_ = new DateValidator(date_);
		currentDate_.setText(dateValidator_.getToday());
		currentDate_.setToolTipText ("Todays Date");
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 0;
		currencySelection_.add (currentDate_, gridBagConstraints3);
  		gridBagConstraints3 = new GridBagConstraints ();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.insets = new Insets (0, 79, 0, 79);
		currencySelection_.add (spacer5_, gridBagConstraints3);
  		gridBagConstraints1 = new GridBagConstraints ();
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		getContentPane().add(currencySelection_, gridBagConstraints1);
		commit_.setEnabled(false);
		setUpCallbacks();

	}

	/**
	 * Set up all required callbacks for components.
	 *
	 */
	private void setUpCallbacks()
	{
		addWindowListener (new WindowAdapter () {
				public void windowClosing (WindowEvent evt) {
					exitForm (evt);
				}
			}
						   );

		products_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) 
				{
					String prd = (String)products_.getSelectedItem();
					
					if (!prd.equals("Spot              "))
					{
						JOptionPane.showMessageDialog
							(commands_, prd + " Not implemented yet", 
							 "Product Not Implemented",
							 JOptionPane.ERROR_MESSAGE);
						products_.setSelectedItem("Spot              ");
					}
				}
			}
									 );

		currency2_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) 
				{
					dealController_.setCurrencies
						("USD", (String)currency2_.getSelectedItem());
				}
			}
									 );

		cancel_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) 
				{
					// Reset the currencies, will clear the screen to its
					// default values.
					dealController_.setCurrencies
						((String)currency1_.getSelectedItem(),
						 (String)currency2_.getSelectedItem());
				}
			}
									 );


		commit_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) 
				{
					// Commit the deal.
					if (dealController_.commit())
					{
						// Reset the currencies, will clear the screen to its
						// default values.
						dealController_.setCurrencies
							((String)currency1_.getSelectedItem(),
							 (String)currency2_.getSelectedItem());
					}
					else
					{
						JOptionPane.showMessageDialog
							(commands_, "Failed to publish deal", 
							 "Publish Error",
							 JOptionPane.ERROR_MESSAGE);
					}
				}
			}
									 );

		currencySelector_ = new CurrencySelector
			(currencySelect_, currency2Label_);
		buySellSelector_ = new BuySellSelector
			(buySellSelect_, buySellLabel_);
		dealController_ = new DealController(this, factory_);
		dealController_.setDateValidator(dateValidator_);
		dealController_.setAmountValidator(amountValidator_);
		dealController_.setRateValidator(new RateValidator(spotRateField_));
		dealController_.setCurrencySelector(currencySelector_);
		dealController_.setBuySellSelector(buySellSelector_);
		dealController_.setCurrencies
			("USD", (String)currency2_.getSelectedItem());
	}
	

	public void setAmount2(String st)
	{
		amount2_.setText(st);
	}
	
	public void setMarketrate(String st)
	{
		marketRate_.setText(st);
	}
	
	public void reset()
	{
		amount2_.setText("");
	}


	public void canCommit(boolean flag)
	{
		commit_.setEnabled(flag);
	}
	

	/**
	 * Exit the application.
	 *
	 */
	private void exitForm(WindowEvent evt) 
	{
		System.exit (0);
	}

	/**
     * Print out information on running this sevice
     */
    static protected void usage()
    {
		java.io.PrintStream out = System.out;

		out.println("\n\n");
		out.println("====================================================================");
		out.println("Usage information for openjms.examples.client.fx.dealcapture.DealCapture");
		out.println("====================================================================");
		out.println("openjms.examples.client.fx.dealcapture.DealCapture\n");
		out.println("    [-help | -mode <rmi/ipc> \n");
		out.println("\t-help   displays this screen\n");
		out.println("\t-mode    connect in either ipc mode or rmi mode.\n");
    }
	



	/**
	 * @param args the command line arguments
	 */
	public static void main (String args[]) 
	{
		CommandLine cmdline = new CommandLine(args);
		if (cmdline.exists("help")) {
			// the help option has been speifie, print the usage information
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
		new DealCapture ().show ();
	}


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private JPanel					commands_;
	private JSeparator				separator_;
	private JPanel					panel1_;
	private JButton					commit_;
	private JButton					cancel_;
	private JPanel					amounts_;
	private JTextField				date_;
	private JButton					currencySelect_;
	private JButton					buySellSelect_;
	private JTextField				amount1_;
	private JTextField				amount2_;
	private JLabel					currency2Label_;
	private JLabel					buySellLabel_;
	private JLabel					spacer3_;
	private JLabel					spacer4_;
	private JTextField				spotRateField_;
	private JPanel					currencySelection_;
	private JComboBox				currency1_;
	private JComboBox				currency2_;
	private JLabel					marketRate_;
	private JLabel					marketRateLabel_;
	private JComboBox				products_;
	private JLabel					filler1_;
	private JLabel					filller2_;
	private JLabel					currentDate_;
	private JLabel					spacer5_;
	private CurrencySelector		currencySelector_;
	private BuySellSelector			buySellSelector_;
	private DateValidator			dateValidator_;
	private DealController			dealController_;
	private AmountValidator			amountValidator_;
    private static TopicConnectionFactory	factory_ = null;
    final static private String ipcMode_ = "org.exolab.jms.jndi.mipc.IpcJndiInitialContextFactory";
	final static private String rmiMode_ = "org.exolab.jms.jndi.rmi.RmiJndiInitialContextFactory";
	
} // End DealCapture
