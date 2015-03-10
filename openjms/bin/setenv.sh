# ---------------------------------------------------------------------------
# Sample environment script for OpenJMS
#
# This is invoked by openjms.sh to configure:
# . the CLASSPATH, for JDBC drivers
# . JVM options
# . JSSE, when using the TCPS connector
# ---------------------------------------------------------------------------

# Configure the JDBC driver
#
#    CLASSPATH=<insert path to JDBC driver here>

# Configure JVM options
#
#    JAVA_OPTS=-Xmx256m

# Configure JSSE
#
#  The following line is optional, and is only required if JDK 1.2 or 
#  JDK 1.3 is being used, and JSSE isn't installed as an extension
#
#    JSSE_HOME=<insert JSSE directory path here>
#
#  Configure the keystore. Required when using the TCPS connector
#
#    JAVA_OPTS="$JAVA_OPTS \
#           -Djavax.net.ssl.trustStore=$OPENJMS_HOME/config/openjms.keystore \
#           -Djavax.net.ssl.keyStore=$OPENJMS_HOME/config/openjms.keystore \
#           -Djavax.net.ssl.keyStorePassword=openjms"
