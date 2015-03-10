@echo off

if not %JAVA_HOME%x==x goto set1

echo JAVA_HOME not set
exit 1

:set1

set JAVA=%JAVA_HOME%\bin\java

if not %OPENJMS_HOME%x==x goto set2

rem OPENJMS_HOME environment variable not set, so default to the parent dir
set OPENJMS_HOME=..

:set2

set POLICY_FILE=%OPENJMS_HOME%\config\openjms.policy

set CP=%OPENJMS_HOME%\build\examples
set CP=%OPENJMS_HOME%\lib\openjms-client-0.7.6.1.jar;%CP%
set CP=%OPENJMS_HOME%\lib\jta-1.0.1.jar;%CP%

%JAVA% -classpath "%CP%" -Djava.security.manager -Djava.security.policy="%POLICY_FILE%" openjms.examples.%*%
