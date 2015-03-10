@echo off
REM $Id: build.bat,v 1.32 2003/08/11 19:03:05 tanderson Exp $

if not %JAVA_HOME%x==x goto set

echo JAVA_HOME not set
exit 1

:set

set JAVA=%JAVA_HOME%\bin\java

rem These are the libraries required to run ant and its associated tasks.
rem Use build.xml to specify the libraries required to build

set CP=%JAVA_HOME%\lib\tools.jar

set CP=lib\ant-1.5.3-1.jar;%CP%
set CP=lib\ant-optional-1.5.3-1.jar;%CP%
set CP=lib\junit-3.7.jar;%CP%
set CP=lib\xalan-2.4.1.jar;%CP%
set CP=lib\xerces-2.3.0.jar;%CP%
set CP=lib\xml-apis-1.0.b2.jar;%CP%


echo %CP%
%JAVA% -classpath %CP% org.apache.tools.ant.Main %1 %2 %3 %4 %5 %6
