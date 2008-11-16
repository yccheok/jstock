java -cp C:\saxon6-5-5\saxon.jar com.icl.saxon.StyleSheet jstockhelp.xml C:\docbook-xsl-1.73.0\javahelp\javahelp.xsl
sleep 2
C:\jh2.0\javahelp\bin\jhindexer .
del jstockhelp.jar
jar cvf jstockhelp.jar *
java -jar C:\jh2.0\demos\bin\hsviewer.jar -helpset C:\Projects\jstock\help\jhelpset.hs