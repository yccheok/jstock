JStock
=====
JStock is a free stock market software for 26 countries. It provides Stock watchlist, Intraday stock price snapshot, Stock indicator editor, Stock indicator scanner and Portfolio management. Free SMS/email alert supported. It integrates seamless with Android. JStock Android can be downloaded separately from http://goo.gl/t24hN

JStock long term product roadmap can be found from http://goo.gl/55gGQS and http://goo.gl/ClN7zT

[![Gitter chat](https://badges.gitter.im/yccheok/client-app.png)](https://gitter.im/yccheok/jstock)

Development & Building
----
JStock is a Netbeans project.

0. Download or checkout jstock - https://github.com/yccheok/jstock
1. Install Netbans - https://netbeans.org/downloads
2. Open Netbeans and from the File Menu choose 'Open Project' and select the directory you checkedout
3. JStock does not include Junit - this needs to be added manually:
 3. Download the latest from [http://sourceforge.net/projects/junit/](http://sourceforge.net/projects/junit/ ) to jstock/libs
 3. Netbeans > Tools > Libraries, New Library named "JUnit" and is a "Class Library", Add Jar/Folder... and select the junit.jar file, hit OK.
4. JStock does not include Junit this needs to be added manually:
 4. JavaFX has been bundled with the JDK since 7u6.  Locate the jfxrt.jar under $JAVA_HOME and copy it into jstock/libs.
 4. Netbeans > Tools > Libraries, New Library named "javafx" and is a "Class Library", Add Jar/Folder... and select the jfxrt.jar file, hit OK.
4. Netbeans > Run > Clean and Build Project to build the file dist/config/jstock.jar and this can be run from the command-line independent of Netbeans:

    `java -jar dist/config/jstock.jar`

5. Alternatively to run directly in Netbeans - `Netbeans > Run > Run`
