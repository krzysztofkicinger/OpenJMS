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
 * $Id: DealBlotter.java,v 1.10 2002/02/19 09:09:47 mourikis Exp $
 *
 * Date         Author  Changes
 * $Date	    jimm    Created
 */

 
package openjms.examples.client.fx.dealblotter;

import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.AWTEvent;

import openjms.examples.client.fx.util.DateValidator;
import javax.jms.Session;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.ObjectMessage;
import javax.jms.TopicConnectionFactory;

import javax.naming.InitialContext;
import javax.naming.Context;

import org.exolab.jms.client.JmsTopicConnectionFactory;
import openjms.examples.client.fx.dealmanager.DealData;
import org.exolab.jms.util.CommandLine;


/**
 * The DealBlotter is a small demo to introduce the power of OpenJMS. Deals
 * created by the DealCapture are publish to the DealManger, which stores
 * them, and re-publishes them using the Book name as the topic.
 *
 * <P>This gui display the data from the dealCapture, in a simple table 
 * structure.
 *
 * @version     $Revision: 1.10 $ $Date: 2002/02/19 09:09:47 $
 * @author      <a href="mailto:mourikis@exolab.org">Jim Mourikis</a>
 * @see         openjms.examples.client.fx.dealblotter.DealTable
 * @see			openjms.examples.client.fx.dealcapture.DealCapture
 **/

public class DealBlotter extends JFrame implements MessageListener
{

	/**
	 * Initialise all the gui components.
	 *
	 */
	public DealBlotter() 
	{	
		initComponents();
		pack();
		setUpCallbacks();
	}

  /** 
   * Initialise the main form.
   *
   */
	private void initComponents () 
	{
		fileMenu_ = new JMenuBar();
		menu1_ = new JMenu();
		saveCommand_ = new JMenuItem();
		exitCommand_ = new JMenuItem();
		bookPanel_ = new JPanel();
		bookSelector_ = new JComboBox();
		dateField_ = new JTextField(6);
		dealTable_ = new DealTable();
		menu1_.setToolTipText("File menu options");
		menu1_.setText("File");
		menu1_.setMnemonic('F');
		saveCommand_.setToolTipText("Save the current configuration");
        saveCommand_.setText("Save");
        menu1_.add(saveCommand_);
        exitCommand_.setToolTipText("Exit the application");
        exitCommand_.setText("Exit");
        menu1_.add(exitCommand_);
		fileMenu_.add(menu1_);
		getContentPane().setLayout(new BorderLayout(5, 20));
		setTitle("OpenJMS DealBlotter Demo");
		bookPanel_.setLayout(new FlowLayout (0, 5, 5));
		bookSelector_.setBorder(new TitledBorder("Portfolio"));
		bookSelector_.setToolTipText("Select a portfolio to view");
		bookSelector_.addItem("USD-AUD-SPOT");
		bookSelector_.addItem("USD-JPY-SPOT");
		bookSelector_.addItem("USD-GBP-SPOT");
		bookSelector_.addItem("USD-EUR-SPOT");
		bookSelector_.addItem("USD-FRF-SPOT");
		bookPanel_.add(bookSelector_);
  		dateField_.setToolTipText("Enter date");
		dateField_.setDocument(new DateValidator(dateField_));
		bookPanel_.add(dateField_);
		getContentPane().add (bookPanel_, BorderLayout.NORTH);
		tablePanel_ = new JScrollPane(dealTable_);
		tablePanel_.setBorder(new TitledBorder("Deals"));
		getContentPane().add(tablePanel_, BorderLayout.CENTER);
		setJMenuBar(fileMenu_);
		setupSubscriber((String)bookSelector_.getSelectedItem());
	}


	/**
	 * Set up all required callbacks for components.
	 *
	 */
	private void setUpCallbacks()
	{
		saveCommand_.addActionListener(new ActionListener () 
			{
				public void actionPerformed (ActionEvent evt) 
				{
					save(evt);
				}
			}
									   );

		exitCommand_.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent evt) 
				{
					exit(evt);
				}			}
										);
    

		addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent evt) 
				{
					exit(evt);
				}
			}
						   );


		bookSelector_.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) 
				{
					setupSubscriber((String)bookSelector_.getSelectedItem());
				}
			}
									 );
  	}
	

	/**
	 * Validate the date for correctness.
	 *
	 */
	private void dateValidated(String date) 
	{
		
	}

	/**
	 * Save the current configuartion, and re-use it at start up.
	 *
	 */
	private void save(ActionEvent evt) 
	{

	}

	/**
	 * Exit the application.
	 *
	 */
	private void exit(AWTEvent evt) 
	{
		System.exit (0);
	}


	/**
	 * Set up the subscriber Topic based on the book name.
	 *
	 */
	private void setupSubscriber(String topicName)
	{
		try
		{
			if (subscriber_ != null)
			{
				dealTable_.removeDeals();
				subscriber_.close();
				session_.close();
			}
			else
			{
				connection_ = factory_.createTopicConnection();
			}
			
			session_ = connection_.createTopicSession
					(false, Session.CLIENT_ACKNOWLEDGE);
            connection_.start();
			Topic topic = session_.createTopic("OpenJMSDemo." + topicName);
			subscriber_ = session_.createSubscriber(topic);
			subscriber_.setMessageListener(this);
			System.err.println("Message listener OpenJMSDemo." + topicName
							   + " has been set");
		}
		catch(Exception err)
		{
			err.printStackTrace();
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
			Object[] tdata = new Object[dealTable_.getNumColumns()];
			int i = 0;
			
			tdata[i++] = data.getDealType();
			tdata[i++] = data.getValueDate();
			tdata[i++] = data.getTradingDate();
			tdata[i++] = data.getCurrency1();
			tdata[i++] = data.getBuySell1();
			tdata[i++] = data.getAmount1();
			tdata[i++] = data.getCurrency2();
			tdata[i++] = data.getBuySell2();
			tdata[i++] = data.getAmount2();
			tdata[i++] = data.getSpotRate();
			tdata[i++] = data.getMarketRate();
			dealTable_.addRow(tdata);
		}
		catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }


	/**
     * Print out information on running this sevice
     */
    static protected void usage()
    {
		java.io.PrintStream out = System.out;

		out.println("\n\n");
		out.println("====================================================================");
		out.println("Usage information for openjms.examples.client.fx.dealblotter.DealBlotter");
		out.println("====================================================================");
		out.println("openjms.examples.client.fx.dealblotter.DealBlotter\n");
		out.println("    [-help | -mode <rmi/ipc> \n");
		out.println("\t-help   displays this screen\n");
		out.println("\t-mode    connect in either ipc mode or rmi mode.\n");
    }
	


	/**
	 * Create the dealBlotter application, position on the right hand side 
	 * of the screen, give it a suitable starting size, and display it.
	 *
	 * @param args the command line arguments
	 *
	 */
	public static void main (String args[]) 
	{
		int width = 800;
		int height = 300;

		CommandLine cmdline = new CommandLine(args);
		if (cmdline.exists("help"))
		{
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
		DealBlotter dealBlotter = new DealBlotter();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		// rhs 1/3 down the screen.
		dealBlotter.setLocation(screen.width-width, screen.height/3);
		dealBlotter.setSize(width,height);
		dealBlotter.show();
	}


	// Swing gui objects.
	private JMenuBar		fileMenu_;
	private JMenu			menu1_;
	private JMenuItem		saveCommand_;
	private JMenuItem		exitCommand_;
	private JPanel			bookPanel_;
	private JComboBox		bookSelector_;
	private JTextField		dateField_;
	private JScrollPane		tablePanel_;
	private DealTable		dealTable_;


	private static TopicConnectionFactory	factory_ = null;
	private TopicConnection			connection_ = null;
	private TopicSession			session_ = null;
	private TopicSubscriber			subscriber_ = null;
	final static private String ipcMode_ = "org.exolab.jms.jndi.mipc.IpcJndiInitialContextFactory";
	final static private String rmiMode_ = "org.exolab.jms.jndi.rmi.RmiJndiInitialContextFactory";
	

} // End DealBlotter
