@ECHO OFF
IF "%OS%" == "Windows_NT" setlocal

set SCRIPT_NAME=digest.bat
set EXECUTABLE_CLASS=org.jasypt.intf.cli.JasyptStringDigestCLI
set CURRENT_DIR=%cd%
set EXEC_CLASSPATH=.;"%CURRENT_DIR%\jasypt-cli-bundle.jar";"%JASYPT_CLASSPATH%"

set JAVA_EXECUTABLE=java
if "%JAVA_HOME%" == "" goto execute
set JAVA_EXECUTABLE="%JAVA_HOME%\bin\java"

:execute
%JAVA_EXECUTABLE% -classpath %EXEC_CLASSPATH% %EXECUTABLE_CLASS% %SCRIPT_NAME% %*

