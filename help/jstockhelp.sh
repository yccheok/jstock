#! /bin/bash

export JAVA_HOME=/home/yccheok/jdk1.6.0_01
export PATH=${JAVA_HOME}/bin:"${PATH}"
java -cp ~/saxon/saxon.jar com.icl.saxon.StyleSheet jstockhelp.xml /home/yccheok/docbook-xsl-1.73.0/javahelp/javahelp.xsl
sleep 1
~/jh2.0/javahelp/bin/jhindexer .
sleep 1
~/jh2.0/javahelp/bin/jhindexer .
rm jstockhelp.jar
jar cvf jstockhelp.jar *
java -jar ~/jh2.0/demos/bin/hsviewer.jar -helpset ~/Projects/jstock/help/jhelpset.hs

