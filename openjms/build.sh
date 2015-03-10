#!/bin/sh
# $Id: build.sh,v 1.31 2003/08/11 19:03:05 tanderson Exp $

#
# Set up the environment
#
if [ -z "$JAVA_HOME" ] ; then
  JAVA=`which java`
  if [ -z "$JAVA" ] ; then
    echo "Cannot find JAVA. Please set your PATH."
    exit 1
  fi
  JAVA_BIN=`dirname $JAVA`
  JAVA_HOME=$JAVA_BIN/..
fi

JAVA=$JAVA_HOME/bin/java

#
# These are the libraries required to run ant and its associated tasks.
# Use build.xml to specify the libraries required to build
#
CP=$JAVA_HOME/lib/tools.jar

CP=lib/ant-1.5.3-1.jar:$CP
CP=lib/ant-optional-1.5.3-1.jar:$CP
CP=lib/junit-3.7.jar:$CP
CP=lib/xalan-2.4.1.jar:$CP
CP=lib/xerces-2.3.0.jar:$CP
CP=lib/xml-apis-1.0.b2.jar:$CP

#
# Execute the build tool passing the build.xml file
#
echo $CP
$JAVA -cp $CP -Dant.home=lib org.apache.tools.ant.Main "$@"
