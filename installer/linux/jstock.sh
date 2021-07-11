#!/bin/bash
#
# Normally, editing this script should not be required.
# Only case is to set up JAVA_HOME if it's not already defined.
#
# To specify an alternative JVM, edit and uncomment the following 
# line and change the path accordingly.
#JAVA_HOME=/usr/lib/java

if [ -n $JAVA_HOME ] ; then
_JAVA_EXEC=`type -p java`
else
_JAVA_EXEC="$JAVA_HOME/java"
fi

if [ ! -e "$_JAVA_EXEC" ] ; then
echo "Error: No Java Runtime Environment found"
echo "Please set the environment variable JAVA_HOME to the root directory of your SUN Java installation, e.g. by editing the 7th line in this launcher script."
exit 1
fi

if [ ! -x "$_JAVA_EXEC" ] ; then
echo "Warning: $_JAVA_EXEC is not executable"
exit 1
fi

#
# Resolve the location of the JStock installation.
# This includes resolving any symlinks.
PRG=$0
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '^.*-> \(.*\)$' 2>/dev/null`
    if expr "$link" : '^/' 2> /dev/null >/dev/null; then
        PRG="$link"
    else
        PRG="`dirname "$PRG"`/$link"
    fi
done

JSTOCK_BIN=`dirname "$PRG"`
cd "${JSTOCK_BIN}"

_VMOPTIONS="-Xms64m -Xmx512m"

_JAVAFXPATH="/usr/share/openjfx/lib"
if [[ (-d "$_JAVAFXPATH") && (-f "$_JAVAFXPATH/javafx.base.jar") ]] ; then
    _JAVAFXOPTIONS="--module-path $_JAVAFXPATH --add-modules=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web"
else
    _JAVAFXOPTIONS=""
fi

$_JAVA_EXEC $_VMOPTIONS $_JAVAFXOPTIONS -jar jstock.jar
