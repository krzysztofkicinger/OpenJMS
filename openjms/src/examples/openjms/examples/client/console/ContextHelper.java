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
 * Copyright 2003 (C) Exoffice Technologies Inc. All Rights Reserved.
 *
 * $Id: ContextHelper.java,v 1.2 2003/03/27 10:28:02 jalateras Exp $
 */
package openjms.examples.client.console;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.exolab.jms.jndi.InitialContextFactory;
import org.exolab.jms.jndi.JndiConstants;
import org.exolab.jms.util.CommandLine;


/**
 * Helper class to parse a command line for JNDI properties and construct
 * an initial context
 *
 * @version     $Revision: 1.2 $ $Date: 2003/03/27 10:28:02 $
 * @author      <a href="mailto:tima@intalio.com">Tim Anderson</a>
 */
class ContextHelper {

    public static Context getContext(CommandLine commands) 
        throws NamingException{

        Hashtable properties = new Hashtable();

        properties.put(
            Context.INITIAL_CONTEXT_FACTORY, 
            InitialContextFactory.class.getName());

        String scheme = commands.value("mode", "rmi");
        String host = commands.value("jndihost", "localhost");
        String port = "";

        if (scheme.equals("tcp") || scheme.equals("tcps")) {
            port = commands.value("jndiport", "3035");
        } else if (scheme.equals("http")) {
            port = commands.value("jndiport", "8080");
            // NOTE: in order for a client to use proxies, the following
            // properties must also be set using System.setProperty():
            // . http.proxyHost
            // . http.proxyPort

            // Client URL to allow server to asynchronously send messages
            // to registered consumers/receivers
            if (commands.exists("url")) {
                System.setProperty(JndiConstants.HTTP_CLIENT_URL_PROPERTY, 
                                   commands.value("url"));
            }
        } else if (scheme.equals("https")) {
            port = commands.value("jndiport", "8443");

            // NOTE: in order for a client to use proxies, the following
            // properties must also be set using System.setProperty():
            // . https.proxyHost
            // . https.proxyPort

            // Client URL to allow server to asynchronously send messages
            // to registered consumers/receivers
            if (commands.exists("url")) {
                System.setProperty(JndiConstants.HTTP_CLIENT_URL_PROPERTY, 
                                   commands.value("url"));
            }
        } else if (scheme.equals("rmi")) {
            port = commands.value("jndiport", "1099");
        }

        String name = "";
        if (scheme.equals("rmi")) {
             name = commands.value("jndiname", "");
        }

        String url = scheme + "://" + host + ":" + port + "/" + name;

        properties.put(Context.PROVIDER_URL, url);
        return new InitialContext(properties);
    }

} //-- ContextHelper
