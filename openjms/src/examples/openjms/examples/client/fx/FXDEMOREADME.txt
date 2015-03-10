
The OpenJMS FX demo consists of three components, rundealmanager (DealManager), 
rundealcapture (DealCapture) and rundealblotter (DealBlotter). All three scripts take an additional 
argument –mode ipc. Use this mode if the OpenJMS server is running in TCP connectivity mode, 
i.e. started by the admin console using admin –config ipc_jms.xml. Refer to the Admin Tool manual 
and the Quick Start Guide for further details

DealCapture
-----------

DealCapture is the input Gui, which allows users to enter a deal in any of the supported
currencies. The Deal cannot be committed unless an amount, rate and date are entered. Date defaults
to today, and rate defaults to a fixed rate hardcoded in the demo. Both date and rate can be 
changed, but cannot be empty. Amount initially has no value, and must always be filled in. The
amount field accepts only numbers but recognizes three characters, 'm' (millions), 't' (thousands), 
'h' hundreds. When any of these three characters are entered the amount is multiplied by 1,000,000
or 1,000, or 100 appropriately.

Once enough data has been entered to make the deal committable, the 'Deal' button becomes sensitive
and the deal can be done. Once successfully committed the DealCapture resets itself to defaults.

Only Spot deals are currently implemented.

All rates are relative to the USD dollar, that is 1 USD = Rate * Currency2.

Only deals against USD are supported in this demo.

The deal is published persistently under the topic OpenJMSDemo.deals



DealManager
-----------

All deals committed by the DealCapture are received by the DealManager. DealManager acts as a 
repository, but has no persistency mechanism in this example. It receives all messages when it is 
up, and re-publishes them based on the currency pair selected under a non-persistent topic name
as "OpenJMSDemo.USD-C2" where C2 is the second currency selected (currency 1 is always USD) in this
demo. 

If DealManager is not running, all deals are queued by OpenJMSServer, and passed to the 
DealsManager when it does start up. Only deals not acknowledged by the DealManager are sent.
Acknowledged deals are not re-sent, unless the DealsManager persistent name is removed from
the OpenJMS persistency store via the admin tool. Then all messages will be re-sent again
when the DealsManager is re-started.

DealBlotter
-----------

DealBlotter is the GUI responsible for display all deals done via the DealCapture based on book
name. It will only display deals whose book name contains the two currencies the Deal was done in.

DealBlotter only receives reliable deals. So if it's not up all deals sent to it are never received.


Usage
-----

Run build preparetest to create the test directory and copy all appropriate config files required.

Start OpenJMSServer by running the admin GUI. Once the admin tool has started select 
"Actions/Start OpenJMSServer". Once the OpenJMSServer is running select 
"Actions/Connections/Online" to connect to the OpenJMSServer. Once connected a single node
can be seen in the display with title "OpenJMSServer". Select this node and right mouse click, 
then left mouse click on the "Add Queue/Topic" menu that pops up. Enter "OpenJMSDemo-deals"
as the queue/topic and press enter or select the OK button. A new node with the entered name
appears under the "OpenJMSServer" node.

Note: Topic names are case sensitive. The given queue/topic "OpenJMSDemo-deals", must be in the 
correct case for the demo to work.

Now start the demon, by either running "runfxdemo" which runs all the components, or run each of
components independently. 

Start entering deals into the DealCapture. Deals committed by the DealCapture appear as a row in the 
DealBlotter. Ensure that the book selected by the DealBlotter contains the currency pair used by
the DealCapture.

The demo is a simple demonstration of the usage of durable and reliable usage of OpenJMS topic 
publish/subscribe.


