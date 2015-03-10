package com.kicinger.openjms;

import org.exolab.jms.jndi.InitialContextFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by Krzysztof Kicinger on 2015-03-10.
 */
public class OpenJMSUtil {

    public static Context createContext(final String providerUrl) throws NamingException {
        return new InitialContext(new Properties() {{
            put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());
            put(Context.PROVIDER_URL, providerUrl);
        }});
    }

    public static Context createContext(final String providerUrl, final String initialContextFactory) throws NamingException {
        return new InitialContext(new Properties() {{
            put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            put(Context.PROVIDER_URL, providerUrl);
        }});
    }

/*    public static QueueConnectionFactory createQueueConnectionFactory(Context context, final String queueConnectionFactoryName) throws NamingException {
        return (QueueConnectionFactory) context.lookup(queueConnectionFactoryName);
    }

    public static TopicConnectionFactory createTopicConnectionFactory(Context context, final String topicConnectionFactoryName) throws NamingException {
        return (TopicConnectionFactory) context.lookup(topicConnectionFactoryName);
    }

    public static QueueConnection createQueueConnection(QueueConnectionFactory qcf) throws JMSException {
        return qcf.createQueueConnection();
    }

    public static TopicConnection createTopicConnection(TopicConnectionFactory tcf) throws JMSException {
        return tcf.createTopicConnection();
    }*/
}
