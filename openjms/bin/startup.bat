@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenJMS server.
rem
rem $Id: stub.bat,v 1.3 2003/08/07 21:18:41 tanderson Exp $
rem ---------------------------------------------------------------------------

if "%OS%" == "Windows_NT" setlocal

rem Guess OPENJMS_HOME if not defined
if not "%OPENJMS_HOME%" == "" goto gotHome
set OPENJMS_HOME=.
if exist "%OPENJMS_HOME%\bin\openjms.bat" goto okHome
set OPENJMS_HOME=..
:gotHome
if exist "%OPENJMS_HOME%\bin\openjms.bat" goto okHome
echo The OPENJMS_HOME variable is not set correctly.
echo This is required to start the OpenJMS server.
goto end
:okHome

set EXECUTABLE=%OPENJMS_HOME%\bin\openjms.bat

rem Check that target executable exists
if exist "%EXECUTABLE%" goto okExec
echo Cannot find %EXECUTABLE%
echo This file is needed to start the OpenJMS server.
goto end
:okExec

rem Get remaining unshifted command line arguments and save them
set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

call "%EXECUTABLE%" start %CMD_LINE_ARGS%

:end

