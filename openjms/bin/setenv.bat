rem ---------------------------------------------------------------------------
rem Sample environment script for OpenJMS
rem
rem This is invoked by openjms.bat to configure:
rem . the CLASSPATH, for JDBC drivers
rem . JVM options
rem . JSSE, when using the TCPS connector
rem ---------------------------------------------------------------------------

rem Configure the JDBC driver
rem
rem    set CLASSPATH=<insert path to JDBC driver here>

rem Configure JVM options
rem
rem    set JAVA_OPTS=-Xmx256m

rem Configure JSSE
rem
rem  The following line is optional, and is only required if JDK 1.2 or 
rem  JDK 1.3 is being used, and JSSE isn't installed as an extension
rem
rem    set JSSE_HOME=<insert JSSE directory path here>
rem
rem  Configure the keystore. Required when using the TCPS connector
rem
rem    set JAVA_OPTS=%JAVA_OPTS% -Djavax.net.ssl.trustStore=%OPENJMS_HOME%\config\openjms.keystore
rem    set JAVA_OPTS=%JAVA_OPTS% -Djavax.net.ssl.keyStore=%OPENJMS_HOME%\config\openjms.keystore
rem    set JAVA_OPTS=%JAVA_OPTS% -Djavax.net.ssl.keyStorePassword=openjms
